package com.application.twksupport.adapter;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.twksupport.R;
import com.application.twksupport.model.AppsUserData;

import java.util.List;

public class RvAppsCompanyAdapter extends RecyclerView.Adapter<RvAppsCompanyAdapter.MyViewHolder> {
    private List<AppsUserData> listApps;

    public RvAppsCompanyAdapter(List<AppsUserData> listApps) {
        this.listApps = listApps;
    }

    public RvAppsCompanyAdapter() {
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_apps_company, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AppsUserData aud = listApps.get(position);
        holder.apps_name.setText(aud.getApps_name());
    }

    @Override
    public int getItemCount() {
        return listApps.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView apps_name;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            apps_name = itemView.findViewById(R.id.apps_name_row);
        }
    }
}
