package com.application.twksupport.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.application.twksupport.R;
import com.application.twksupport.model.NotificationData;
import java.util.List;

public class RvNotificationAdapter extends RecyclerView.Adapter<RvNotificationAdapter.MyViewHolder> {
    private Context context;
    private List<NotificationData> mNotif;
    private ItemClick click;

    public RvNotificationAdapter(List<NotificationData> mNotif, Context context) {
        this.context = context;
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
        if (nd.getRead_at() == 1){
            holder.imgNotifIndicator.setVisibility(View.GONE);
        }else {
            holder.imgNotifIndicator.setVisibility(View.VISIBLE);
        }

        SharedPreferences getRoleUser = context.getSharedPreferences("userInformation", 0);
        final String role = getRoleUser.getString("role", "not Authenticated");
        if (role.equals("twk-staff")){
            holder.txtPriority.setText(nd.getPriority());
            holder.txtSubject.setText(nd.getSubject());
            holder.txtAppname.setText(nd.getApps_name());
            holder.txtDate.setText(nd.getUpdated_at());
            if (nd.getType().equals("Report") && !nd.getStatus().equals("Done")){
                holder.txtStatus.setText("Bugs report");
            }else if (nd.getType().equals("Request") && !nd.getStatus().equals("Done")){
                holder.txtStatus.setText("Feature request");
            }else {
                holder.txtStatus.setText(nd.getStatus());
            }
        }
        else {
            holder.txtPriority.setText(nd.getPriority());
            holder.txtSubject.setText(nd.getSubject());
            holder.txtAppname.setText(nd.getApps_name());
            holder.txtStatus.setText(nd.getStatus());
            holder.txtDate.setText(nd.getUpdated_at());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onItemClicked(mNotif.get(holder.getAdapterPosition()));
                holder.imgNotifIndicator.setVisibility(View.GONE);
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
        private TextView txtPriority, txtSubject, txtAppname, txtStatus, txtDate;
        private ImageView imgNotifIndicator;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPriority = itemView.findViewById(R.id.priority_notif);
            txtSubject = itemView.findViewById(R.id.subject_notif);
            txtAppname = itemView.findViewById(R.id.appname_notif);
            txtStatus = itemView.findViewById(R.id.status_notif);
            imgNotifIndicator = itemView.findViewById(R.id.notif_indicator);
            txtDate = itemView.findViewById(R.id.notif_date);
        }
    }
}
