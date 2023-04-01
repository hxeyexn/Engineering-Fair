package com.cookandroid.project_energizor.view;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cookandroid.project_energizor.R;

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

//실종신고 페이지
public class ThirdActivity extends AppCompatActivity {

    private String Pk;
    private FirebaseAuth auth;

    Integer Pw;
    Boolean ReportFlag;
    String ReportTime;

    EditText Age, Location, Description;
    TextView UserName, Rrn, Phone, BeaconUUID, BeaconMAC, Nationality, Sex, Height, Weight, Blood, ProtectorName, ProtectorPhone;
    Button MissingReport;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 왼쪽 상단 버튼 만들기
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_back_24); //왼쪽 상단 버튼 아이콘 지정
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존 title 지우기

        auth = FirebaseAuth.getInstance();

        UserName = findViewById(R.id.mUserName);
        Rrn = findViewById(R.id.mRrn);
        Phone = findViewById(R.id.mPhone);
        BeaconUUID = findViewById(R.id.mBeaconUUID);
        BeaconMAC = findViewById(R.id.mBeaconMAC);
        Nationality = findViewById(R.id.mNationality);
        Sex = findViewById(R.id.mSex);
        Height = findViewById(R.id.mHeight);
        Weight = findViewById(R.id.mWeight);
        Blood = findViewById(R.id.mBlood);
        ProtectorName = findViewById(R.id.mProtectorName);
        ProtectorPhone = findViewById(R.id.mProtectorPhone);
        Age = findViewById(R.id.Age);
        Location = findViewById(R.id.Location);
        Description = findViewById(R.id.Description);
        MissingReport = findViewById(R.id.MissingReport);

        auth = FirebaseAuth.getInstance();

        String Id = auth.getCurrentUser().getEmail();
        readBeacon(Id);
        readUser(Id);
        readReport(Id);

        //실종신고하기 버튼 클릭 시
        MissingReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Age.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(getApplicationContext(), "실종자 나이를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else if(Location.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(getApplicationContext(), "실종장소를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else if(Description.getText().toString().replace(" ", "").equals("")) {
                    Toast.makeText(getApplicationContext(), "인상착의를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    //실종신고 시간
                    long now = System.currentTimeMillis();
                    Date mDate = new Date(now);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String ReportTime = simpleDate.format(mDate);

                    boolean ReportFlag = true;

                    //실종신고 접수
                    addBeacon(Pk, BeaconUUID.getText().toString(), BeaconMAC.getText().toString());

                    addReport(Pk, Id, Pw, ReportFlag, ReportTime);

                    addUser(Pk, UserName.getText().toString(), Rrn.getText().toString(), Phone.getText().toString(),
                            Nationality.getText().toString(), Sex.getText().toString(), Height.getText().toString(),
                            Weight.getText().toString(), Blood.getText().toString(), ProtectorName.getText().toString(),
                            ProtectorPhone.getText().toString(), Age.getText().toString(), Location.getText().toString(),
                            Description.getText().toString());

                    Toast.makeText(getApplicationContext(), "실종신고가 접수되었습니다.", Toast.LENGTH_SHORT).show();
                    UserName.setText("");
                    Rrn.setText("");
                    Phone.setText("");
                    BeaconUUID.setText("");
                    BeaconMAC.setText("");
                    Nationality.setText("");
                    Sex.setText("");
                    Blood.setText("");
                    Height.setText("");
                    Weight.setText("");
                    ProtectorName.setText("");
                    ProtectorPhone.setText("");
                    Age.setText("");
                    Location.setText("");
                    Description.setText("");
                }
            }
        });
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
                            BeaconMAC.setText(beacon.BeaconMAC);
                            BeaconUUID.setText(beacon.BeaconUUID);
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
                            UserName.setText(user.UserName);
                            Rrn.setText(user.Rrn);
                            Phone.setText(user.Phone);
                            Nationality.setText(user.Nationality);
                            Sex.setText(user.Sex);
                            Height.setText(user.Height);
                            Weight.setText(user.Weight);
                            Blood.setText(user.Blood);
                            ProtectorName.setText(user.ProtectorName);
                            ProtectorPhone.setText(user.ProtectorPhone);
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

    //비콘 정보 저장
    private void addBeacon(String Pk, String BeaconUUID, String BeaconMAC) {
        Beacon Beacon = new Beacon(BeaconUUID, BeaconMAC);
        Map<String, Object> beaconValues = Beacon.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/DataSet/" + Pk + "/Beacon/" , beaconValues);
        databaseReference.updateChildren(childUpdates);
    }

    //사용자 정보 저장
    private void addUser(String Pk, String UserName, String Rrn, String Phone, String Nationality, String Sex, String Height, String Weight, String Blood, String ProtectorName, String ProtectorPhone, String Age, String Position, String Description) {
        User User = new User(UserName, Rrn, Phone, Nationality, Sex, Height, Weight, Blood, ProtectorName, ProtectorPhone, Age, Position, Description);
        Map<String, Object> userValues = User.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/DataSet/" + Pk + "/ProtectorApp/" + "/PersonalInformation/" , userValues);
        databaseReference.updateChildren(childUpdates);
    }

    //실종신고 정보 저장
    private void addReport(String Pk, String Id, int PW, boolean LostWarningFlag, String LostWarningTime) {
        Report Report = new Report(Id, PW, LostWarningFlag, LostWarningTime);
        Map<String, Object> reportValues = Report.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/DataSet/" + Pk + "/ProtectorApp/", reportValues);
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
}
