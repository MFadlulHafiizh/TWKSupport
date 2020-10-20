package com.application.twksupport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;
import com.application.twksupport.adapter.SectionsPagerAdapter;
import com.application.twksupport.auth.MainActivity;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.tabs.TabLayout;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class UserActivity extends AppCompatActivity implements View.OnClickListener{
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SectionsPagerAdapter adapter;
    private Button btnLogout;
    private BlurView blurView;
    private FloatingActionsMenu floatMenu;
    private FloatingActionButton fab_bugs;
    private FloatingActionButton fab_reqFeature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        getSupportActionBar().setElevation(0);

        inisialitation();

        fab_bugs.setOnClickListener(this);
        fab_reqFeature.setOnClickListener(this);

        floatMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                blurBackground(true);
            }

            @Override
            public void onMenuCollapsed() {
                blurBackground(false);
            }
        });

        adapter = new SectionsPagerAdapter(getSupportFragmentManager());

        //Add fragment
        adapter.addFragment(new BugsFragment(), "Bugs");
        adapter.addFragment(new FeatureFragment(), "Feature");
        adapter.addFragment(new DoneFragment(), "Done");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        /*btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });*/
    }

    private void logout(){
        SharedPreferences logoutPreferences = getSharedPreferences("valid", MODE_PRIVATE);
        logoutPreferences.edit().remove("token").commit();
        Intent login = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(login);

    }

    private void inisialitation(){
        //btnLogout = findViewById(R.id.btnlogout);
        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewpager);
        fab_bugs = findViewById(R.id.fab_bugReport);
        fab_reqFeature = findViewById(R.id.fab_requestFeature);
        floatMenu = findViewById(R.id.fab_menu);
        blurView = findViewById(R.id.blur_bg);
    }

    private void blurBackground(boolean state){
        float radius = 2;

        View decorView = getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);

        Drawable windowBackground = decorView.getBackground();

        if (state == true){
            blurView.setupWith(rootView)
                    .setFrameClearDrawable(windowBackground)
                    .setBlurAlgorithm(new RenderScriptBlur(this))
                    .setBlurRadius(radius)
                    .setHasFixedTransformationMatrix(false);
            Animation fadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
            blurView.setAnimation(fadeInAnimation);
            blurView.setAlpha(1);
        }else{
            blurView.setAlpha(0);

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab_bugReport:
                Toast.makeText(this, "Untuk report bug, tpi nanti yaa", Toast.LENGTH_SHORT).show();
                break;

            case R.id.fab_requestFeature:
                Toast.makeText(this, "Untuk req new feature, tpi nanti yaa", Toast.LENGTH_SHORT).show();
                break;
        }

    }
}