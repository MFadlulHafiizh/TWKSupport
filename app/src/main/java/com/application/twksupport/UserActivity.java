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
    Dialog popUpFilter;
    ImageView userImage;
    TextView userName;
    TextView userEmail;
    TextView btmSheetTitle;

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

        adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        //Add fragment
        adapter.addFragment(new BugsFragment(), "Bugs");
        adapter.addFragment(new FeatureFragment(), "Feature");
        adapter.addFragment(new DoneFragment(), "Done");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

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
                showPopupFilter();
                break;
        }
        return true;
    }

    private void blurBackground(boolean state) {
        float radius = 2;

        View decorView = getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);

        Drawable windowBackground = decorView.getBackground();

        if (state == true) {
            blurView.setupWith(rootView)
                    .setFrameClearDrawable(windowBackground)
                    .setBlurAlgorithm(new RenderScriptBlur(this))
                    .setBlurRadius(radius)
                    .setHasFixedTransformationMatrix(false);
            Animation fadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
            blurView.setAnimation(fadeInAnimation);
            blurView.setAlpha(1);
        } else {
            blurView.setAlpha(0);

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_bugReport:
                showBottomSheet("Report Your Bug", "report");
                break;

            case R.id.fab_requestFeature:
                showBottomSheet("Request Some Feature", "request");
                break;
        }

    }

    public void showBottomSheet(String title, final String type) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(UserActivity.this, R.style.AppBottomSheetDialogTheme);
        View content = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        final Spinner spinPriority = (Spinner) content.findViewById(R.id.prioritySpinner);
        final Spinner spinAppName = (Spinner) content.findViewById(R.id.appnameSpin);
        bottomSheetDialog.setContentView(content);
        final EditText etSubject = bottomSheetDialog.findViewById(R.id.edtSubject);
        final EditText etDetails = bottomSheetDialog.findViewById(R.id.edtDetails);
        btmSheetTitle = bottomSheetDialog.findViewById(R.id.reqeust_type);
        btmSheetTitle.setText(title);
        bottomSheetDialog.setCanceledOnTouchOutside(false);

        bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                floatMenu.collapse();
                blurView.setAlpha(1);
            }
        });
        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                blurView.setAlpha(0);
            }
        });
        Button btnReport = bottomSheetDialog.findViewById(R.id.btn_report);

        spinPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etSubject.getText().toString().equals("") && !etDetails.getText().toString().equals("")) {
                    AlertDialog.Builder alertBuild = new AlertDialog.Builder(view.getContext());
                    alertBuild.setTitle("Your "+ type + " has sended");
                    alertBuild.setMessage("Wait for next response");
                    alertBuild.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            bottomSheetDialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = alertBuild.create();
                    alertDialog.show();
                } else {
                    Toast.makeText(UserActivity.this, "Please input data correctly", Toast.LENGTH_SHORT).show();
                }
            }
        });
        bottomSheetDialog.show();
    }

    public void showPopupFilter(){
        Spinner spinPriorityFiler, spinAppnameFilter;
        Button reset, apply;
        ImageButton close;
        popUpFilter = new Dialog(UserActivity.this, R.style.AppBottomSheetDialogTheme);
        popUpFilter.setContentView(R.layout.filter_popup);
        popUpFilter.setCanceledOnTouchOutside(false);
        spinPriorityFiler = popUpFilter.findViewById(R.id.priorityFilter);
        spinAppnameFilter = popUpFilter.findViewById(R.id.appNameFilter);
        reset = popUpFilter.findViewById(R.id.btnResetFilter);
        apply = popUpFilter.findViewById(R.id.btnApplyFilter);
        close = popUpFilter.findViewById(R.id.btnCloseFilter);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpFilter.dismiss();
            }
        });
        popUpFilter.show();
    }
}