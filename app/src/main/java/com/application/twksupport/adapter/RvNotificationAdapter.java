package com.application.twksupport.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.twksupport.R;
import com.application.twksupport.model.NotificationData;

import java.util.List;

public class RvNotificationAdapter extends RecyclerView.Adapter<RvNotificationAdapter.MyViewHolder> {
    private List<NotificationData> mNotif;
    private ItemClick click;

    public RvNotificationAdapter(List<NotificationData> mNotif) {
        this.mNotif = mNotif;
    }

    public void setClick(ItemClick click) {
        this.click = click;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notif_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final NotificationData nd = mNotif.get(position);
        if (nd.getPriority().equals("High")){
            holder.txtPriority.setTextColor(Color.parseColor("#D13C4F"));
        }else if(nd.getPriority().equals("Middle")){
            holder.txtPriority.setTextColor(Color.parseColor("#A1C349"));
        }else if(nd.getPriority().equals("Low")){
            holder.txtPriority.setTextColor(Color.parseColor("#809CFF"));
        }
        holder.txtPriority.setText(nd.getPriority());
        holder.txtSubject.setText(nd.getSubject());
        holder.txtAppname.setText(nd.getApps_name());
        holder.txtStatus.setText(nd.getStatus());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onItemClicked(mNotif.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotif.size();
    }

    public interface ItemClick{
        void onItemClicked(NotificationData notifData);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtPriority, txtSubject, txtAppname, txtStatus;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtPriority = itemView.findViewById(R.id.priority_notif);
            txtSubject = itemView.findViewById(R.id.subject_notif);
            txtAppname = itemView.findViewById(R.id.appname_notif);
            txtStatus = itemView.findViewById(R.id.status_notif);
        }
    }
}
