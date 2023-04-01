package com.cookandroid.project_energizor.view;

import android.content.Intent;
import android.os.Bundle;
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

//로그인 페이지
public class LoginActivity extends AppCompatActivity {
    private Button SignUp, Login;
    private EditText login_Email, login_Pw;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존 title 지우기

        SignUp = findViewById(R.id.SignUp);
        Login = findViewById(R.id.Login);
        login_Email = findViewById(R.id.login_Email);
        login_Pw = findViewById(R.id.login_Pw);
        firebaseAuth = firebaseAuth.getInstance();

        //로그인
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email = login_Email.getText().toString().trim();
                String Pw = login_Pw.getText().toString().trim();

                if (Email.replace(" ", "").equals("")) {
                    Toast.makeText(getApplicationContext(), "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else if (Pw.replace(" ", "").equals("")) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    //공백인 부분을 제거하고 보여주는 trim();
                    firebaseAuth.signInWithEmailAndPassword(Email, Pw)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        //이메일이 존재하지 않을 때, 이메일 형식이 아닐 때, 비밀번호가 6자리가 아닐 때
                                        Toast.makeText(LoginActivity.this, "가입되지 않은 계정입니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            });
                    }
            }

        });

        //회원가입 액티비티로 이동
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
}
