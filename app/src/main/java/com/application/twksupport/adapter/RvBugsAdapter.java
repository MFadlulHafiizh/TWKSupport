package com.application.twksupport.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.twksupport.R;
import com.application.twksupport.RestApi.ApiClient;
import com.application.twksupport.RestApi.ApiService;
import com.application.twksupport.model.BugsData;
import com.application.twksupport.model.StaffResponse;
import com.application.twksupport.model.UserData;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.security.AccessController.getContext;

public class RvBugsAdapter extends RecyclerView.Adapter<RvBugsAdapter.MyViewHolder> {

    private Activity activity;
    private List<BugsData> mBugs;
    private ItemClick click;

    public RvBugsAdapter(List<BugsData> mBugs, Activity activity) {
        this.activity = activity;
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
        final BugsData bd = mBugs.get(position);
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
            SharedPreferences getRoleUser = activity.getSharedPreferences("userInformation", 0);
            final String role = getRoleUser.getString("role", "not Authenticated");
            //if (role.equals("client-head") || role.equals("client-staff")){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    click.onItemClicked(mBugs.get(holder.getAdapterPosition()));
                }
            });


        //}
        /*else if(role.equals("twk-head")){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext, R.style.AppBottomSheetDialogTheme);
                    View content = LayoutInflater.from(mContext).inflate(R.layout.fragment_detail_bottom_sheet,null);
                    if (bd.getStatus().equals("Reported")){
                        content.findViewById(R.id.bt_btn_assign).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                click.onItemClicked(mBugs.get(holder.getAdapterPosition()));
                                android.os.Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        bottomSheetDialog.dismiss();
                                    }
                                }, 100);

                            }
                        });
                    }
                    else{
                        content.findViewById(R.id.bt_btn_assign).setVisibility(View.INVISIBLE);
                    }
                    TextView bt_priority, bt_subject, bt_status, bt_app_name, bt_detail;
                    bt_priority = content.findViewById(R.id.bt_priorityDetail);
                    bt_app_name = content.findViewById(R.id.bt_appnameDetail);
                    bt_subject = content.findViewById(R.id.bt_subjectDetail);
                    bt_detail = content.findViewById(R.id.bt_detail_content);
                    bt_priority.setText(bd.getPriority());
                    bt_app_name.setText(bd.getApps_name());
                    bt_subject.setText(bd.getSubject());
                    bt_detail.setText(bd.getDetail());
                    bottomSheetDialog.setContentView(content);
                    bottomSheetDialog.show();
                }
            });
        }*/

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
