package com.application.twksupport;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.application.twksupport.RestApi.ApiClient;
import com.application.twksupport.RestApi.ApiService;
import com.application.twksupport.adapter.RvTodoAdapter;
import com.application.twksupport.model.ResponseData;
import com.application.twksupport.model.TodoData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffToDoFragment extends Fragment {
    View view;
    private RecyclerView rvTodo;
    private TextView filterbutton;
    private List<TodoData> listjobs;
    private SwipeRefreshLayout swipeRefreshLayout;

    public StaffToDoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_staff_to_do, container, false);

        rvTodo = view.findViewById(R.id.rv_todo);
        swipeRefreshLayout = view.findViewById(R.id.refresh_todo);
        swipeRefreshLayout.setRefreshing(true);
        rvTodo.setLayoutManager(new LinearLayoutManager(getContext()));
        getJobs();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getJobs();
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
        Call<ResponseData> jobsData = api.getJobs("Bearer "+tokenStaff, idStaff);
        jobsData.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (response.isSuccessful()){
                    listjobs = response.body().getTodoData();
                    Log.d("staffjob", ""+listjobs);
                    RvTodoAdapter mAdapter = new RvTodoAdapter(getContext(), listjobs);
                    rvTodo.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    rvTodo.smoothScrollToPosition(0);
                    mAdapter.setClick(new RvTodoAdapter.ItemClick() {
                        @Override
                        public void onItemClicked(TodoData jobdata) {
                            Intent toDetail = new Intent(getActivity(), DetailActivity.class);

                        }
                    });
                    swipeRefreshLayout.setRefreshing(false);
                }else{
                    Log.d("staffjob", ""+response.body());
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Log.d("Staffjob", ""+t.getMessage());
            }
        });
    }
}