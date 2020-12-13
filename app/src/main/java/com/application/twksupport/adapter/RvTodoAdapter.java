package com.application.twksupport.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.application.twksupport.R;
import com.application.twksupport.model.TodoData;

import java.util.List;

public class RvTodoAdapter extends RecyclerView.Adapter<RvTodoAdapter.MyViewHolder> {

    private Activity activity;
    private List<TodoData> todoList;
    private ItemClick click;

    public RvTodoAdapter( List<TodoData> todoList, Activity activity) {
        this.activity = activity;
        this.todoList = todoList;
    }

    public void setClick(ItemClick click){
        this.click = click;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item_layout, parent, false);
        MyViewHolder vHolder = new MyViewHolder(view);

        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        TodoData td = todoList.get(position);
        if (td.getPriority().equals("High")){
            holder.tv_priority.setTextColor(Color.parseColor("#D13C4F"));
        }
        else if(td.getPriority().equals("Middle")){
            holder.tv_priority.setTextColor(Color.parseColor("#A1C349"));
        }
        else if(td.getPriority().equals("Low")){
            holder.tv_priority.setTextColor(Color.parseColor("#809CFF"));
        }
        Log.d("adapterjob", ""+td.getApps_name());
        holder.tv_priority.setText(td.getPriority());
        holder.tv_subject.setText(td.getSubject());
        if (td.getType().equals("Report")){
            holder.tv_status.setText("Bugs report");
        }else {
            holder.tv_status.setText("Feature request");
        }
        holder.date.setText(td.getCreated_at());
        holder.app_name.setText(td.getApps_name());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onItemClicked(todoList.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public interface ItemClick{
        void onItemClicked(TodoData jobdata);
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
