package com.application.twksupport.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.application.twksupport.R;
import com.application.twksupport.model.DoneData;

import java.util.List;

public class RvDoneAdapter extends RecyclerView.Adapter<RvDoneAdapter.MyViewHolder> {

    private Activity activity;
    private List<DoneData> mDone;
    private ItemClick click;

    public RvDoneAdapter(List<DoneData> mDone, Activity activity) {
        this.activity = activity;
        this.mDone = mDone;
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
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        DoneData dt = mDone.get(position);
        if (dt.getPriority().equals("High")){
            holder.tv_priority.setTextColor(Color.parseColor("#D13C4F"));
        }
        else if(dt.getPriority().equals("Middle")){
            holder.tv_priority.setTextColor(Color.parseColor("#A1C349"));
        }
        else if(dt.getPriority().equals("Low")){
            holder.tv_priority.setTextColor(Color.parseColor("#809CFF"));
        }

        holder.tv_priority.setText(dt.getPriority());
        holder.tv_subject.setText(dt.getSubject());
        holder.tv_status.setText(dt.getStatus());
        holder.app_name.setText(dt.getApps_name());
        holder.date.setText(dt.getUpdated_at());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click.onItemClicked(mDone.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDone.size();
    }

    public interface ItemClick{
        void onItemClicked(DoneData datadone);
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
