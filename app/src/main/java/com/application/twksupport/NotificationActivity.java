package com.application.twksupport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import com.application.twksupport.model.UserData;
import com.google.android.material.appbar.AppBarLayout;

public class NotificationActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    UserData userInformation = new UserData();
    ImageView accountImage;
    TextView userName;
    TextView userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initialize();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        appBarLayout.setStateListAnimator(null);
        SharedPreferences getUserInformation= getSharedPreferences("userInformation", 0);
        String email = getUserInformation.getString("email", "Not Authorized");
        String name = getUserInformation.getString("name", "Not Authorized");
        userEmail.setText(email);
        userName.setText(name);
    }

    protected void initialize(){
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = (AppBarLayout) toolbar.getParent();
        accountImage = appBarLayout.findViewById(R.id.account_pict);
        userName = appBarLayout.findViewById(R.id.userName);
        userEmail = appBarLayout.findViewById(R.id.userEmail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notification_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
        switch (item.getItemId()){
            case R.id.notification_menu:

                break;

            case R.id.filter_menu:

                break;
        }
        return true;
    }
}