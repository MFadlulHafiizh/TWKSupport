package com.application.twksupport.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class DoneData implements Parcelable {
    private String nama_perusahaan;
    private String id_ticket;
    private String type;
    private String apps_name;
    private String priority;
    private String subject;
    private String detail;
    private String status;
    private String updated_at;
    private String created_at;
    private String dead_line;
    private String time_periodic;
    private int price;
    private String assign_at;

    protected DoneData(Parcel in) {
        nama_perusahaan = in.readString();
        id_ticket = in.readString();
        type = in.readString();
        apps_name = in.readString();
        priority = in.readString();
        subject = in.readString();
        detail = in.readString();
        status = in.readString();
        created_at = in.readString();
        dead_line = in.readString();
        price = in.readInt();
        assign_at = in.readString();
        updated_at = in.readString();
        time_periodic = in.readString();
    }

    public static final Creator<DoneData> CREATOR = new Creator<DoneData>() {
        @Override
        public DoneData createFromParcel(Parcel in) {
            return new DoneData(in);
        }

        @Override
        public DoneData[] newArray(int size) {
            return new DoneData[size];
        }
    };

    public String getNama_perusahaan() {
        return nama_perusahaan;
    }

    public void setNama_perusahaan(String nama_perusahaan) {
        this.nama_perusahaan = nama_perusahaan;
    }

    public String getApps_name() {
        return apps_name;
    }

    public String getId_ticket() {
        return id_ticket;
    }

    public void setId_ticket(String id_ticket) {
        this.id_ticket = id_ticket;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setApps_name(String apps_name) {
        this.apps_name = apps_name;
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

    public String getDead_line() {
        return dead_line;
    }

    public void setDead_line(String dead_line) {
        this.dead_line = dead_line;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getAssign_at() {
        return assign_at;
    }

    public void setAssign_at(String assign_at) {
        this.assign_at = assign_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getTime_periodic() {
        return time_periodic;
    }

    public void setTime_periodic(String time_periodic) {
        this.time_periodic = time_periodic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nama_perusahaan);
        dest.writeString(id_ticket);
        dest.writeString(type);
        dest.writeString(apps_name);
        dest.writeString(priority);
        dest.writeString(subject);
        dest.writeString(detail);
        dest.writeString(status);
        dest.writeString(created_at);
        dest.writeString(dead_line);
        dest.writeInt(price);
        dest.writeString(assign_at);
        dest.writeString(updated_at);
        dest.writeString(time_periodic);
    }
}
