package com.example.zzz.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class JoinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        final EditText emailText = (EditText)findViewById(R.id.emailText);

        Button nextButton = (Button) findViewById(R.id.nextButton);

        Button joinButton = (Button) findViewById(R.id.joinButton);
        Button loginButton = (Button) findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LoginIntent = new Intent(JoinActivity.this, LoginActivity.class);
                JoinActivity.this.startActivity(LoginIntent);
            }
        });


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent RegisterIntent = new Intent(JoinActivity.this, RegisterActivity.class);
                String userEmail = emailText.getText().toString();
                RegisterIntent.putExtra("Email",userEmail);

                JoinActivity.this.startActivity(RegisterIntent);
            }
        });


    }
}
