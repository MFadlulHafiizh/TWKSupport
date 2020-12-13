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
import com.application.twksupport.adapter.RvNotificationAdapter;
import com.application.twksupport.model.NotificationData;
import com.application.twksupport.model.ResponseData;
import com.application.twksupport.model.UserData;
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
    private static NotificationActivity instance;
    private List<NotificationData> listnotif = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    private int page = 1;
    private int last_page = 1;
    private int notifCount;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView txt_notifCount;
    ImageView accountImage;
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
                        addListNotification(id_user);
                    }
                });
                break;
        }
    }

    public void addListNotification(String id_user) {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<ResponseData> getListNotif = api.getListNotification(page, id_user);
        Log.d("notifoeoe", "" + id_user);
        getListNotif.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful() && response.body() != null) {
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
                } else {
                    Log.d("RETRO", "errror: " + response.body());
                    Toast.makeText(NotificationActivity.this, "Error 401", Toast.LENGTH_SHORT).show();
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Log.d("RETRO", "error" + t.getMessage());
                Toast.makeText(NotificationActivity.this, "error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addListNotifStaff(String id_user, String token){
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<ResponseData> getListnotif = api.listNotifStaff("Bearer "+token, id_user, page);
        getListnotif.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful() && response.body() != null) {
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
                } else {
                    Log.d("RETRO", "errror: " + response.body());
                    Toast.makeText(NotificationActivity.this, "Error 401", Toast.LENGTH_SHORT).show();
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Log.d("RETRO", "error" + t.getMessage());
                Toast.makeText(NotificationActivity.this, "error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

                break;
        }
        return true;
    }
}