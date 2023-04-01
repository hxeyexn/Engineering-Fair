package com.cookandroid.project_energizor.view;

import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cookandroid.project_energizor.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

//사용자 위치 지도
public class SecondActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FirebaseAuth auth;
    private GoogleMap mMap;
    private Geocoder geoCoder;
    private String address;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 왼쪽 상단 버튼 만들기
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_back_24); //왼쪽 상단 버튼 아이콘 지정
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존 title 지우기


        auth = FirebaseAuth.getInstance();

        String id = auth.getCurrentUser().getEmail();
        readLocation(id);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    private void readLocation(String Id) {

        //사용자가 입력한 Id로 Pk값을 불러오기 위한 Ouery
        Query keyQuery = databaseReference.child("DataSet").orderByChild("ProtectorApp/Id").equalTo(Id);
        keyQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //참조위치의 자식노드들을 순차적으로 순회해서 값을 가져와 Pk에 넣어주는 반복문
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    //등록번호 가져옴
                    String Pk = childSnapshot.getKey();
                    Log.i("Pk", Pk);

                    //가져온 등록번호를 이용하여 위도, 경도 불러옴
                    Query locationQuery = databaseReference.child("/DataSet/").child(Pk).child("TrafficLight/Scan");
                    locationQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Map<String, Float> TrafficLight = (Map) dataSnapshot.getValue();

                            float lat = (float) Double.parseDouble(String.valueOf(TrafficLight.get("latitude")));
                            float lon = (float) Double.parseDouble(String.valueOf(TrafficLight.get("longitude")));

                            Log.i("lat", String.valueOf(lat));
                            Log.i("lon", String.valueOf(lon));

                            geoCoder = new Geocoder(getApplicationContext(), Locale.KOREA);

                            try {
                                address = geoCoder.getFromLocation(lat, lon, 1).get(0).getAddressLine(0);
                                Log.i("Address", address);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            LatLng Location = new LatLng(lat, lon);
                            MarkerOptions markerOptions = new MarkerOptions();         // 마커 생성
                            markerOptions.position(Location);
                            markerOptions.title("사용자 위치");      // 마커 제목
                            markerOptions.snippet(address);         // 마커 설명
                            mMap.addMarker(markerOptions);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Location, 15));
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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
    }
}
