package com.application.twksupport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.application.twksupport.RestApi.ApiClient;
import com.application.twksupport.RestApi.ApiService;
import com.application.twksupport.UIUX.UserInteraction;
import com.application.twksupport.adapter.SectionsPagerAdapter;
import com.application.twksupport.auth.MainActivity;
import com.application.twksupport.model.AppsUserData;
import com.application.twksupport.model.BugsData;
import com.application.twksupport.model.ResponseData;
import com.application.twksupport.model.UserData;
import com.application.twksupport.model.UserManager;
import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eightbitlab.com.blurview.BlurView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity implements View.OnClickListener{
    private TabLayout tabLayout;
    private BlurView blurView;
    private FloatingActionsMenu floatMenu;
    private FloatingActionButton fab_bugs;
    private FloatingActionButton fab_reqFeature;
    private ImageView userAccountPict;
    private Toolbar tool;
    private AppBarLayout appbar;
    private View decorView;
    private String getToken;
    private ViewPager viewPager;
    private String getUserEmail;
    private UserManager userInformation;
    private List<AppsUserData> listAppData = new ArrayList<>();
    UserInteraction userInteraction = new UserInteraction();
    ImageView userImage;
    TextView userName;
    TextView userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        userInformation = new UserManager(getApplicationContext());

        initialize();

        setSupportActionBar(tool);
        getSupportActionBar().setTitle(null);
        appbar.setStateListAnimator(null);

        SharedPreferences getUserInformation= getSharedPreferences("userInformation", 0);
        String email = getUserInformation.getString("email", "Not Authorized");
        String name = getUserInformation.getString("name", "Not Authorized");
        String role = getUserInformation.getString("role", "Not Authorized");
        String photo_url = getUserInformation.getString("photo","");
        Glide.with(userAccountPict.getContext()).load(photo_url).placeholder(R.drawable.ic_account_circle_white).into(userAccountPict);
        userEmail.setText(email);
        userName.setText(name);

        setUpWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);

        fab_bugs.setOnClickListener(this);
        fab_reqFeature.setOnClickListener(this);
        userImage.setOnClickListener(this);

        switch (role){
            case "client-head":
                floatMenu.setVisibility(View.VISIBLE);
                floatMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
                    @Override
                    public void onMenuExpanded() {
                        userInteraction.setBlurBackground(true, blurView, decorView, getApplicationContext());
                    }

                    @Override
                    public void onMenuCollapsed() {
                        userInteraction.setBlurBackground(false, blurView, decorView, getApplicationContext());
                    }
                });
                break;

            case "client-staff":
                floatMenu.setVisibility(View.VISIBLE);
                floatMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
                    @Override
                    public void onMenuExpanded() {
                        userInteraction.setBlurBackground(true, blurView, decorView, getApplicationContext());
                    }

                    @Override
                    public void onMenuCollapsed() {
                        userInteraction.setBlurBackground(false, blurView, decorView, getApplicationContext());
                    }
                });
                break;


            case "twk-head":
                floatMenu.setVisibility(View.INVISIBLE);
                break;
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences getUserInformation= getSharedPreferences("userInformation", 0);
        String photo_url = getUserInformation.getString("photo","");
        Glide.with(userAccountPict.getContext()).load(photo_url).placeholder(R.drawable.ic_account_circle_white).into(userAccountPict);
    }

    private void pushNotificationRegister(){

    }

    private void callUserInformation(){
        SharedPreferences _objpref = getSharedPreferences("JWTTOKEN", 0);
        getToken = _objpref.getString("jwttoken", "missing");
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Log.d("token", getToken);
        Call<ResponseBody> getUserData = api.getUserInformation("Bearer "+getToken);
        getUserData.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        String responseJSON = response.body().string();
                        SharedPreferences getUserInformation= getSharedPreferences("userInformation", 0);
                        String email = getUserInformation.getString("email", "Not Authorized");
                        String name = getUserInformation.getString("name", "Not Authorized");
                        userEmail.setText(email);
                        userName.setText(name);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(UserActivity.this, "Unknown Error", Toast.LENGTH_SHORT).show();
                    Log.d("hasil", ""+response.body());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("hasil", "Failed to load data, check your internet connection"+t.getMessage());
            }
        });
    }

    private void setUpWithViewPager(ViewPager myViewPager){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        //Add fragment
        adapter.addFragment(new BugsFragment(), "Bugs");
        adapter.addFragment(new FeatureFragment(), "Feature");
        adapter.addFragment(new DoneFragment(), "Done");
        myViewPager.setAdapter(adapter);
    }

    private void initialize() {
        tabLayout = findViewById(R.id.tabs);
        fab_bugs = findViewById(R.id.fab_bugReport);
        fab_reqFeature = findViewById(R.id.fab_requestFeature);
        viewPager = findViewById(R.id.viewpager);
        floatMenu = findViewById(R.id.fab_menu);
        blurView = findViewById(R.id.blur_bg);
        tool = findViewById(R.id.toolbar);
        appbar = (AppBarLayout) tool.getParent();
        userImage = appbar.findViewById(R.id.account_pict);
        userName = appbar.findViewById(R.id.userName);
        userEmail = appbar.findViewById(R.id.userEmail);
        decorView = getWindow().getDecorView();
        userAccountPict = tool.findViewById(R.id.account_pict);
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

            /*case R.id.filter_menu:
                userInteraction.showPopupFilter(this);
                break;*/
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

            case R.id.account_pict :
                Intent toProfile = new Intent(UserActivity.this, ProfileActivity.class);
                startActivity(toProfile);
        }

    }

}