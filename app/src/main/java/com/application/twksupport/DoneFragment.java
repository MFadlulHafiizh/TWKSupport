package com.application.twksupport;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
    private RecyclerView.Adapter mAdapter;
    private List<DoneData> listDone = new ArrayList<>();
    ProgressDialog pd;

    public DoneFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_done, container, false);
        rvDone = (RecyclerView) view.findViewById(R.id.rv_done);
        rvDone.setLayoutManager(new LinearLayoutManager(getContext()));
        pd.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                addListDataDone();
            }
        }, 50);


        return view;
    }

    protected void addListDataDone(){
        SharedPreferences getEmailUser = getActivity().getSharedPreferences("userInformation", 0);
        String email = getEmailUser.getString("email", "not Authenticated");
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<ResponseData> getData = api.getUserDoneData(email);
        getData.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                Log.d("RETRO", "RESPONSE : " + response.body().getDoneData());
                listDone = response.body().getDoneData();
                mAdapter = new RvDoneAdapter(listDone, getContext());
                rvDone.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                pd.hide();
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                pd.hide();
                Toast.makeText(getActivity(), "Error, check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }
}