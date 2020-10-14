package com.application.twksupport.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.twksupport.R;
import com.application.twksupport.model.TokenResponse;
import com.application.twksupport.UserActivity;

public class SplashActivity extends AppCompatActivity {

    private ConstraintLayout parentLayout;
    ImageView twkLogo;
    TextView txtSupport;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        twkLogo = findViewById(R.id.twklogo);
        txtSupport = findViewById(R.id.twkSupport);
        parentLayout = findViewById(R.id.splash_layout);

        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(3000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            actUserLogin();
                        }
                    });
                    finish();
                }
                catch (Exception e){
                    e.printStackTrace();
                    Log.d("machine", e.getMessage());
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
            Intent login = new Intent(SplashActivity.this, MainActivity.class);
            Pair[] pairs = new Pair[2];
            pairs[0] = new Pair<View, String>(twkLogo, "sendLogo");
            pairs[1] = new Pair<View, String>(txtSupport, "sendTitle");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, pairs);
            startActivity(login, options.toBundle());
        }
    }
}