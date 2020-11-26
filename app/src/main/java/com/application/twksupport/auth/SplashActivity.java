package com.application.twksupport.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.application.twksupport.R;
import com.application.twksupport.TwkStaffActivity;
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
                            finish();
                        }
                    });
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
        SharedPreferences _objpref=getSharedPreferences("JWTTOKEN", 0);
        SharedPreferences getRole = getSharedPreferences("userInformation", 0);
        String role = getRole.getString("role", "");
        if (_objpref.getString("jwttoken", "") != ""){
            Log.d("Splashauth", ""+_objpref.getString("jwttoken", ""));
            Log.d("Splashauth", ""+role);
            if (role.equals("twk-head") || role.equals("client-head") || role.equals("client-staff")) {
                Intent toUser = new Intent(getApplicationContext(), UserActivity.class);
                startActivity(toUser);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }else {
                Intent toStaff = new Intent(getApplicationContext(), TwkStaffActivity.class);
                startActivity(toStaff);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }

        }
        else {
            Intent login = new Intent(SplashActivity.this, MainActivity.class);
            Pair[] pairs = new Pair[3];
            pairs[0] = new Pair<View, String>(twkLogo, "sendLogo");
            pairs[1] = new Pair<View, String>(txtSupport, "sendTitle");
            pairs[2] = new Pair<View, String>(parentLayout, "layout");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, pairs);
            startActivity(login, options.toBundle());
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }
}