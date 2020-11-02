package com.application.twksupport.adapter;

import android.annotation.SuppressLint;
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

import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.MyViewHolder> {

    Context mContext;
    List<BugsData> mBugs;

    public RecycleViewAdapter(List<BugsData> mBugs, Context mContext) {
        this.mContext = mContext;
        this.mBugs = mBugs;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cardview_item_layout, parent, false);
        MyViewHolder vHolder = new MyViewHolder(v);

        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String priority = mBugs.get(position).getPriority();
        if (priority == "high"){
            holder.tv_priority.setTextColor(Color.parseColor("#D13C4F"));
        }
        else if(priority == "middle"){
            holder.tv_priority.setTextColor(Color.parseColor("#A1C349"));
        }
        else if(priority == "low"){
            holder.tv_priority.setTextColor(Color.parseColor("#809CFF"));
        }
        holder.tv_priority.setText(mBugs.get(position).getPriority());
        holder.tv_subject.setText(mBugs.get(position).getSubject());
        holder.tv_status.setText(mBugs.get(position).getStatus());
    }

    @Override
    public int getItemCount() {
        return mBugs.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_priority, tv_subject, tv_status;
        public MyViewHolder(View itemView) {
            super(itemView);

            tv_priority = (TextView) itemView.findViewById(R.id.priority);
            tv_subject = (TextView) itemView.findViewById(R.id.subject_item);
            tv_status = (TextView) itemView.findViewById(R.id.status);
        }
    }
}
