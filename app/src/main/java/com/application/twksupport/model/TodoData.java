package com.application.twksupport.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TodoData implements Parcelable {
    private String id_assignment;
    private String id_ticket;
    private String deadline;
    private String apps_name;
    private String type;
    private String priority;
    private String subject;
    private String detail;
    private String status;
    private String created_at;

    protected TodoData(Parcel in) {
        id_assignment = in.readString();
        id_ticket = in.readString();
        deadline = in.readString();
        apps_name = in.readString();
        type = in.readString();
        priority = in.readString();
        subject = in.readString();
        detail = in.readString();
        status = in.readString();
        created_at = in.readString();
    }

    public static final Creator<TodoData> CREATOR = new Creator<TodoData>() {
        @Override
        public TodoData createFromParcel(Parcel in) {
            return new TodoData(in);
        }

        @Override
        public TodoData[] newArray(int size) {
            return new TodoData[size];
        }
    };

    public String getId_assignment() {
        return id_assignment;
    }

    public void setId_assignment(String id_assignment) {
        this.id_assignment = id_assignment;
    }

    public String getId_ticket() {
        return id_ticket;
    }

    public void setId_ticket(String id_ticket) {
        this.id_ticket = id_ticket;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getApps_name() {
        return apps_name;
    }

    public void setApps_name(String apps_name) {
        this.apps_name = apps_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id_assignment);
        dest.writeString(id_ticket);
        dest.writeString(deadline);
        dest.writeString(apps_name);
        dest.writeString(type);
        dest.writeString(priority);
        dest.writeString(subject);
        dest.writeString(detail);
        dest.writeString(status);
        dest.writeString(created_at);
    }
}
