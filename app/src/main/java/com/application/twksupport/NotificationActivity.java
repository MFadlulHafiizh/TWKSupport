package com.application.twksupport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.application.twksupport.RestApi.ApiClient;
import com.application.twksupport.RestApi.ApiService;
import com.application.twksupport.UIUX.UserInteraction;
import com.application.twksupport.adapter.RvNotificationAdapter;
import com.application.twksupport.model.NotificationData;
import com.application.twksupport.model.ResponseData;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private RecyclerView rvNotif;
    private RvNotificationAdapter mAdapter;
    private LinearLayout error_container;
    private static NotificationActivity instance;
    private List<NotificationData> listnotif = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    private int page = 1;
    private int last_page = 1;
    private String apps_name = null;
    private String priority = null;
    private String fromDate = null;
    private String untilDate = null;
    private int notifCount;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView txt_notifCount, txtErrorMessage;
    ImageView accountImage;
    Button btnTryAgain;
    TextView userName;
    TextView userEmail;

    public static NotificationActivity getInstance(){
        return instance;
    }

    public List<NotificationData> getListnotif() {
        return listnotif;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setApps_name(String apps_name) {
        this.apps_name = apps_name;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public void setUntilDate(String untilDate) {
        this.untilDate = untilDate;
    }

    public RvNotificationAdapter getmAdapter() {
        return mAdapter;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initialize();
        instance = this;

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        appBarLayout.setStateListAnimator(null);

        mAdapter = new RvNotificationAdapter(listnotif, getApplicationContext());
        rvNotif.setLayoutManager(linearLayoutManager);
        rvNotif.setAdapter(mAdapter);

        swipeRefreshLayout.setRefreshing(true);

        SharedPreferences getUserInformation = getSharedPreferences("userInformation", 0);
        SharedPreferences getSession = getSharedPreferences("JWTTOKEN", 0);
        final String token = getSession.getString("jwttoken", "");
        String email = getUserInformation.getString("email", "Not Authorized");
        final String id_user = getUserInformation.getString("id", "Not Authorized");
        String name = getUserInformation.getString("name", "Not Authorized");
        String role = getUserInformation.getString("role", "");
        String photo_url = getUserInformation.getString("photo","");
        Glide.with(accountImage.getContext()).load(photo_url).placeholder(R.drawable.ic_account_circle_white).into(accountImage);
        userEmail.setText(email);
        userName.setText(name);

        switch (role){
            case "twk-staff":
                addListNotifStaff(id_user, token);
                rvNotif.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        int visibleItemCount = linearLayoutManager.getChildCount();
                        int pastVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                        int total = mAdapter.getItemCount();
                        if (page < last_page) {
                            if (visibleItemCount + pastVisibleItem >= total) {
                                page++;
                                progressBar.setVisibility(View.VISIBLE);
                                addListNotifStaff(id_user, token);
                            }
                        }
                        super.onScrolled(recyclerView, dx, dy);
                    }
                });

                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        page = 1;
                        listnotif.clear();
                        priority = null;
                        apps_name = null;
                        fromDate = null;
                        untilDate = null;
                        mAdapter.notifyDataSetChanged();
                        addListNotifStaff(id_user, token);
                    }
                });

                btnTryAgain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        page = 1;
                        listnotif.clear();
                        mAdapter.notifyDataSetChanged();
                        addListNotifStaff(id_user, token);
                    }
                });
                break;

            default:
                addListNotification(id_user);
                rvNotif.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        int visibleItemCount = linearLayoutManager.getChildCount();
                        int pastVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                        int total = mAdapter.getItemCount();
                        if (page < last_page) {
                            if (visibleItemCount + pastVisibleItem >= total) {
                                page++;
                                progressBar.setVisibility(View.VISIBLE);
                                addListNotification(id_user);
                            }
                        }
                        super.onScrolled(recyclerView, dx, dy);
                    }
                });

                swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        page = 1;
                        listnotif.clear();
                        mAdapter.notifyDataSetChanged();
                        priority = null;
                        apps_name = null;
                        fromDate = null;
                        untilDate = null;
                        addListNotification(id_user);
                    }
                });

                btnTryAgain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        page = 1;
                        listnotif.clear();
                        mAdapter.notifyDataSetChanged();
                        addListNotification(id_user);
                    }
                });
                break;
        }
    }

    public void addListNotification(String id_user) {
        error_container.setVisibility(View.GONE);
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<ResponseData> getListNotif = api.getListNotification(page, id_user, priority, apps_name, fromDate, untilDate);
        Log.d("notifoeoe", "" + id_user);
        getListNotif.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMessage().equals("success")) {
                    Log.d("RETROnotif", "success: " + response.body().getNotifData());
                    List<NotificationData> responseBody = response.body().getNotifData();
                    listnotif.addAll(responseBody);
                    notifCount = response.body().getNotifCount();
                    txt_notifCount.setText(String.valueOf(notifCount));
                    Log.d("counteaw", "" + notifCount);
                    mAdapter.notifyDataSetChanged();
                    last_page = response.body().getLast_page_notif();
                    if (page == last_page) {
                        progressBar.setVisibility(View.GONE);
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    swipeRefreshLayout.setRefreshing(false);
                    mAdapter.setClick(new RvNotificationAdapter.ItemClick() {
                        @Override
                        public void onItemClicked(final NotificationData notifData) {
                            ApiService api = ApiClient.getClient().create(ApiService.class);
                            Call<ResponseBody> markAsRead = api.markAsRead(notifData.getId_notif(), 1);
                            Log.d("clickedNotif", "" + notifData.getId_notif());
                            markAsRead.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    Intent toDetail = new Intent(NotificationActivity.this, DetailActivity.class);
                                    toDetail.putExtra(DetailActivity.EXTRA_NOTIF, notifData);
                                    startActivity(toDetail);
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Toast.makeText(NotificationActivity.this, "failed", Toast.LENGTH_SHORT).show();
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                }else if(response.isSuccessful() && response.body() != null && response.body().getMessage().equals("No Data Available")){
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(NotificationActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else {
                    Log.d("RETRO", "errror: " + response.body());
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(NotificationActivity.this, "Error 401", Toast.LENGTH_SHORT).show();
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Log.d("RETRO", "error" + t.getMessage());
                error_container.setVisibility(View.VISIBLE);
                txtErrorMessage.setText("Can't connect to server, please check your internet connection");
            }
        });

    }

    private void addListNotifStaff(String id_user, String token){
        error_container.setVisibility(View.GONE);
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<ResponseData> getListnotif = api.listNotifStaff("Bearer "+token, id_user, page);
        getListnotif.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMessage().equals("success")) {
                    Log.d("RETROnotif", "success: " + response.body().getNotifData());
                    List<NotificationData> responseBody = response.body().getNotifData();
                    listnotif.addAll(responseBody);
                    notifCount = response.body().getNotifCount();
                    txt_notifCount.setText(String.valueOf(notifCount));
                    Log.d("counteaw", "" + notifCount);
                    mAdapter.notifyDataSetChanged();
                    last_page = response.body().getLast_page_notif();
                    if (page == last_page) {
                        progressBar.setVisibility(View.GONE);
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    swipeRefreshLayout.setRefreshing(false);
                    mAdapter.setClick(new RvNotificationAdapter.ItemClick() {
                        @Override
                        public void onItemClicked(final NotificationData notifData) {
                            ApiService api = ApiClient.getClient().create(ApiService.class);
                            Call<ResponseBody> markAsRead = api.markAsRead(notifData.getId_notif(), 1);
                            Log.d("clickedNotif", "" + notifData.getId_notif());
                            markAsRead.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    Intent toDetail = new Intent(NotificationActivity.this, DetailActivity.class);
                                    toDetail.putExtra(DetailActivity.EXTRA_NOTIF, notifData);
                                    startActivity(toDetail);
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Toast.makeText(NotificationActivity.this, "Error please check your internet connection", Toast.LENGTH_SHORT).show();
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                }
                else if(response.isSuccessful() && response.body() != null && response.body().getMessage().equals("No Data Available")){
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(NotificationActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else {
                    Log.d("RETRO", "errror: " + response.body());
                    swipeRefreshLayout.setRefreshing(false);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Log.d("RETRO", "error" + t.getMessage());
                error_container.setVisibility(View.VISIBLE);
                txtErrorMessage.setText("Can't connect to server, please check your internet connection");
            }
        });
    }

    protected void initialize() {
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = (AppBarLayout) toolbar.getParent();
        accountImage = appBarLayout.findViewById(R.id.account_pict);
        userName = appBarLayout.findViewById(R.id.userName);
        userEmail = appBarLayout.findViewById(R.id.userEmail);
        rvNotif = findViewById(R.id.rv_notif);
        progressBar = findViewById(R.id.progresbar_notif);
        swipeRefreshLayout = findViewById(R.id.refresh_notif);
        txt_notifCount = findViewById(R.id.notif_count);
        error_container = findViewById(R.id.error_frame_notif);
        txtErrorMessage = findViewById(R.id.error_message_notif);
        btnTryAgain = findViewById(R.id.btn_tryAgain_notif);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notification_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
        switch (item.getItemId()) {
            case R.id.notification_menu:

                break;

            case R.id.filter_menu:
                UserInteraction userInteraction = new UserInteraction();
                userInteraction.showPopupFilter(NotificationActivity.this,"extra_notif");
                break;
        }
        return true;
    }
}