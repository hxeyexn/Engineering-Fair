package com.cookandroid.project_energizor.view;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

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


public class NoticeFragment extends Fragment {

    private FirebaseAuth auth;
    ViewGroup rootView;
    ListView noticeList;
    NoticeAdapter noticeAdapter;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_notice, container, false);
        noticeList = rootView.findViewById(R.id.noticeList);

        auth = FirebaseAuth.getInstance();
        String id = auth.getCurrentUser().getEmail();
        readNoticeMsg(id);

        noticeAdapter = new NoticeAdapter();

        noticeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), NoticeContentActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    //NoticeMsg 상태 불러옴
    private void readNoticeMsg(String Id) {
        Query keyQuery = databaseReference.child("DataSet").orderByChild("ProtectorApp/Id").equalTo(Id);
        keyQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String Pk = childSnapshot.getKey();
                    Log.i("Pk", Pk);

                    Query noticeMsgQuery = databaseReference.child("/DataSet/").child(Pk).child("/NoticeMsg/");
                    noticeMsgQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean noticeMsg = dataSnapshot.getValue(Boolean.class);
                            Log.i("msg", String.valueOf(noticeMsg));

                            //noticeMsg가 true이면 알림 페이지에 실종자를 찾았다는 글 띄우기
                            if(noticeMsg == true) {
                                long now = System.currentTimeMillis();
                                Date mDate = new Date(now);
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                String Time = simpleDate.format(mDate);

                                noticeAdapter.addItem("실종자를 찾았습니다.", Time);
                                noticeList.setAdapter(noticeAdapter);
                            }
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
}
