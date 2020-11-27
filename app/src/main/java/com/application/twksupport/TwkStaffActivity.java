package com.application.twksupport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.twksupport.adapter.SectionsPagerAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;


public class TwkStaffActivity extends AppCompatActivity {

    private TabLayout staffTabs;
    private ViewPager viewPagerStaff;
    private Toolbar toolbar;
    private AppBarLayout appbar;
    private ImageView staffImage;
    private TextView staffName, staffEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twk_staff);
        initialize();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        appbar.setStateListAnimator(null);

        SharedPreferences getUserInformation= getSharedPreferences("userInformation", 0);
        String email = getUserInformation.getString("email", "Not Authorized");
        String name = getUserInformation.getString("name", "Not Authorized");
        staffEmail.setText(email);
        staffName.setText(name);

        setUpWithViewPager(viewPagerStaff);
        staffTabs.setupWithViewPager(viewPagerStaff);

        staffImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toProfile = new Intent(TwkStaffActivity.this, ProfileActivity.class);
                startActivity(toProfile);
            }
        });
    }
    private void initialize(){
        viewPagerStaff = findViewById(R.id.viewpagerStaff);
        toolbar = findViewById(R.id.toolbar);
        appbar = (AppBarLayout) toolbar.getParent();
        staffImage = findViewById(R.id.account_pict);
        staffName = findViewById(R.id.userName);
        staffEmail = findViewById(R.id.userEmail);
        staffTabs = findViewById(R.id.staff_tabs);
    }

    private void setUpWithViewPager(ViewPager myViewPager){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new StaffToDoFragment(), "To do");
        adapter.addFragment(new DoneFragment(), "Has done");
        myViewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notification_menu:
                Intent toNotificationAct = new Intent(TwkStaffActivity.this, NotificationActivity.class);
                Pair[] pairs = new Pair[5];
                pairs[0] = new Pair<View, String>(staffImage, "account_pict");
                pairs[1] = new Pair<View, String>(staffName, "username");
                pairs[2] = new Pair<View, String>(staffEmail, "email");
                pairs[3] = new Pair<View, String>(toolbar, "toolbar");
                pairs[4] = new Pair<View, String>(staffTabs, "tabContent");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(TwkStaffActivity.this, pairs);
                startActivity(toNotificationAct, options.toBundle());
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
        return true;
    }
}