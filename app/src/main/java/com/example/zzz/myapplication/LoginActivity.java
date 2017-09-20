package com.example.zzz.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

                            Log.d("Login", "Loading1234");

                            boolean result = jsonResponse.getBoolean("result");

                            Log.d("Login", "AfterLoading1234");

                            if(result) {
                                String userID = jsonResponse.getString("userID");
                                //String userPassword = jsonResponse.getString("userPassword");

                                Intent nextIntent = new Intent(LoginActivity.this, MainSceneWithLogin.class);
                                nextIntent.putExtra("userID",userID);

                                LoginActivity.this.startActivity(nextIntent);
                            } else {
                                Log.d("aa", "bbaacc111");
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("일치하는 로그인 정보가 없습니다.")
                                        .setNegativeButton("다시 시도",null)
                                        .create()
                                        .show();
                            }
                        }
                        catch(Exception e) {
                            Log.d("d","돼지야");
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
