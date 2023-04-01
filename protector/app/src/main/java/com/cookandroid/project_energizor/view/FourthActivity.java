package com.cookandroid.project_energizor.view;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import com.cookandroid.project_energizor.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

//실종자 찾기 페이지
public class FourthActivity extends AppCompatActivity {

    ListView listView;
    ListItemAdapter adapter;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 왼쪽 상단 버튼 만들기
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_back_24); //왼쪽 상단 버튼 아이콘 지정
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존 title 지우기

        listView = findViewById(R.id.listView);
        adapter = new ListItemAdapter();
        listView.setAdapter(adapter);

        Query missingListQuery = databaseReference.child("DataSet").orderByChild("ProtectorApp/LostWarningFlag").equalTo(true);
        missingListQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String Pk = childSnapshot.getKey();
                    Log.i("Pk", Pk);

                    Query userQuery = databaseReference.child("DataSet").child(Pk).child("ProtectorApp").child("PersonalInformation");
                    userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot childSnapshot) {
                            Map<String, String> User = (Map) childSnapshot.getValue();
                            String[] info = {User.get("UserName"), User.get("Sex"), String.valueOf(User.get("Age")), User.get("Position"), User.get("Description")};
                            Log.i("Info", Arrays.toString(info));

                            //실종자 정보를 불러와 list에 담음
                            adapter.addItem(new ListItem(User.get("UserName"), User.get("Sex"), String.valueOf(User.get("Age")), User.get("Position"), User.get("Description")));

                            adapter.notifyDataSetChanged();
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
}