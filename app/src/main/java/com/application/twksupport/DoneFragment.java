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
    private RvDoneAdapter mAdapter;
    private List<DoneData> listDone = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
    private TextView filterbutton, txtErrorMessage;
    private Button tryAgain;
    private LinearLayout error_container;
    private int page = 1;
    private int last_page = 1;
    private ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_done, container, false);

        //initializeComponentView
        error_container = view.findViewById(R.id.error_frame);
        txtErrorMessage = view.findViewById(R.id.error_message);
        rvDone = (RecyclerView) view.findViewById(R.id.rv_done);
        swipeRefreshLayout = view.findViewById(R.id.refresh_hasDone);
        tryAgain = view.findViewById(R.id.btn_tryAgain);
        filterbutton = view.findViewById(R.id.filter_fragment);
        progressBar = view.findViewById(R.id.progressbar_done);
        swipeRefreshLayout.setRefreshing(true);

        //prepare RecycleView
        mAdapter = new RvDoneAdapter(listDone, getActivity());
        rvDone.setLayoutManager(linearLayoutManager);
        rvDone.setHasFixedSize(true);
        rvDone.setAdapter(mAdapter);

        //LaunchMain
        SharedPreferences getEmailUser = getActivity().getSharedPreferences("userInformation", 0);
        final String role = getEmailUser.getString("role", "not Authenticated");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (role) {
                    case "twk-head":
                        addListDoneAdmin();
                        rvDone.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                int visibleItemCount = linearLayoutManager.getChildCount();
                                int pastVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                                int total = mAdapter.getItemCount();
                                if (page < last_page){
                                    if (visibleItemCount + pastVisibleItem >= total){
                                        page++;
                                        progressBar.setVisibility(View.VISIBLE);
                                        addListDoneAdmin();
                                    }
                                }
                                super.onScrolled(recyclerView, dx, dy);
                            }
                        });
                        break;

                    case "twk-staff":
                        addListStaffHasDone();
                        rvDone.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                int visibleItemCount = linearLayoutManager.getChildCount();
                                int pastVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                                int total = mAdapter.getItemCount();
                                if (page < last_page){
                                    if (visibleItemCount + pastVisibleItem >= total){
                                        page++;
                                        progressBar.setVisibility(View.VISIBLE);
                                        addListStaffHasDone();
                                    }
                                }
                                super.onScrolled(recyclerView, dx, dy);
                            }
                        });
                        break;

                    default:
                        addListDataDone();
                        rvDone.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                int visibleItemCount = linearLayoutManager.getChildCount();
                                int pastVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                                int total = mAdapter.getItemCount();
                                if (page < last_page){
                                    if (visibleItemCount + pastVisibleItem >= total){
                                        page++;
                                        progressBar.setVisibility(View.VISIBLE);
                                        addListDataDone();
                                    }
                                }
                                super.onScrolled(recyclerView, dx, dy);
                            }
                        });
                        break;
                }
            }
        }, 10);
        final UserInteraction userInteraction = new UserInteraction();

        //onRefresh
        refreshData(swipeRefreshLayout, role);

        //filter
        filterbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userInteraction.showPopupFilter(getActivity(), "extra_done");
            }
        });

        return view;
    }

    protected void addListDataDone() {
        error_container.setVisibility(View.GONE);
        ApiService api = ApiClient.getClient().create(ApiService.class);
        final SharedPreferences _objpref = getActivity().getSharedPreferences("JWTTOKEN", 0);
        SharedPreferences getCompanyUser = getActivity().getSharedPreferences("userInformation", 0);
        String getToken = _objpref.getString("jwttoken", "");
        int idCompany = getCompanyUser.getInt("id_perushaan", 0);
        Log.d("donefragment", "" + idCompany);
        Call<ResponseData> getData = api.getUserDoneData(idCompany, page,"Bearer " + getToken);
        getData.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful()) {
                    Log.d("RETRO", "RESPONSE : " + response.body().getDoneData());
                    List<DoneData> responseBody = response.body().getDoneData();
                    listDone.addAll(responseBody);
                    mAdapter.notifyDataSetChanged();
                    last_page = response.body().getDone_page_total();
                    if (page == last_page){
                        progressBar.setVisibility(View.GONE);
                    }else {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    Log.d("totalpage", ""+last_page);
                    swipeRefreshLayout.setRefreshing(false);

                    mAdapter.setClick(new RvDoneAdapter.ItemClick() {
                        @Override
                        public void onItemClicked(DoneData datadone) {
                            Intent toDetail = new Intent(getActivity(), DetailActivity.class);
                            toDetail.putExtra(DetailActivity.EXTRA_DONE, datadone);
                            startActivity(toDetail);
                        }
                    });
                } else {
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

    protected void addListDoneAdmin() {
        error_container.setVisibility(View.GONE);
        SharedPreferences _objpref = getActivity().getSharedPreferences("JWTTOKEN", 0);
        String getToken = _objpref.getString("jwttoken", "missing");
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<ResponseData> getData = api.getAdminDoneData(page,"Bearer " + getToken);
        getData.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful()) {
                    Log.d("RETRO", "RESPONSE : " + response.body().getDoneData());
                    List<DoneData> responseBody = response.body().getDoneData();
                    listDone.addAll(responseBody);
                    mAdapter.notifyDataSetChanged();
                    last_page = response.body().getDone_page_total();
                    if (page == last_page){
                        progressBar.setVisibility(View.GONE);
                    }else {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    Log.d("totalpage", ""+last_page);
                    swipeRefreshLayout.setRefreshing(false);
                    mAdapter.setClick(new RvDoneAdapter.ItemClick() {
                        @Override
                        public void onItemClicked(DoneData datadone) {
                            Intent toDetail = new Intent(getActivity(), DetailActivity.class);
                            toDetail.putExtra(DetailActivity.EXTRA_DONE, datadone);
                            startActivity(toDetail);
                        }
                    });
                } else {
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

    protected void addListStaffHasDone(){
        error_container.setVisibility(View.GONE);
        final SharedPreferences _objpref = getActivity().getSharedPreferences("JWTTOKEN", 0);
        SharedPreferences getCompanyUser = getActivity().getSharedPreferences("userInformation", 0);
        String getToken = _objpref.getString("jwttoken", "");
        String idStaff = getCompanyUser.getString("id", "");
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<ResponseData> staffDone = api.getStaffDoneData(page, "Bearer "+getToken, idStaff);
        staffDone.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful()){
                    Log.d("RETRO", "RESPONSE : " + response.body().getHasDone());
                    List<DoneData> responseBody = response.body().getHasDone();
                    listDone.addAll(responseBody);
                    mAdapter.notifyDataSetChanged();
                    last_page = response.body().getStaff_done_page_total();
                    if (page == last_page){
                        progressBar.setVisibility(View.GONE);
                    }else {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    Log.d("totalpage", ""+last_page);
                    swipeRefreshLayout.setRefreshing(false);
                    mAdapter.setClick(new RvDoneAdapter.ItemClick() {
                        @Override
                        public void onItemClicked(DoneData datadone) {
                            Intent toDetail = new Intent(getActivity(), DetailActivity.class);
                            toDetail.putExtra(DetailActivity.EXTRA_DONE, datadone);
                            startActivity(toDetail);
                        }
                    });

                }else{
                    Log.d("RETRO", "resultFail : "+response.body());
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

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                switch (role) {
                    case "twk-head":
                        listDone.clear();
                        page = 1;
                        addListDoneAdmin();
                        break;

                    case "twk-staff":
                        listDone.clear();
                        page = 1;
                        addListStaffHasDone();
                        break;

                    default:
                        listDone.clear();
                        page = 1;
                        addListDataDone();
                        break;
                }
            }
        });
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeRefreshLayout.setRefreshing(true);
                switch (role) {
                    case "twk-head":
                        listDone.clear();
                        page = 1;
                        addListDoneAdmin();
                        break;

                    case "twk-staff":
                        listDone.clear();
                        page = 1;
                        addListStaffHasDone();
                        break;

                    default:
                        listDone.clear();
                        page = 1;
                        addListDataDone();
                        break;
                }
            }
        });

    }
}