package com.application.twksupport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.application.twksupport.RestApi.ApiClient;
import com.application.twksupport.RestApi.ApiService;
import com.application.twksupport.adapter.RvStaffListAdapter;
import com.application.twksupport.model.BugsData;
import com.application.twksupport.model.CacheData;
import com.application.twksupport.model.FeatureData;
import com.application.twksupport.model.StaffResponse;
import com.application.twksupport.model.UserData;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffListActivity extends AppCompatActivity {
    public static final String EXTRA_TICKET_BUG = "extra_ticket_bug";
    public static final String EXTRA_TICKET_FITUR = "extra_ticket_fitur";
    public static final String EXTRA_DATE = "extra_date";
    private List<UserData> staffList = new ArrayList<>();
    private CacheData cacheData;
    private RecyclerView rvStaff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_list);

        rvStaff = findViewById(R.id.rv_staff);
        rvStaff.setLayoutManager(new LinearLayoutManager(this));

        BugsData getTicketBug = getIntent().getParcelableExtra(EXTRA_TICKET_BUG);
        FeatureData getTicketFitur = getIntent().getParcelableExtra(EXTRA_TICKET_FITUR);
        String getDate = getIntent().getStringExtra(EXTRA_DATE);
        if (getIntent().hasExtra(EXTRA_TICKET_BUG)){
            String id_ticket = getTicketBug.getId_ticket();
            Log.d("ticketvalues", "" + id_ticket);
            assignAct(id_ticket, getDate);
        }
        if (getIntent().hasExtra(EXTRA_TICKET_FITUR)){
            String id_ticket = getTicketFitur.getId_ticket();
            Log.d("ticketvalues", ""+id_ticket);
            assignAct(id_ticket, getDate);

        }

    }

    private void assignAct(final String id_ticket, final String date) {
        final SharedPreferences _objpref = getSharedPreferences("JWTTOKEN", 0);
        final String getToken = _objpref.getString("jwttoken", "");
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<StaffResponse> getStaffData = api.getStaff("Bearer " + getToken);
        getStaffData.enqueue(new Callback<StaffResponse>() {
            @Override
            public void onResponse(Call<StaffResponse> call, Response<StaffResponse> response) {
                if (response.isSuccessful()) {
                    staffList = response.body().getUser();
                    Log.d("stafflist", "" + staffList);
                    RvStaffListAdapter mAdapter = new RvStaffListAdapter(staffList);
                    rvStaff.setAdapter(mAdapter);
                    mAdapter.setClick(new RvStaffListAdapter.ItemClick() {
                        @Override
                        public void onItemClicked(final UserData datauser) {
                            Log.d("userid", "" + datauser.getId());
                            new SweetAlertDialog(StaffListActivity.this)
                                    .setTitleText("Are you sure?")
                                    .setContentText("Assign this to " + datauser.getName() + " ?")
                                    .setCancelText("Cancel")
                                    .showCancelButton(true)
                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.cancel();
                                        }
                                    })
                                    .setConfirmText("Yes")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(final SweetAlertDialog sweetAlertDialog) {
                                            final SweetAlertDialog pDialog = new SweetAlertDialog(StaffListActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                                            pDialog.setTitleText("Loading");
                                            pDialog.setCancelable(false);
                                            pDialog.show();
                                            sweetAlertDialog.dismissWithAnimation();
                                            ApiService api = ApiClient.getClient().create(ApiService.class);
                                            Call<ResponseBody> sendAssignment = api.assign("Bearer " + getToken, datauser.getId(), id_ticket, date);
                                            Log.d("value", "" + datauser.getId() + " " + id_ticket);
                                            sendAssignment.enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    if (response.isSuccessful()) {
                                                        pDialog.dismiss();
                                                        SweetAlertDialog sweet = new SweetAlertDialog(StaffListActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                                                        sweet.setTitleText("Success");
                                                        sweet.setContentText("Assigned to " + datauser.getName());
                                                        sweet.setCanceledOnTouchOutside(false);
                                                        sweet.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                    @Override
                                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                        Intent goBack = new Intent(StaffListActivity.this, UserActivity.class);
                                                                        goBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                        startActivity(goBack);
                                                                        finish();
                                                                    }
                                                                });
                                                        sweet.show();
                                                    } else {
                                                        Log.d("RETRO", "" + response.body());
                                                        pDialog.dismiss();
                                                        new SweetAlertDialog(StaffListActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                                .setTitleText("Oops...")
                                                                .setContentText("Something went wrong!")
                                                                .show();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    pDialog.dismiss();
                                                    new SweetAlertDialog(StaffListActivity.this, SweetAlertDialog.ERROR_TYPE)
                                                            .setTitleText("Oops...")
                                                            .setContentText("Something went wrong!, Check your internet connection")
                                                            .show();
                                                }
                                            });
                                        }
                                    })
                                    .show();
                        }
                    });

                } else {
                    Log.d("RETRO", "" + response.body());
                }
            }

            @Override
            public void onFailure(Call<StaffResponse> call, Throwable t) {
                Log.d("StaffListActivity", "" + t.getMessage());
            }
        });
    }
}