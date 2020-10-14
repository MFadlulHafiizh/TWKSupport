package com.application.twksupport.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.application.twksupport.R;
import com.application.twksupport.model.TokenResponse;
import com.application.twksupport.UserActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(3500);
                    actUserLogin();
                    finish();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    private void actUserLogin(){
        SharedPreferences _objpref=getSharedPreferences("valid", MODE_PRIVATE);
        TokenResponse tokenResponse = new TokenResponse();
        if (_objpref.getString("token", "") != ""){
            Intent toUser = new Intent(getApplicationContext(), UserActivity.class);
            startActivity(toUser);
        }
        else {
            Intent login = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(login);
        }
    }
}