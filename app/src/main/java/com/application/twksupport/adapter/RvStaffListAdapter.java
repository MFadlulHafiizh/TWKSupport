package com.application.twksupport.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.twksupport.R;
import com.application.twksupport.model.BugsData;
import com.application.twksupport.model.UserData;

import java.util.List;

public class RvStaffListAdapter extends RecyclerView.Adapter<RvStaffListAdapter.MyViewHolder> {
    private List<UserData> stafflist;
    private ItemClick click;

    public RvStaffListAdapter(List<UserData> stafflist) {
        this.stafflist = stafflist;
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
        UserData ud = stafflist.get(position);

        holder.tvStaffName.setText(ud.getName());
        holder.tvStaffEmail.setText(ud.getEmail());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click.onItemClicked(stafflist.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return stafflist.size();
    }



    public interface ItemClick{
        void onItemClicked(UserData datauser);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvStaffName, tvStaffEmail;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvStaffName = itemView.findViewById(R.id.staff_name);
            tvStaffEmail =itemView.findViewById(R.id.staff_email);
        }
    }
}