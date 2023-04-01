package com.cookandroid.project_energizor.view;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.cookandroid.project_energizor.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

// 메인 메뉴 페이지
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentCallback {

    private FirebaseAuth auth;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Fragment mainFragment, infoFragment, noticeFragment;
    TextView tv_email;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //앱 켜짐 유지
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        auth = FirebaseAuth.getInstance();
        String id = auth.getCurrentUser().getEmail();

        readNoticeMsg(id);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 왼쪽 상단 버튼 만들기
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24); //왼쪽 상단 버튼 아이콘 지정

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(this);

        View navi_header = navigationView.getHeaderView(0);
        tv_email = navi_header.findViewById(R.id.tv_email);

        tv_email.setText(auth.getCurrentUser().getEmail());

        mainFragment = new MainFragment();
        infoFragment = new InfoFragment();
        noticeFragment = new NoticeFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.container, mainFragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // 왼쪽 상단 버튼 눌렀을 때
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void onFragmentSelected(int position, Bundle bundle) {
        Fragment curFragment = null;
        if (position == 0) {
            curFragment = mainFragment;
            toolbar.setTitle("홈");
        } else if (position == 1) {
            curFragment = infoFragment;
            toolbar.setTitle("내 정보");
        } else if (position == 2) {
            curFragment = noticeFragment;
            toolbar.setTitle("알림");
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container, curFragment).commit();
    }

    @Override
    public void onBackPressed() { //뒤로가기 했을 때
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // 바로가기에서 메뉴가 선택되었을 때 호출
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item_main) {
            Toast.makeText(this, "홈", Toast.LENGTH_SHORT).show();
            onFragmentSelected(0, null);
        } else if (id == R.id.item_info) {
            Toast.makeText(this, "내 정보", Toast.LENGTH_SHORT).show();
            onFragmentSelected(1, null);
        } else if (id == R.id.item_notice) {
            Toast.makeText(this, "알림", Toast.LENGTH_SHORT).show();
            onFragmentSelected(2, null);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //DB에서 NoticeMsg 불러와서 true면 알림 띄움
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

                            if(noticeMsg == true) {

                                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                NotificationCompat.Builder builder = null;

                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    String channelID = "channel_01";
                                    String channelName = "MyChannel01";

                                    NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                                    notificationManager.createNotificationChannel(channel);
                                    builder = new NotificationCompat.Builder(MainActivity.this, channelID);
                                } else {
                                    //builder = new NotificationCompat.Builder(MainActivity.this, null);
                                }

                                builder.setSmallIcon(R.drawable.icon).setContentTitle("Co-exist").setContentText("실종자를 찾았습니다.");
                                Bitmap bm= BitmapFactory.decodeResource(getResources(),R.drawable.icon);
                                builder.setLargeIcon(bm);//매개변수가 Bitmap을 줘야함

                                //알림창 클릭시 NoticeContent 실행
                                Intent intent = new Intent(getApplicationContext(), NoticeContentActivity.class);
                                //지금 실행하는 것이 아니라 잠시 보류시키는 Intent 객체 필요
                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                builder.setContentIntent(pendingIntent);

                                //알림창 클릭 시에 자동으로 알림제거
                                builder.setAutoCancel(true);

                                //건축가에게 알림 객체 생성하도록
                                Notification notification=builder.build();

                                //알림매니저에게 알림(Notify) 요청
                                notificationManager.notify(1, notification);

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

