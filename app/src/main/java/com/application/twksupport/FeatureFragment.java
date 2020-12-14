package com.application.twksupport;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.application.twksupport.RestApi.ApiClient;
import com.application.twksupport.RestApi.ApiService;
import com.application.twksupport.UIUX.UserInteraction;
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
    private RvFeatureAdapter mAdapter;
    private List<FeatureData> listFeature = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
    private TextView filterbutton, txtErrorMessage;
    private Button tryAgain;
    private LinearLayout error_container;
    private static FeatureFragment instance;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int page = 1;
    private int last_page = 1;
    private String priority = null;
    private String apps_name = null;
    private String assigned = null;
    private String fromDate = null;
    private String untilDate = null;
    private ProgressBar progressBar;

    public List<FeatureData> getListFeature() {
        return listFeature;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public static FeatureFragment getInstance(){
        return instance;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setApps_name(String apps_name) {
        this.apps_name = apps_name;
    }

    public void setAssigned(String assigned) {
        this.assigned = assigned;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public void setUntilDate(String untilDate) {
        this.untilDate = untilDate;
    }

    public RvFeatureAdapter getmAdapter() {
        return mAdapter;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_feature, container, false);
        instance = this;

        //initializeComponent
        error_container = view.findViewById(R.id.error_frame);
        txtErrorMessage = view.findViewById(R.id.error_message);
        rvFeature = (RecyclerView) view.findViewById(R.id.rv_feature);
        swipeRefreshLayout = view.findViewById(R.id.refresh_feature);
        tryAgain = view.findViewById(R.id.btn_tryAgain);
        filterbutton = view.findViewById(R.id.filter_fragment);
        progressBar = view.findViewById(R.id.progresbar_feature);
        swipeRefreshLayout.setRefreshing(true);

        //prepeareRecycleView
        mAdapter = new RvFeatureAdapter(listFeature, getActivity());
        rvFeature.setLayoutManager(linearLayoutManager);
        rvFeature.setHasFixedSize(true);
        rvFeature.setAdapter(mAdapter);

        //LaunchMain
        SharedPreferences getEmailUser = getActivity().getSharedPreferences("userInformation", 0);
        final String role = getEmailUser.getString("role", "not Authenticated");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (role) {
                    case "twk-head":
                        addListAdminFeature();
                        rvFeature.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                int visibleItemCount = linearLayoutManager.getChildCount();
                                int pastVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                                int total = mAdapter.getItemCount();
                                if (page < last_page) {
                                    if (visibleItemCount + pastVisibleItem >= total) {
                                        page++;
                                        progressBar.setVisibility(View.VISIBLE);
                                        addListAdminFeature();
                                    }
                                }
                                super.onScrolled(recyclerView, dx, dy);
                            }
                        });
                        break;
                    default:
                        addListDataFeatureUser();
                        rvFeature.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                int visibleItemCount = linearLayoutManager.getChildCount();
                                int pastVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                                int total = mAdapter.getItemCount();
                                if (page < last_page) {
                                    if (visibleItemCount + pastVisibleItem >= total) {
                                        page++;
                                        progressBar.setVisibility(View.VISIBLE);
                                        addListDataFeatureUser();
                                    }
                                }
                                super.onScrolled(recyclerView, dx, dy);
                            }
                        });
                        break;
                }
            }
        }, 10);

        //onRefresh
        refreshData(swipeRefreshLayout, role);

        //filter
        final UserInteraction userInteraction = new UserInteraction();
        filterbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userInteraction.showPopupFilter(getActivity(), "extra_feature");
            }
        });

        return view;
    }

    public void addListDataFeatureUser(){
        error_container.setVisibility(View.GONE);
        ApiService api = ApiClient.getClient().create(ApiService.class);
        SharedPreferences getCompanyUser = getActivity().getSharedPreferences("userInformation", 0);
        SharedPreferences _objpref = getActivity().getSharedPreferences("JWTTOKEN", 0);
        String getToken = _objpref.getString("jwttoken", "missing");
        int idCompany = getCompanyUser.getInt("id_perushaan", 0);
        Call<ResponseData> getData = api.getUserFeatureData(idCompany, page,"Bearer "+getToken, priority, apps_name);
        getData.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMessage().equals("success")){
                    Log.d("RETRO", "RESPONSE : " + response.body().getFeatureData());
                    List<FeatureData> responseBody = response.body().getFeatureData();
                    listFeature.addAll(responseBody);
                    mAdapter.notifyDataSetChanged();
                    last_page = response.body().getFitur_page_total();
                    if (page == last_page){
                        progressBar.setVisibility(View.GONE);
                    }else{
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    Log.d("lastpage", ""+last_page);
                    swipeRefreshLayout.setRefreshing(false);
                    mAdapter.setClick(new RvFeatureAdapter.ItemClick() {
                        @Override
                        public void onItemClicked(FeatureData datafeature) {
                            Intent toDetail = new Intent(getActivity(), DetailActivity.class);
                            toDetail.putExtra(DetailActivity.EXTRA_FEATURE, datafeature);
                            startActivity(toDetail);
                        }
                    });
                }
                else if(response.isSuccessful() && response.body() != null && response.body().getMessage().equals("No Data Available")){
                    Toast.makeText(getActivity(), ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    mAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }
                else {
                    Toast.makeText(getActivity(), "Unatourized", Toast.LENGTH_SHORT).show();
                    mAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }

            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Log.d("RETRO", "FAILED : respon gagal"+t.getMessage());
                error_container.setVisibility(View.VISIBLE);
                txtErrorMessage.setText("Can't connect to server, please check your internet connection");
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void addListAdminFeature(){
        error_container.setVisibility(View.GONE);
        ApiService api = ApiClient.getClient().create(ApiService.class);
        SharedPreferences _objpref = getActivity().getSharedPreferences("JWTTOKEN", 0);
        String getToken = _objpref.getString("jwttoken", "");
        Call<ResponseData> getData = api.getAdminFeatureData(page,"Bearer "+getToken, priority, apps_name, assigned, fromDate, untilDate);
        Log.d("ovovavase", "prio : "+priority);
        Log.d("ovovavase", "prioapp : "+apps_name);
        Log.d("ovovavase", "prioassign : "+assigned);
        Log.d("ovovavase", "priofromdate : "+fromDate);
        Log.d("ovovavase", "priountildate : "+untilDate);
        getData.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMessage().equals("success")){
                    Log.d("RETRO", "RESPONSE : " + response.body().getFeatureData());
                    List<FeatureData> responseBody = response.body().getFeatureData();
                    listFeature.addAll(responseBody);
                    mAdapter.notifyDataSetChanged();
                    last_page = response.body().getFitur_page_total();
                    if (page == last_page){
                        progressBar.setVisibility(View.GONE);
                    }else{
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    Log.d("lastpage", ""+last_page);
                    swipeRefreshLayout.setRefreshing(false);
                    mAdapter.setClick(new RvFeatureAdapter.ItemClick() {
                        @Override
                        public void onItemClicked(FeatureData datafeature) {
                            Intent toDetail = new Intent(getActivity(), DetailActivity.class);
                            toDetail.putExtra(DetailActivity.EXTRA_FEATURE, datafeature);
                            startActivity(toDetail);
                        }
                    });
                }else if(response.isSuccessful() && response.body() != null && response.body().getMessage().equals("No Data Available")){
                    swipeRefreshLayout.setRefreshing(false);
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "Unatourized", Toast.LENGTH_SHORT).show();
                    mAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Log.d("RETRO", "FAILED : respon gagal"+t.getMessage());
                error_container.setVisibility(View.VISIBLE);
                txtErrorMessage.setText("Can't connect to server, please check your internet connection");
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void refreshData(final SwipeRefreshLayout swipeRefreshLayout, final String role){

        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                swipeRefreshLayout.setRefreshing(true);
                switch (role) {
                    case "twk-head":
                        listFeature.clear();
                        page = 1;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                addListAdminFeature();
                            }
                        },20);
                        break;

                    default:
                        listFeature.clear();
                        page = 1;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                addListDataFeatureUser();
                            }
                        },20);
                        break;
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Handler handler = new Handler();
                switch (role) {
                    case "twk-head":
                        page = 1;
                        priority = null;
                        apps_name = null;
                        assigned = null;
                        fromDate = null;
                        untilDate = null;
                        if (listFeature!=null){
                            listFeature.clear();
                            mAdapter.notifyDataSetChanged();
                        }
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                addListAdminFeature();
                            }
                        }, 20);
                        break;

                    default:
                        page = 1;
                        priority = null;
                        apps_name = null;
                        if (listFeature != null){
                            listFeature.clear();
                            mAdapter.notifyDataSetChanged();
                        }
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                addListDataFeatureUser();
                            }
                        },20);
                        break;
                }
            }
        });


    }
}