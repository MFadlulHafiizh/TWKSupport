package com.application.twksupport;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.application.twksupport.RestApi.ApiClient;
import com.application.twksupport.RestApi.ApiService;
import com.application.twksupport.UIUX.UserInteraction;
import com.application.twksupport.adapter.RvBugsAdapter;
import com.application.twksupport.model.BugsData;
import com.application.twksupport.model.ResponseData;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BugsFragment extends Fragment {
    View view;
    private RecyclerView rvBugs;
    private List<BugsData> listBugs = new ArrayList<>();
    private static BugsFragment instance;
    private TextView filterbutton;
    SwipeRefreshLayout swipeRefreshLayout;


    public BugsFragment(){

    }

    public static BugsFragment getInstance(){
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bugs, container, false);
        instance = this;

        rvBugs = (RecyclerView) view.findViewById(R.id.rv_bugs);
        swipeRefreshLayout = view.findViewById(R.id.refresh_bug);
        filterbutton = view.findViewById(R.id.filter_fragment);
        final UserInteraction userInteraction = new UserInteraction();
        filterbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userInteraction.showPopupFilter(getActivity());
            }
        });

        SharedPreferences getRoleUser = getActivity().getSharedPreferences("userInformation", 0);
        final String role = getRoleUser.getString("role", "not Authenticated");
        Log.d("role", ""+ role);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                switch (role){
                    case "client-head":
                        addListDataBugsUser();
                        break;

                    case "client-staff":
                        addListDataBugsUser();
                        break;

                    case "twk-head" :
                        addListAdminBug();
                        break;

                    default:
                        break;
                }
            }
        });
        rvBugs.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeRefreshLayout.setRefreshing(true);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (role){
                    case "client-head":
                        addListDataBugsUser();
                        break;

                    case "client-staff":
                        addListDataBugsUser();
                        break;

                    case "twk-head" :
                        addListAdminBug();
                        break;

                    default:
                        break;
                }
            }
        },50);

        return view;
    }

    public void addListDataBugsUser(){
        ApiService api = ApiClient.getClient().create(ApiService.class);
        final SharedPreferences _objpref = getActivity().getSharedPreferences("JWTTOKEN", 0);
        SharedPreferences getCompanyUser = getActivity().getSharedPreferences("userInformation", 0);
        String getToken = _objpref.getString("jwttoken", "");
        int idCompany = getCompanyUser.getInt("id_perushaan", 0);
        Log.d("BugsFragment", ""+idCompany);
        Call<ResponseData> getData = api.getUserBugData(idCompany, "Bearer "+getToken);
        getData.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful()){
                    Log.d("RETRO", "RESPONSE : " + response.body().getBugData());
                    listBugs = response.body().getBugData();
                    RvBugsAdapter mAdapter = new RvBugsAdapter(listBugs, getContext());
                    rvBugs.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    rvBugs.smoothScrollToPosition(0);
                    swipeRefreshLayout.setRefreshing(false);
                    mAdapter.setClick(new RvBugsAdapter.ItemClick() {
                        @Override
                        public void onItemClicked(BugsData databug) {
                            Intent toDetail = new Intent(getActivity(), DetailActivity.class);
                            toDetail.putExtra(DetailActivity.EXTRA_BUG, databug);
                            startActivity(toDetail);
                        }
                    });
                }
                else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Log.d("RETRO", "FAILED : respon gagal"+t.getMessage());
                SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
                dialog.setTitleText("Oops...");
                dialog.setContentText("Something went wrong!, Please check your internet connection");
                dialog.setConfirmText("exit");
                dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        getActivity().finish();
                        System.exit(0);
                    }
                });
                dialog.show();
            }
        });
    }

    protected void addListAdminBug(){
        final SharedPreferences _objpref = getActivity().getSharedPreferences("JWTTOKEN", 0);
        String getToken = _objpref.getString("jwttoken", "");
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<ResponseData> getData = api.getAdminBugData("Bearer "+getToken);
        getData.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful()){
                    Log.d("RETRO", "RESPONSE : " + response.body().getBugData());
                    listBugs = response.body().getBugData();
                    RvBugsAdapter mAdapter = new RvBugsAdapter(listBugs, getContext());
                    rvBugs.setAdapter(mAdapter);
                    mAdapter.setClick(new RvBugsAdapter.ItemClick() {
                        @Override
                        public void onItemClicked(BugsData databug) {
                            Intent toDetail = new Intent(getActivity(), DetailActivity.class);
                            toDetail.putExtra(DetailActivity.EXTRA_BUG, databug);
                            startActivity(toDetail);
                        }
                    });
                    mAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Log.d("RETRO", "FAILED : respon gagal");
                SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE);
                dialog.setTitleText("Oops...");
                dialog.setContentText("Something went wrong!, Please check your internet connection");
                dialog.setConfirmText("exit");
                dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                getActivity().finish();
                                System.exit(0);
                            }
                        });
                dialog.show();
            }
        });
    }
}