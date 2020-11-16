package com.application.twksupport;

import android.app.ProgressDialog;
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
import com.application.twksupport.adapter.RvDoneAdapter;
import com.application.twksupport.model.BugsData;
import com.application.twksupport.model.DoneData;
import com.application.twksupport.model.ResponseData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoneFragment extends Fragment {
    View view;
    private RecyclerView rvDone;
    private List<DoneData> listDone = new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout;

    public DoneFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_done, container, false);
        SharedPreferences getEmailUser = getActivity().getSharedPreferences("userInformation", 0);
        final String role = getEmailUser.getString("role", "not Authenticated");
        rvDone = (RecyclerView) view.findViewById(R.id.rv_done);
        swipeRefreshLayout = view.findViewById(R.id.refresh_hasDone);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                switch (role) {
                    case "client-head":
                        addListDataDone();
                        break;

                    case "client-staff":
                        addListDataDone();
                        break;


                    case "twk-head":
                        addListDoneAdmin();
                        break;

                    default:
                        break;
                }
            }
        });
        rvDone.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeRefreshLayout.setRefreshing(true);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (role) {
                    case "client-head":
                        addListDataDone();
                        break;

                    case "client-staff":
                        addListDataDone();
                        break;


                    case "twk-head":
                        addListDoneAdmin();
                        break;

                    default:
                        break;
                }
            }
        }, 50);


        return view;
    }

    protected void addListDataDone() {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        final SharedPreferences _objpref = getActivity().getSharedPreferences("JWTTOKEN", 0);
        SharedPreferences getCompanyUser = getActivity().getSharedPreferences("userInformation", 0);
        String getToken = _objpref.getString("jwttoken", "");
        int idCompany = getCompanyUser.getInt("id_perushaan", 0);
        Log.d("donefragment", ""+idCompany);
        Call<ResponseData> getData = api.getUserDoneData(idCompany, "Bearer " + getToken);
        getData.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful()) {
                    Log.d("RETRO", "RESPONSE : " + response.body().getDoneData());
                    listDone = response.body().getDoneData();
                    Log.d("datauser", "" + listDone);
                    RvDoneAdapter mAdapter = new RvDoneAdapter(listDone, getContext());
                    rvDone.setAdapter(mAdapter);
                    mAdapter.setClick(new RvDoneAdapter.ItemClick() {
                        @Override
                        public void onItemClicked(DoneData datadone) {
                            Toast.makeText(getActivity(), "" + datadone.getPriority(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    mAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }

            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "Unknown System Error, Please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void addListDoneAdmin() {
        SharedPreferences _objpref = getActivity().getSharedPreferences("JWTTOKEN", 0);
        String getToken = _objpref.getString("jwttoken", "missing");
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<ResponseData> getData = api.getAdminDoneData("Bearer " + getToken);
        getData.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful()) {
                    listDone = response.body().getDoneData();
                    RvDoneAdapter mAdapter = new RvDoneAdapter(listDone, getContext());
                    rvDone.setAdapter(mAdapter);
                    mAdapter.setClick(new RvDoneAdapter.ItemClick() {
                        @Override
                        public void onItemClicked(DoneData datadone) {
                            Toast.makeText(getActivity(), "" + datadone.getPriority(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    mAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }

            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "Unknown System Error, Please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }
}