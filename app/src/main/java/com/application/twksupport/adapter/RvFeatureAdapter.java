package com.application.twksupport.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.application.twksupport.R;
import com.application.twksupport.model.DoneData;
import com.application.twksupport.model.FeatureData;
import java.util.List;

public class RvFeatureAdapter extends RecyclerView.Adapter<RvFeatureAdapter.MyViewHolder> {
    private Activity activity;
    private List<FeatureData> mFeature;
    private ItemClick click;

    public RvFeatureAdapter(List<FeatureData> mFeature, Activity activity) {
        this.activity = activity;
        this.mFeature = mFeature;
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click.onItemClicked(mFeature.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFeature.size();
    }

    public interface ItemClick{
        void onItemClicked(FeatureData datafeature);
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
