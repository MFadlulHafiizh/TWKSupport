package com.application.twksupport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.application.twksupport.adapter.SectionsPagerAdapter;
import com.application.twksupport.auth.MainActivity;
import com.google.android.material.tabs.TabLayout;

public class UserActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SectionsPagerAdapter adapter;


    private Button btnLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        btnLogout = findViewById(R.id.btnlogout);
        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewpager);
        adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        //Add fragment
        adapter.addFragment(new BugsFragment(), "Bugs");
        adapter.addFragment(new FeatureFragment(), "Feature");
        adapter.addFragment(new DoneFragment(), "Done");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    private void logout(){
        SharedPreferences logoutPreferences = getSharedPreferences("valid", MODE_PRIVATE);
        logoutPreferences.edit().remove("token").commit();
        Intent login = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(login);

    }
}