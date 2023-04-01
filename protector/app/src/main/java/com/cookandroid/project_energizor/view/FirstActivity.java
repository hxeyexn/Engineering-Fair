package com.cookandroid.project_energizor.view;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cookandroid.project_energizor.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neovisionaries.bluetooth.ble.advertising.ADPayloadParser;
import com.neovisionaries.bluetooth.ble.advertising.ADStructure;
import com.neovisionaries.bluetooth.ble.advertising.IBeacon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//사용자 정보 등록 페이지
public class FirstActivity extends AppCompatActivity {

    //Firebase
    private FirebaseAuth auth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();
    EditText Id, Pw, UserName, Rrn, Phone, Height, Weight, ProtectorName, ProtectorPhone;
    Spinner Nationality, Sex, Blood;
    Button enroll;

    //db에 저장된 데이터의 수
    private long numofuser;

    //BLE
    private TextView BeaconUUID, BeaconMAC;
    private BluetoothAdapter bluetoothAdapter;
    private int REQUEST_ENABLE_BT = 1000;
    private static final long SCAN_PERIOD = 10000;
    public String SERVICE_STRING = "FFD15047-EF43-40BD-9D5F-18D758E79B7C";

    ArrayList<BluetoothDevice> arraylist = new ArrayList<>();
    ArrayList<String> arrUUID;

    //BLE_SCAN
    private Handler handler;
    private boolean mScanning;

    //UUID
    BluetoothLeScanner scanner;
    boolean leScanning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 왼쪽 상단 버튼 만들기
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_back_24); //왼쪽 상단 버튼 아이콘 지정
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존 title 지우기

        auth = FirebaseAuth.getInstance();

        arraylist = new ArrayList<>();
        arrUUID = new ArrayList<>();
        arrUUID.add("e2c56db5-dffb-48d2-b060-d0f5a71096e0");

        init();
        bleInit();
        bleSupportCheck();

        Id = findViewById(R.id.Id);
        UserName = findViewById(R.id.UserName);
        Rrn = findViewById(R.id.Rrn);
        Phone = findViewById(R.id.Phone);
        Nationality = findViewById(R.id.Nationality);
        Sex = findViewById(R.id.Sex);
        Height = findViewById(R.id.Height);
        Weight = findViewById(R.id.Weight);
        Blood = findViewById(R.id.Blood);
        ProtectorName = findViewById(R.id.ProtectorName);
        ProtectorPhone = findViewById(R.id.ProtectorPhone);
        Pw = findViewById(R.id.Pw);
        enroll = findViewById(R.id.enroll);

        Id.setText(auth.getCurrentUser().getEmail());

        @SuppressLint("ResourceType")
        ArrayAdapter<CharSequence> NationalityAdapter = ArrayAdapter.createFromResource(this, R.array.nationality_array, android.R.layout.simple_spinner_item);

        NationalityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Nationality.setAdapter(NationalityAdapter);

        @SuppressLint("ResourceType")
        ArrayAdapter<CharSequence> SexAdapter = ArrayAdapter.createFromResource(this, R.array.sex_array, android.R.layout.simple_spinner_item);

        SexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Sex.setAdapter(SexAdapter);

        @SuppressLint("ResourceType")
        ArrayAdapter<CharSequence> BloodAdapter = ArrayAdapter.createFromResource(this, R.array.blood_array, android.R.layout.simple_spinner_item);

        BloodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Blood.setAdapter(BloodAdapter);

        scanLeDevice(true);

        //firebase에 저장된 데이터 갯수 불러옴
        DatabaseReference count = databaseReference.child("/DataSet/");
        count.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                numofuser = dataSnapshot.getChildrenCount();
                Log.d("NumOfUser", Long.toString(numofuser));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        //사용자 정보 등록
        enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Id.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(getApplicationContext(), "아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else if(Pw.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요." , Toast.LENGTH_SHORT).show();
                }
                else if(UserName.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(getApplicationContext(), "비콘 소지자 이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else if(Rrn.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(getApplicationContext(), "주민등록번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else if(Phone.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(getApplicationContext(), "비콘 소지자 전화번호를 입력하세요", Toast.LENGTH_SHORT).show();
                }
                else if(BeaconUUID.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(getApplicationContext(), "비콘을 스캔해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(BeaconMAC.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(getApplicationContext(), "비콘을 스캔해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(Nationality.getSelectedItem().toString().equals("국적")) {
                    Toast.makeText(getApplicationContext(), "국적을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(Sex.getSelectedItem().toString().equals("성별")) {
                    Toast.makeText(getApplicationContext(), "성별을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(Height.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(getApplicationContext(), "신장을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else if(Weight.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(getApplicationContext(), "몸무게를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else if(Blood.getSelectedItem().toString().equals("혈액형")) {
                    Toast.makeText(getApplicationContext(), "혈액형을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(ProtectorName.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(getApplicationContext(), "보호자 이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else if(ProtectorPhone.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(getApplicationContext(), "보호자 전화번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    String Age = "실종신고 전";
                    String Position = "실종신고 전";
                    String Description = "실종신고 전";
                    boolean NoticeMsg = false;
                    boolean ReportFlag = false;
                    String ReportTime = "실종신고 전";
                    int DeviceNo = 0;
                    float latitude = 0;
                    float longitude = 0;
                    String Time = "비콘 스캔 전";
                    String Pk = String.valueOf(5000+numofuser+1);

                    addNotice(Pk, NoticeMsg);

                    addBeacon(Pk, BeaconUUID.getText().toString(), BeaconMAC.getText().toString());

                    addReport(Id.getText().toString(), Pk, Integer.parseInt(Pw.getText().toString()), ReportFlag, ReportTime);

                    addUser(Pk, UserName.getText().toString(), Rrn.getText().toString(), Phone.getText().toString(), Nationality.getSelectedItem().toString(),
                            Sex.getSelectedItem().toString(), Height.getText().toString(), Weight.getText().toString(), Blood.getSelectedItem().toString(),
                            ProtectorName.getText().toString(), ProtectorPhone.getText().toString(), Age, Position, Description);
                    addTrafficLight(Pk, DeviceNo, latitude, longitude, Time);

                    Toast.makeText(getApplicationContext(), "등록되었습니다.", Toast.LENGTH_SHORT).show();

                    Id.setText("");
                    Pw.setText("");
                    UserName.setText("");
                    Rrn.setText("");
                    Phone.setText("");
                    BeaconUUID.setText("");
                    BeaconMAC.setText("");
                    Nationality.setSelection(0);
                    Sex.setSelection(0);
                    Height.setText("");
                    Weight.setText("");
                    Blood.setSelection(0);
                    ProtectorName.setText("");
                    ProtectorPhone.setText("");
                }
            }
        });
    }

    private void addNotice(String Pk, boolean NoticeMsg) {
        com.cookandroid.project_energizor.view.NoticeMsg Notice = new NoticeMsg(NoticeMsg);
        Map<String, Object> noticeValues = Notice.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/DataSet/" + Pk , noticeValues);
        databaseReference.updateChildren(childUpdates);
    }

    private void addBeacon(String Pk, String BeaconUUID, String BeaconMAC) {
        Beacon Beacon = new Beacon(BeaconUUID, BeaconMAC);
        Map<String, Object> beaconValues = Beacon.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/DataSet/" + Pk + "/Beacon/" , beaconValues);
        databaseReference.updateChildren(childUpdates);
    }

    private void addReport(String Id, String Pk, int PW, boolean LostWarningFlag, String LostWarningTime) {
        Report Report = new Report(Id, PW, LostWarningFlag, LostWarningTime);
        Map<String, Object> reportValues = Report.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/DataSet/" + Pk + "/ProtectorApp/", reportValues);
        databaseReference.updateChildren(childUpdates);
    }

    private void addUser(String Pk, String UserName, String Rrn, String Phone, String Nationality, String Sex,
                         String Height, String Weight, String Blood, String ProtectorName,
                         String ProtectorPhone, String Age, String Position, String Description) {
        User User = new User(UserName, Rrn, Phone, Nationality, Sex, Height, Weight, Blood, ProtectorName, ProtectorPhone, Age, Position, Description);
        Map<String, Object> userValues = User.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/DataSet/" + Pk + "/ProtectorApp/" + "/PersonalInformation/" , userValues);
        databaseReference.updateChildren(childUpdates);
    }

    private void addTrafficLight(String Pk, int DeviceNo, float latitude, float longitude, String Time) {
        TrafficLight TrafficLight = new TrafficLight(DeviceNo, latitude, longitude, Time);
        Map<String, Object> trafficLightValues = TrafficLight.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/DataSet/" + Pk + "/TrafficLight/" + "/Scan/" , trafficLightValues);
        databaseReference.updateChildren(childUpdates);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // 왼쪽 상단 버튼 눌렀을 때
                //뒤로가기
                this.finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void init(){
        BeaconUUID = findViewById(R.id.BeaconUUID);
        BeaconMAC = findViewById(R.id.BeaconMAC);
        handler = new Handler();
        mScanning = true;
    }

    //블루투스
    //BLE SupportCheck
    private void bleSupportCheck(){
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    //BLE_init
    @SuppressLint("MissingPermission")
    private void bleInit(){
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        scanner = bluetoothAdapter.getBluetoothLeScanner();
        leScanning = true;
    }

    //BLE_SCAN
    @SuppressLint("MissingPermission")
    private void scanLeDevice(final boolean enable) {
        if(mScanning){
            bluetoothAdapter.startLeScan(leScanCallback);
            mScanning = false;
        }else{
            bluetoothAdapter.stopLeScan(leScanCallback);
            mScanning = true;
        }
    }


    final private BluetoothAdapter.LeScanCallback leScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    if (device.getName() != null) {
                        Log.i("BLE_UUID", getUUID(scanRecord));
                        Log.i("BLE_MAC", device.getAddress());
                            if (arrUUID.contains(getUUID(scanRecord))) {
                                if(!arraylist.contains(device)){
                                    arraylist.add(device);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            BeaconUUID.setText(getUUID(scanRecord));
                                            BeaconMAC.setText(device.getAddress());
                                        }
                                    });
                                }

                            }
                        }

                    }
                };

    private String getUUID(byte[] result){
        List<ADStructure> structures =
                ADPayloadParser.getInstance().parse(result);

        for (ADStructure structure : structures) {
            if (structure instanceof IBeacon) {
                IBeacon iBeacon = (IBeacon) structure;
                return iBeacon.getUUID().toString();
            }
        }
        return "";
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onDestroy() {
        super.onDestroy();
        //stop scan
        bluetoothAdapter.stopLeScan(leScanCallback);
    }
}


