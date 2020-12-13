package com.application.twksupport;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.application.twksupport.RestApi.ApiClient;
import com.application.twksupport.RestApi.ApiService;
import com.application.twksupport.UIUX.UserInteraction;
import com.application.twksupport.adapter.RvStaffListAdapter;
import com.application.twksupport.adapter.RvTodoAdapter;
import com.application.twksupport.model.ResponseData;
import com.application.twksupport.model.TodoData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffToDoFragment extends Fragment {
    View view;
    private RecyclerView rvTodo;
    private RvTodoAdapter mAdapter;
    private List<TodoData> listjobs = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
    private TextView filterbutton;
    private static StaffToDoFragment instance;
    private int page = 1;
    private int last_page = 1;
    private String priority = null;
    private String apps_name = null;
    private String fromDate = null;
    private String untilDate = null;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    public StaffToDoFragment() {
    }

    public List<TodoData> getListjobs() {
        return listjobs;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public static StaffToDoFragment getInstance(){
        return instance;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setApps_name(String apps_name) {
        this.apps_name = apps_name;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public void setUntilDate(String untilDate) {
        this.untilDate = untilDate;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_staff_to_do, container, false);
        instance = this;
        //initializeComponentView
        rvTodo = view.findViewById(R.id.rv_todo);
        swipeRefreshLayout = view.findViewById(R.id.refresh_todo);
        filterbutton = view.findViewById(R.id.filter_todo);
        progressBar = view.findViewById(R.id.progresbar_todo);
        swipeRefreshLayout.setRefreshing(true);

        //prepareRecycleView
        mAdapter = new RvTodoAdapter(listjobs, getActivity());
        rvTodo.setLayoutManager(linearLayoutManager);
        rvTodo.setHasFixedSize(true);
        rvTodo.setAdapter(mAdapter);


        //launchMain
        getJobs();
        rvTodo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int visibleItemCount = linearLayoutManager.getChildCount();
                int pastVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                int total = mAdapter.getItemCount();
                if (page < last_page) {
                    if (visibleItemCount + pastVisibleItem >= total) {
                        page++;
                        progressBar.setVisibility(View.VISIBLE);
                        getJobs();
                    }
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        //onRefresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                priority = null;
                apps_name = null;
                fromDate = null;
                untilDate = null;
                listjobs.clear();
                mAdapter.notifyDataSetChanged();
                getJobs();
            }
        });

        //filter
        final UserInteraction userInteraction = new UserInteraction();
        filterbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInteraction.showPopupFilter(getActivity(), "extra_jobs");
            }
        });
        return view;
    }

    public void getJobs(){
        SharedPreferences _objresp = getActivity().getSharedPreferences("JWTTOKEN", 0);
        SharedPreferences getIdStaff = getActivity().getSharedPreferences("userInformation", 0);
        String tokenStaff = _objresp.getString("jwttoken", "");
        String idStaff = getIdStaff.getString("id", "");
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<ResponseData> jobsData = api.getJobs("Bearer "+tokenStaff, page, idStaff, priority, apps_name, fromDate, untilDate);
        jobsData.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMessage().equals("success")){
                    List<TodoData> responseBody = response.body().getTodoData();
                    listjobs.addAll(responseBody);
                    mAdapter.notifyDataSetChanged();
                    last_page = response.body().getStaff_todo_page_total();
                    if (page == last_page){
                        progressBar.setVisibility(View.GONE);
                    }else{
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    swipeRefreshLayout.setRefreshing(false);
                    mAdapter.setClick(new RvTodoAdapter.ItemClick() {
                        @Override
                        public void onItemClicked(TodoData jobdata) {
                            Intent toDetail = new Intent(getActivity(), DetailActivity.class);
                            toDetail.putExtra(DetailActivity.EXTRA_JOBS, jobdata);
                            startActivity(toDetail);
                        }
                    });
                }
                else if(response.isSuccessful() && response.body() != null && response.body().getMessage().equals("No Data Available")){
                    swipeRefreshLayout.setRefreshing(false);
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.d("staffjob", ""+response.body());
                    mAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                mAdapter.notifyDataSetChanged();
                Log.d("Staffjob", ""+t.getMessage());
            }
        });
    }
}