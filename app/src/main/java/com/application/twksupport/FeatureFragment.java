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
import com.application.twksupport.adapter.RvFeatureAdapter;
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
    private RecyclerView.Adapter mAdapter;
    private List<FeatureData> listFeature = new ArrayList<>();
    ProgressDialog pd;

    public FeatureFragment() {

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
        view = inflater.inflate(R.layout.fragment_feature, container, false);
        rvFeature = (RecyclerView) view.findViewById(R.id.rv_feature);
        rvFeature.setLayoutManager(new LinearLayoutManager(getContext()));
        pd.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                addListDataFeature();
            }
        },50);

        return view;
    }


    public void addListDataFeature(){
        ApiService api = ApiClient.getClient().create(ApiService.class);
        SharedPreferences getEmailUser = getActivity().getSharedPreferences("JWTTOKEN", 0);
        String email = getEmailUser.getString("email", "not Authenticated");
        Call<ResponseData> getData = api.getUserFeatureData(email);
        getData.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                Log.d("RETRO", "RESPONSE : " + response.body().getFeatureData());
                listFeature = response.body().getFeatureData();
                mAdapter = new RvFeatureAdapter(listFeature, getContext());
                rvFeature.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                pd.hide();
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                pd.hide();
                Log.d("RETRO", "FAILED : respon gagal");
                Toast.makeText(getActivity(), "Unknown System Error, Please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }
}