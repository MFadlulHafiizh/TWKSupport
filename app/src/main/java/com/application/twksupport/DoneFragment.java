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
import android.widget.Button;
import android.widget.LinearLayout;
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
    private List<DoneData> listDone = new ArrayList<>();
    private TextView filterbutton, txtErrorMessage;
    private Button tryAgain;
    private LinearLayout error_container;
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
        filterbutton = view.findViewById(R.id.filter_fragment);
        error_container = view.findViewById(R.id.error_frame);
        txtErrorMessage = view.findViewById(R.id.error_message);
        tryAgain = view.findViewById(R.id.btn_tryAgain);

        final UserInteraction userInteraction = new UserInteraction();

        filterbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userInteraction.showPopupFilter(getActivity());
            }
        });
        rvDone = (RecyclerView) view.findViewById(R.id.rv_done);
        swipeRefreshLayout = view.findViewById(R.id.refresh_hasDone);
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

                    case "twk-staff":
                        addListStaffHasDone();
                        break;

                    default:
                        break;
                }
            }
        }, 50);

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

                    case "twk-staff":
                        addListStaffHasDone();
                        break;

                    default:
                        break;
                }
            }
        });
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeRefreshLayout.setRefreshing(true);
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

                    case "twk-staff":
                        addListStaffHasDone();
                        break;

                    default:
                        break;
                }
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
                            Intent toDetail = new Intent(getActivity(), DetailActivity.class);
                            toDetail.putExtra(DetailActivity.EXTRA_DONE, datadone);
                            startActivity(toDetail);
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
        Call<ResponseData> staffDone = api.getStaffDoneData("Bearer "+getToken, idStaff);
        staffDone.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful()){
                    listDone = response.body().getHasDone();
                    Log.d("RETRO", "resultSuccess : "+response.body());
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
}