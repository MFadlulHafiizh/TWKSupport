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

import java.util.List;

public class RvBugsAdapter extends RecyclerView.Adapter<RvBugsAdapter.MyViewHolder> {

    private Context mContext;
    private List<BugsData> mBugs;
    private ItemClick click;

    public RvBugsAdapter(List<BugsData> mBugs, Context mContext) {
        this.mContext = mContext;
        this.mBugs = mBugs;
    }

    public void setClick(ItemClick click) {
        this.click = click;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item_layout, parent, false);
        MyViewHolder vHolder = new MyViewHolder(v);

        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        BugsData bd = mBugs.get(position);
        if (bd.getPriority().equals("High")){
            holder.tv_priority.setTextColor(Color.parseColor("#D13C4F"));
        }
        else if(bd.getPriority().equals("Middle")){
            holder.tv_priority.setTextColor(Color.parseColor("#A1C349"));
        }
        else if(bd.getPriority().equals("Low")){
            holder.tv_priority.setTextColor(Color.parseColor("#809CFF"));
        }
        holder.tv_priority.setText(bd.getPriority());
        holder.tv_subject.setText(bd.getSubject());
        holder.tv_status.setText(mBugs.get(position).getStatus());
        holder.app_name.setText(bd.getApps_name());
        holder.date.setText(bd.getCreated_at());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click.onItemClicked(mBugs.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBugs.size();
    }

    public interface ItemClick{
        void onItemClicked(BugsData databug);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_priority, tv_subject, tv_status, app_name, date;
        public MyViewHolder(View itemView) {
            super(itemView);

            tv_priority = (TextView) itemView.findViewById(R.id.priorityHome);
            tv_subject = (TextView) itemView.findViewById(R.id.subject_itemHome);
            tv_status = (TextView) itemView.findViewById(R.id.statusHome);
            app_name = itemView.findViewById(R.id.appnameHome);
            date = itemView.findViewById(R.id.dateHome);
        }
    }
}
