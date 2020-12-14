package com.application.twksupport;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.application.twksupport.auth.MainActivity;
import com.application.twksupport.model.BugsData;
import com.application.twksupport.model.ResponseData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BugsFragment extends Fragment {
    View view;
    private RecyclerView rvBugs;
    private RvBugsAdapter mAdapter;
    private List<BugsData> listBugs = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
    private static BugsFragment instance;
    private TextView filterbutton, txtErrorMessage;
    private Button tryAgain;
    private LinearLayout error_container;
    private int page = 1;
    private int last_page = 1;
    //filterRequest
    private String priority = null;
    private String apps_name = null;
    private String assigned = null;
    private String fromDate = null;
    private String untilDate = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;


    public List<BugsData> getListBugs() {
        return listBugs;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public static BugsFragment getInstance() {
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

    public RvBugsAdapter getmAdapter() {
        return mAdapter;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bugs, container, false);
        instance = this;

        //initialize ComponentView
        error_container = view.findViewById(R.id.error_frame);
        txtErrorMessage = view.findViewById(R.id.error_message);
        rvBugs = (RecyclerView) view.findViewById(R.id.rv_bugs);
        swipeRefreshLayout = view.findViewById(R.id.refresh_bug);
        tryAgain = view.findViewById(R.id.btn_tryAgain);
        filterbutton = view.findViewById(R.id.filter_fragment);
        progressBar = view.findViewById(R.id.progresbar_bug);
        swipeRefreshLayout.setRefreshing(true);

        //prepare recycleview
        mAdapter = new RvBugsAdapter(listBugs, getActivity());
        rvBugs.setLayoutManager(linearLayoutManager);
        rvBugs.setHasFixedSize(true);
        rvBugs.setAdapter(mAdapter);

        //launchMain
        SharedPreferences getRoleUser = getActivity().getSharedPreferences("userInformation", 0);
        final String role = getRoleUser.getString("role", "not Authenticated");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (role) {
                    case "twk-head":
                        addListAdminBug();
                        rvBugs.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                int visibleItemCount = linearLayoutManager.getChildCount();
                                int pastVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                                int total = mAdapter.getItemCount();
                                if (page < last_page) {
                                    if (visibleItemCount + pastVisibleItem >= total) {
                                        page++;
                                        progressBar.setVisibility(View.VISIBLE);
                                        addListAdminBug();
                                    }
                                }
                                super.onScrolled(recyclerView, dx, dy);
                            }
                        });
                        break;
                    default:
                        addListDataBugsUser();
                        rvBugs.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                int visibleItemCount = linearLayoutManager.getChildCount();
                                int pastVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                                int total = mAdapter.getItemCount();
                                if (page < last_page) {
                                    if (visibleItemCount + pastVisibleItem >= total) {
                                        page++;
                                        progressBar.setVisibility(View.VISIBLE);
                                        addListDataBugsUser();
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
                userInteraction.showPopupFilter(getActivity(), "extra_bugs");
            }
        });

        return view;
    }

    public void addListDataBugsUser() {
        error_container.setVisibility(View.GONE);
        ApiService api = ApiClient.getClient().create(ApiService.class);
        final SharedPreferences _objpref = getActivity().getSharedPreferences("JWTTOKEN", 0);
        SharedPreferences getCompanyUser = getActivity().getSharedPreferences("userInformation", 0);
        String getToken = _objpref.getString("jwttoken", "");
        int idCompany = getCompanyUser.getInt("id_perushaan", 0);
        Log.d("BugsFragment", "" + idCompany);
        Log.d("priorityselectbug", "prio : "+priority);
        Log.d("priorityselectbug", "prioapps : "+apps_name);
        Call<ResponseData> getData = api.getUserBugData(idCompany, page, "Bearer " + getToken, priority, apps_name);
        getData.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMessage().equals("success")) {
                    Log.d("RETRO", "RESPONSE : " + response.body().getBugData());
                    List<BugsData> responseBody = response.body().getBugData();
                    listBugs.addAll(responseBody);
                    mAdapter.notifyDataSetChanged();
                    last_page = response.body().getBug_page_total();
                    if (page == last_page){
                        progressBar.setVisibility(View.GONE);
                    }else{
                        progressBar.setVisibility(View.INVISIBLE);
                    }
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
                else if(response.isSuccessful() && response.body() != null && response.body().getMessage().equals("No Data Available")){
                    swipeRefreshLayout.setRefreshing(false);
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
                else {
                    swipeRefreshLayout.setRefreshing(false);
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), "Unauthorized", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Log.d("RETRO", "FAILED : respon gagal" + t.getMessage());
                error_container.setVisibility(View.VISIBLE);
                txtErrorMessage.setText("Can't connect to server, please check your internet connection");
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void addListAdminBug() {
        error_container.setVisibility(View.GONE);
        final SharedPreferences _objpref = getActivity().getSharedPreferences("JWTTOKEN", 0);
        String getToken = _objpref.getString("jwttoken", "");
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<ResponseData> getData = api.getAdminBugData(page,"Bearer " + getToken, priority, apps_name, assigned, fromDate, untilDate);
        getData.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMessage().equals("success")) {
                    Log.d("RETRO", "RESPONSE : " + response.body().getBugData());
                    List<BugsData> responseBody = response.body().getBugData();
                    listBugs.addAll(responseBody);
                    mAdapter.notifyDataSetChanged();
                    last_page = response.body().getBug_page_total();
                    if (page == last_page){
                        progressBar.setVisibility(View.GONE);
                    }else{
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    Log.d("lastpage", ""+last_page);
                    swipeRefreshLayout.setRefreshing(false);
                    mAdapter.setClick(new RvBugsAdapter.ItemClick() {
                        @Override
                        public void onItemClicked(BugsData databug) {
                            Intent toDetail = new Intent(getActivity(), DetailActivity.class);
                            toDetail.putExtra(DetailActivity.EXTRA_BUG, databug);
                            startActivity(toDetail);
                        }
                    });
                }else if(response.isSuccessful() && response.body() != null && response.body().getMessage().equals("No Data Available")){
                    swipeRefreshLayout.setRefreshing(false);
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
                else {
                    SweetAlertDialog sweet = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);
                    sweet.setTitleText("Your session has expired");
                    sweet.setContentText("Please login again");
                    sweet.setCanceledOnTouchOutside(false);
                    sweet.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            Intent login = new Intent(getActivity(), MainActivity.class);
                            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(login);
                            getActivity().finish();
                        }
                    });
                    swipeRefreshLayout.setRefreshing(false);
                    mAdapter.notifyDataSetChanged();
                    sweet.show();
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Log.d("RETRO", "FAILED : respon gagal" + t.getMessage());
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
                        listBugs.clear();
                        page = 1;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                addListAdminBug();
                            }
                        },20);
                        break;

                    default:
                        listBugs.clear();
                        page = 1;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                addListDataBugsUser();
                            }
                        },20);
                        break;
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Handler delay = new Handler();
                switch (role) {
                    case "twk-head":
                        page = 1;
                        listBugs.clear();
                        mAdapter.notifyDataSetChanged();
                        priority = null;
                        apps_name = null;
                        assigned = null;
                        fromDate = null;
                        untilDate = null;
                        delay.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                addListAdminBug();
                            }
                        }, 20);
                        break;

                    default:
                        page = 1;
                        listBugs.clear();
                        mAdapter.notifyDataSetChanged();
                        priority = null;
                        apps_name = null;
                        delay.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                addListDataBugsUser();
                            }
                        }, 20);
                        break;
                }
            }
        });

    }
}