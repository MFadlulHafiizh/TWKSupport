package com.application.twksupport.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.twksupport.R;
import com.application.twksupport.model.BugsData;
import com.application.twksupport.model.FeatureData;

import java.util.List;

public class RvFeatureAdapter extends RecyclerView.Adapter<RvFeatureAdapter.MyViewHolder> {
    private Context context;
    private List<FeatureData> mFeature;

    public RvFeatureAdapter(List<FeatureData> mFeature, Context context) {
        this.context = context;
        this.mFeature = mFeature;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item_layout, parent, false);
        MyViewHolder vHolder = new MyViewHolder(v);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FeatureData fd = mFeature.get(position);
        if (fd.getPriority().equals("High")){
            holder.tv_priority.setTextColor(Color.parseColor("#D13C4F"));
        }
        else if(fd.getPriority().equals("Middle")){
            holder.tv_priority.setTextColor(Color.parseColor("#A1C349"));
        }
        else if(fd.getPriority().equals("Low")){
            holder.tv_priority.setTextColor(Color.parseColor("#809CFF"));
        }
        holder.tv_priority.setText(fd.getPriority());
        holder.tv_subject.setText(fd.getSubject());
        holder.tv_status.setText(fd.getStatus());
        holder.app_name.setText(fd.getApps_name());
        holder.date.setText(fd.getCreated_at());
    }

    @Override
    public int getItemCount() {
        return mFeature.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_priority, tv_subject, tv_status, app_name, date;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_priority = (TextView) itemView.findViewById(R.id.priorityHome);
            tv_subject = (TextView) itemView.findViewById(R.id.subject_itemHome);
            tv_status = (TextView) itemView.findViewById(R.id.statusHome);
            app_name = itemView.findViewById(R.id.appnameHome);
            date = itemView.findViewById(R.id.dateHome);
        }
    }
}
