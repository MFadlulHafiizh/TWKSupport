package com.application.twksupport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.application.twksupport.UIUX.UserInteraction;
import com.application.twksupport.adapter.SectionsPagerAdapter;
import com.application.twksupport.auth.MainActivity;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
    private Toolbar tool;
    private AppBarLayout appbar;
    private View decorView;
    UserInteraction userInteraction = new UserInteraction();
    ImageView userImage;
    TextView userName;
    TextView userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initialize();

        fab_bugs.setOnClickListener(this);
        fab_reqFeature.setOnClickListener(this);

        setSupportActionBar(tool);
        getSupportActionBar().setTitle(null);
        appbar.setStateListAnimator(null);

        floatMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                userInteraction.setBlurBackground(true, blurView, decorView, UserActivity.this);
            }

            @Override
            public void onMenuCollapsed() {
                userInteraction.setBlurBackground(false, blurView, decorView, UserActivity.this);
            }
        });

        /*btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });*/
    }

    private void logout() {
        SharedPreferences logoutPreferences = getSharedPreferences("valid", MODE_PRIVATE);
        logoutPreferences.edit().remove("token").commit();
        Intent login = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(login);

    }

    private void initialize() {
        //btnLogout = findViewById(R.id.btnlogout);
        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewpager);
        fab_bugs = findViewById(R.id.fab_bugReport);
        fab_reqFeature = findViewById(R.id.fab_requestFeature);
        floatMenu = findViewById(R.id.fab_menu);
        blurView = findViewById(R.id.blur_bg);
        tool = findViewById(R.id.toolbarnotification);
        appbar = (AppBarLayout) tool.getParent();
        userImage = appbar.findViewById(R.id.account_pict);
        userName = appbar.findViewById(R.id.userName);
        userEmail = appbar.findViewById(R.id.userEmail);
        decorView = getWindow().getDecorView();
        adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        //Add fragment
        adapter.addFragment(new BugsFragment(), "Bugs");
        adapter.addFragment(new FeatureFragment(), "Feature");
        adapter.addFragment(new DoneFragment(), "Done");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.notification_menu:
                Intent toNotificationAct = new Intent(UserActivity.this, NotificationActivity.class);
                Pair[] pairs = new Pair[5];
                pairs[0] = new Pair<View, String>(userImage, "account_pict");
                pairs[1] = new Pair<View, String>(userName, "username");
                pairs[2] = new Pair<View, String>(userEmail, "email");
                pairs[3] = new Pair<View, String>(tool, "toolbar");
                pairs[4] = new Pair<View, String>(tabLayout, "tabContent");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(UserActivity.this, pairs);
                startActivity(toNotificationAct, options.toBundle());
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.filter_menu:
                userInteraction.showPopupFilter(this);
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_bugReport:
                userInteraction.showBottomSheet(UserActivity.this, floatMenu, blurView, getLayoutInflater(),"Report Your Bug", "report");
                break;

            case R.id.fab_requestFeature:
                userInteraction.showBottomSheet(UserActivity.this, floatMenu, blurView, getLayoutInflater(), "Request Some Feature", "request");
                break;
        }

    }

}