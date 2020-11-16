package com.application.twksupport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    public static final String EXTRA_TICKET = "extra_ticket";
    private List<UserData> staffList = new ArrayList<>();
    private CacheData cacheData;
    private RecyclerView rvStaff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_list);

        BugsData getTicket = getIntent().getParcelableExtra(EXTRA_TICKET);

        String id_ticket = getTicket.getId_ticket();
        rvStaff = findViewById(R.id.rv_staff);
        rvStaff.setLayoutManager(new LinearLayoutManager(this));
        cacheData = new CacheData(getApplicationContext());
        cacheData.assignmentSetIdTicket(id_ticket);
        SharedPreferences assignment = getSharedPreferences("assignment", 0);
        String ticket_id = assignment.getString("ticket_id", "");
        assignAct(ticket_id);

    }

    private void assignAct(final String id_ticket) {
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
                            cacheData.assignmentSetIdStaff(datauser.getId());
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
                                            Call<ResponseBody> sendAssignment = api.assign("Bearer " + getToken, datauser.getId(), id_ticket, "2020-11-16");
                                            Log.d("value", "" + datauser.getId() + " " + id_ticket);
                                            sendAssignment.enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    if (response.isSuccessful()) {
                                                        new SweetAlertDialog(StaffListActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                                                .setTitleText("Success")
                                                                .setContentText("Assigned to " + datauser.getName())
                                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                    @Override
                                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                        onBackPressed();
                                                                    }
                                                                })
                                                                .show();
                                                        pDialog.dismiss();

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