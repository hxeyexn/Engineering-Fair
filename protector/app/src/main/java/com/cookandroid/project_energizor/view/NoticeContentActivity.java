package com.cookandroid.project_energizor.view;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothClass;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cookandroid.project_energizor.R;
import com.google.android.gms.tasks.NativeOnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//알림 내용 Activity
public class NoticeContentActivity extends AppCompatActivity {

    private String Pk;
    private FirebaseAuth auth;

    boolean NoticeMsg, ReportFlag;

    String BeaconMAC, BeaconUUID, ReportTime,
            UserName, Rrn, Phone, Nationality, Sex, Height, Weight, Blood, ProtectorName, ProtectorPhone, Age, Position, Description,
            DeviceNo, Time;
    Integer Pw;
    float latitude, longitude;

    TextView noticeDate;
    Button confirm_button;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_content);

        auth = FirebaseAuth.getInstance();
        String Id = auth.getCurrentUser().getEmail();

        readBeacon(Id);
        readUser(Id);
        readReport(Id);
        readTrafficLight(Id);

        noticeDate = findViewById(R.id.noticeDate);
        confirm_button = findViewById(R.id.confirm_button);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 왼쪽 상단 버튼 만들기
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_back_24); //왼쪽 상단 버튼 아이콘 지정
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존 title 지우기

        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String lTime = simpleDate.format(mDate);

        noticeDate.setText(lTime);

        //확인 버튼 눌렸을 때
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addNotice(Pk, NoticeMsg);

                addBeacon(Pk, BeaconUUID, BeaconMAC);

                addReport(Pk, Id, Pw, ReportFlag, ReportTime);

                addUser(Pk, UserName, Rrn, Phone, Nationality,
                        Sex, Height, Weight, Blood,
                        ProtectorName, ProtectorPhone, Age, Position, Description);

                addTrafficLight(Pk, Integer.parseInt(DeviceNo), latitude, longitude, Time);

                Toast.makeText(getApplicationContext(), "전송되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
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

    //비콘 정보 불러오기
    private void readBeacon(String Id) {
        Query keyQuery = databaseReference.child("DataSet").orderByChild("ProtectorApp/Id").equalTo(Id);
        keyQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Pk = childSnapshot.getKey();
                    Log.i("Pk", Pk);

                    Query beaconQuery = databaseReference.child("/DataSet/").child(Pk).child("/Beacon/");
                    beaconQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Beacon beacon = dataSnapshot.getValue(Beacon.class);
                            BeaconMAC = beacon.BeaconMAC;
                            BeaconUUID = beacon.BeaconUUID;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //실종 정보 불러오기
    private void readReport(String Id) {
        Query keyQuery = databaseReference.child("DataSet").orderByChild("ProtectorApp/Id").equalTo(Id);
        keyQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Pk = childSnapshot.getKey();
                    Log.i("Pk", Pk);

                    Query reportQuery = databaseReference.child("DataSet").child(Pk).child("ProtectorApp");
                    reportQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Report report = dataSnapshot.getValue(Report.class);
                            Pw = report.PW;
                            ReportFlag = report.LostWarningFlag;
                            ReportTime = report.LostWarningTime;

                            Log.i("Pw", String.valueOf(Pw));
                            Log.i("Flag", String.valueOf(ReportFlag));
                            Log.i("Time", String.valueOf(ReportTime));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //사용자 정보 불러오기
    private void readUser(String Id) {
        Query keyQuery = databaseReference.child("DataSet").orderByChild("ProtectorApp/Id").equalTo(Id);
        keyQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String Pk = childSnapshot.getKey();
                    Log.i("Pk", Pk);

                    Query userQuery = databaseReference.child("DataSet").child(Pk).child("ProtectorApp").child("PersonalInformation");
                    userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            UserName = user.UserName;
                            Rrn = user.Rrn;
                            Phone = user.Phone;
                            Nationality = user.Nationality;
                            Sex = user.Sex;
                            Height = user.Height;
                            Weight = user.Weight;
                            Blood = user.Blood;
                            ProtectorName = user.ProtectorName;
                            ProtectorPhone = user.ProtectorPhone;
                            Age = user.Age;
                            Position = user.Position;
                            Description = user.Description;

                            Log.i("UserName", String.valueOf(UserName));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //사용자 위치 정보 불러오기
    private void readTrafficLight(String Id) {
        Query keyQuery = databaseReference.child("DataSet").orderByChild("ProtectorApp/Id").equalTo(Id);
        keyQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Pk = childSnapshot.getKey();
                    Log.i("Pk", Pk);

                    Query beaconQuery = databaseReference.child("/DataSet/").child(Pk).child("TrafficLight/Scan");
                    beaconQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            Map<String, Object> TrafficLight = (Map) dataSnapshot.getValue();


//                            int No = (int) Integer.parseInt(String.valueOf(TrafficLight.get("DeviceNo")).trim());
//                            float lat = (float) Double.parseDouble(String.valueOf(TrafficLight.get("latitude")).trim());
//                            float lon = (float) Double.parseDouble(String.valueOf(TrafficLight.get("longitude")).trim());
//                            String Ti = (String) String.valueOf(TrafficLight.get("Time")).trim();
//
//                            Log.i("DeviceNo", String.valueOf(No));
//                            Log.i("latitude", String.valueOf(lat));
//                            Log.i("longitude", String.valueOf(lon));
//                            Log.i("Time", Ti);

                            TrafficLight trafficLight = dataSnapshot.getValue(TrafficLight.class);
                            DeviceNo = String.valueOf(trafficLight.DeviceNo);
                            latitude = trafficLight.latitude;
                            longitude = trafficLight.longitude;
                            Time = trafficLight.Time;

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

    private void addReport(String Pk, String Id, int PW, boolean LostWarningFlag, String LostWarningTime) {
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
}
