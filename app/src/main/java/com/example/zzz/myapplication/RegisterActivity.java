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

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Intent intent = getIntent();

        final EditText idText = (EditText)findViewById(R.id.idText);
        final EditText passwordText = (EditText)findViewById(R.id.passwordText);
        final EditText passwordCheckText = (EditText)findViewById(R.id.passwordCheckText);

        Button nextButton = (Button) findViewById(R.id.nextButton);

        ImageButton joinButton = (ImageButton) findViewById(R.id.joinButton);
        ImageButton loginButton = (ImageButton) findViewById(R.id.loginButton);

        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String userID = idText.getText().toString();
                String userPassword = passwordText.getText().toString();
                String userPasswordCheck = passwordCheckText.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            //특정 response를 실행했을 때 결과가 담길 수 있도록 함
                            boolean result = jsonResponse.getBoolean("result");

                            if(result) {

                                Intent intent = new Intent(RegisterActivity.this, RegisterCompleteActivity.class);
                                RegisterActivity.this.startActivity(intent);
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage("입력하신 아이디가 이미 사용중입니다.")
                                        .setNegativeButton("다시 시도",null)
                                        .create()
                                        .show();
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                if(userPassword.equals(userPasswordCheck)) {
                    RegisterRequest registerRequest = new RegisterRequest(userID, userPassword, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                    queue.add(registerRequest); //버튼을 클릭했을때 registerRequest가 실행
                }

                else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("비밀번호와 비밀번호 확인이 일치하지 않습니다.")
                            .setNegativeButton("다시 시도",null)
                            .create()
                            .show();
                }
            }
        });

        //가입 화면으로 넘어갈 수 있게 버튼 클릭 부분 구현
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent LoginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(LoginIntent);

            }
        });
    }

}
