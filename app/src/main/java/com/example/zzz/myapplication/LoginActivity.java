package com.example.zzz.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        final EditText idText = (EditText)findViewById(R.id.idText);
        final EditText passwordText = (EditText)findViewById(R.id.passwordText);

        final Button nextButton = (Button) findViewById(R.id.nextButton);

        ImageButton joinButton = (ImageButton) findViewById(R.id.joinButton);
        ImageButton loginButton = (ImageButton) findViewById(R.id.loginButton);
        //로그인 버튼 눌러도 아무 것도 실행되지 않음


/* 둘러보기 버튼 눌렀을 때 익명으로 로그인하기 구현
        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = "anonymous";
                String userPassword = "anonymous";
            }
        });
*/
        /*로그인 버튼*/
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userID = idText.getText().toString();
                final String userPassword = passwordText.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String result = jsonResponse.getString("result");
                            if(result.equals("0")) {
                                String userID = jsonResponse.getString("userID");
                                String userPassword = jsonResponse.getString("userPassword");

                                Intent nextIntent = new Intent(LoginActivity.this, MainSceneWithoutLogin.class);
                                nextIntent.putExtra("userID",userID);
                                nextIntent.putExtra("userPassword",userPassword);

                                LoginActivity.this.startActivity(nextIntent);
                            } else if(result.equals("1")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("일치하는 로그인 정보가 없습니다.")
                                        .setNegativeButton("다시 시도",null)
                                        .create()
                                        .show();
                            }
                            else if(result.equals("2")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("이메일이 인증되지 않은 사용자입니다.")
                                        .setNegativeButton("다시 시도",null)
                                        .create()
                                        .show();
                            }
                        }
                        catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                LoginRequest loginRequest = new LoginRequest(userID, userPassword, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);

            }
        });


        //가입 화면으로 넘어갈 수 있게 버튼 클릭 부분 구현
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent joinIntent = new Intent(LoginActivity.this, JoinActivity.class);
//                LoginActivity.this.startActivity(joinIntent);

                Intent RegisterIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(RegisterIntent);

            }
        });


    }
}
