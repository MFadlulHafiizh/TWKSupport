package com.application.twksupport;

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
import com.application.twksupport.model.BugsData;
import com.application.twksupport.model.ResponseData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BugsFragment extends Fragment {
    View view;
    private RecyclerView rvBugs;
    private List<BugsData> listBugs = new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout;


    public BugsFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bugs, container, false);
        rvBugs = (RecyclerView) view.findViewById(R.id.rv_bugs);
        swipeRefreshLayout = view.findViewById(R.id.refresh_bug);
        SharedPreferences getEmailUser = getActivity().getSharedPreferences("userInformation", 0);
        final String role = getEmailUser.getString("role", "not Authenticated");
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                switch (role){
                    case "client":
                        addListDataBugsUser();
                        break;

                    case "admin" :
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
                    case "client":
                        addListDataBugsUser();
                        break;

                    case "admin" :
                        addListAdminBug();
                        break;

                    default:
                        break;
                }
            }
        },50);

        return view;
    }

    protected void addListDataBugsUser(){
        ApiService api = ApiClient.getClient().create(ApiService.class);
        final SharedPreferences _objpref = getActivity().getSharedPreferences("JWTTOKEN", 0);
        SharedPreferences getEmailUser = getActivity().getSharedPreferences("userInformation", 0);
        String getToken = _objpref.getString("jwttoken", "");
        String email = getEmailUser.getString("email", "not Authenticated");
        Log.d("BugsFragment", email);
        Call<ResponseData> getData = api.getUserBugData(email, "Bearer "+getToken);
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
                            Toast.makeText(getActivity(), ""+databug.getPriority(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), "Unknown System Error, Please check your internet connection", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getActivity(), ""+databug.getPriority(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), "Unknown System Error, Please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }
}