package com.application.twksupport.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.twksupport.R;
import com.application.twksupport.UIUX.TvShowListener;
import com.application.twksupport.model.BugsData;
import com.application.twksupport.model.UserData;

import java.util.ArrayList;
import java.util.List;

public class RvStaffListAdapter extends RecyclerView.Adapter<RvStaffListAdapter.MyViewHolder> {
    private List<UserData> stafflist;
    private ItemClick click;
    private TvShowListener tvShowListener;

    public RvStaffListAdapter(List<UserData> stafflist, TvShowListener tvShowListener) {
        this.stafflist = stafflist;
        this.tvShowListener = tvShowListener;
    }

    public void setClick(ItemClick click) {
        this.click = click;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_staff, parent, false);
        MyViewHolder vHolder = new MyViewHolder(v);

        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final UserData ud = stafflist.get(position);

        holder.tvStaffName.setText(ud.getName());
        holder.tvStaffEmail.setText(ud.getEmail());

        if (ud.isSelected){
            holder.staffrowContainer.setBackgroundResource(R.drawable.tv_show_selected_bg);
            holder.iconSelected.setVisibility(View.VISIBLE);
        }else{
            holder.staffrowContainer.setBackgroundColor(Color.WHITE);
            holder.iconSelected.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click.onItemClicked(stafflist.get(holder.getAdapterPosition()));
                if(ud.isSelected){
                    holder.staffrowContainer.setBackgroundColor(Color.WHITE);
                    holder.iconSelected.setVisibility(View.GONE);
                    ud.isSelected = false;
                    if (getSelectedStaff().size() == 0 ){
                        tvShowListener.onTvShowAction(false);
                    }
                }else {
                    holder.staffrowContainer.setBackgroundResource(R.drawable.tv_show_selected_bg);
                    holder.iconSelected.setVisibility(View.VISIBLE);
                    ud.isSelected = true;
                    tvShowListener.onTvShowAction(true);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return stafflist.size();
    }

    public List<UserData> getSelectedStaff(){
        List<UserData> selectedStaff = new ArrayList<>();
        for (UserData stafflist : stafflist){
            if (stafflist.isSelected){
                selectedStaff.add(stafflist);
            }
        }
        return selectedStaff;
    }

    public interface ItemClick{
        void onItemClicked(UserData datauser);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvStaffName, tvStaffEmail;
        LinearLayout staffrowContainer;
        ImageView iconSelected;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            staffrowContainer = itemView.findViewById(R.id.row_staff_container);
            tvStaffName = itemView.findViewById(R.id.staff_name);
            tvStaffEmail =itemView.findViewById(R.id.staff_email);
            iconSelected = itemView.findViewById(R.id.icon_selected);
        }
    }
}
