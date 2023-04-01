package com.cookandroid.project_energizor.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cookandroid.project_energizor.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//회원가입
public class SignUpActivity extends AppCompatActivity {

    EditText signUp_Email, signUp_Pw, signUp_CheckPw;
    Button signUp_Btn;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 왼쪽 상단 버튼 만들기
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_back_24); //왼쪽 상단 버튼 아이콘 지정
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존 title 지우기

        signUp_Email = findViewById(R.id.signUp_Email);
        signUp_Pw = findViewById(R.id.signUp_Pw);
        signUp_CheckPw = findViewById(R.id.signUp_CheckPw);
        signUp_Btn = findViewById(R.id.signUp_Btn);

        firebaseAuth = FirebaseAuth.getInstance();

        signUp_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //가입 정보 가져오기
                //공백인 부분을 제거하고 보여주는 trim();
                String Email = signUp_Email.getText().toString().trim();
                String Pw = signUp_Pw.getText().toString().trim();
                String CheckPw = signUp_CheckPw.getText().toString().trim();

                if (Email.replace(" ", "").equals("")) {
                    Toast.makeText(getApplicationContext(), "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else if (Pw.replace(" ", "").equals("")) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else if (CheckPw.replace(" ", "").equals("")) {
                    Toast.makeText(getApplicationContext(), "비밀번호 확인을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(Pw.equals(CheckPw)) {
                        //가입 성공 시
                        firebaseAuth.createUserWithEmailAndPassword(Email, Pw)
                                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        //가입 성공시
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SignUpActivity.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();

                                        }
                                        else {
                                            //이미 가입된 계정일 때, 이메일 형식이 아닐 때, 비밀번호가 6자리가 아닐 때
                                            Toast.makeText(SignUpActivity.this, "이미 존재하는 계정입니다.", Toast.LENGTH_SHORT).show();

                                            return; //해당 메소드 진행을 멈추고 빠져나감.
                                        }
                                    }
                                });
                        //비밀번호 오류시
                    } else {
                        Toast.makeText(SignUpActivity.this, "비밀번호가 틀렸습니다. 다시 입력해 주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
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
