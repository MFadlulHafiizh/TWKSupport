package com.application.twksupport;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.application.twksupport.RestApi.ApiClient;
import com.application.twksupport.RestApi.ApiService;
import com.application.twksupport.adapter.RvBugsAdapter;
import com.application.twksupport.adapter.RvFeatureAdapter;
import com.application.twksupport.model.BugsData;
import com.application.twksupport.model.FeatureData;
import com.application.twksupport.model.ResponseData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeatureFragment extends Fragment {
    View view;
    private RecyclerView rvFeature;
    private List<FeatureData> listFeature = new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout;
    private static FeatureFragment instance;

    public FeatureFragment() {

    }

    public static FeatureFragment getInstance(){
        return instance;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_feature, container, false);
        swipeRefreshLayout = view.findViewById(R.id.refresh_feature);
        instance = this;
        SharedPreferences getEmailUser = getActivity().getSharedPreferences("userInformation", 0);
        final String role = getEmailUser.getString("role", "not Authenticated");
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                switch (role){
                    case "client-head":
                        addListDataFeatureUser();
                        break;

                    case "client-staff":
                        addListDataFeatureUser();
                        break;

                    case "twk-head" :
                        addListAdminFeature();
                        break;

                    default:
                        break;
                }
            }
        });
        rvFeature = (RecyclerView) view.findViewById(R.id.rv_feature);
        rvFeature.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeRefreshLayout.setRefreshing(true);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (role){
                    case "client-head":
                        addListDataFeatureUser();
                        break;

                    case "client-staff":
                        addListDataFeatureUser();
                        break;

                    case "twk-head" :
                        addListAdminFeature();
                        break;

                    default:
                        break;
                }
            }
        },50);

        return view;
    }


    public void addListDataFeatureUser(){
        ApiService api = ApiClient.getClient().create(ApiService.class);
        SharedPreferences getCompanyUser = getActivity().getSharedPreferences("userInformation", 0);
        SharedPreferences _objpref = getActivity().getSharedPreferences("JWTTOKEN", 0);
        String getToken = _objpref.getString("jwttoken", "missing");
        int idCompany = getCompanyUser.getInt("id_perushaan", 0);
        Call<ResponseData> getData = api.getUserFeatureData(idCompany, "Bearer "+getToken);
        getData.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful()){
                    listFeature = response.body().getFeatureData();
                    RvFeatureAdapter mAdapter = new RvFeatureAdapter(listFeature, getContext());
                    rvFeature.setAdapter(mAdapter);
                    mAdapter.setClick(new RvFeatureAdapter.ItemClick() {
                        @Override
                        public void onItemClicked(FeatureData datafeature) {
                            Toast.makeText(getActivity(), ""+datafeature.getPriority(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    mAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else {
                    Toast.makeText(getActivity(), "Unatourized", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }

            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Log.d("RETRO", "FAILED : respon gagal");
                Toast.makeText(getActivity(), "Unknown System Error, Please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void addListAdminFeature(){
        ApiService api = ApiClient.getClient().create(ApiService.class);
        SharedPreferences _objpref = getActivity().getSharedPreferences("JWTTOKEN", 0);
        String getToken = _objpref.getString("jwttoken", "");
        Call<ResponseData> getData = api.getAdminFeatureData("Bearer "+getToken);
        getData.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful()){
                    listFeature = response.body().getFeatureData();
                    Log.d("feature", ""+listFeature);
                    RvFeatureAdapter mAdapter = new RvFeatureAdapter(listFeature, getContext());
                    rvFeature.setAdapter(mAdapter);
                    mAdapter.setClick(new RvFeatureAdapter.ItemClick() {
                        @Override
                        public void onItemClicked(FeatureData datafeature) {
                            Toast.makeText(getActivity(), ""+datafeature.getPriority(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    mAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else {
                    Toast.makeText(getActivity(), "Unatourized", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }

            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Log.d("RETRO", "FAILED : respon gagal");
                Toast.makeText(getActivity(), "Unknown System Error, Please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }
}