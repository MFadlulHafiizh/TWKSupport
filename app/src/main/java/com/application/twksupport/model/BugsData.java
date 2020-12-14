package com.application.twksupport.model;

import android.os.Parcel;
import android.os.Parcelable;

public class BugsData implements Parcelable {
    private String nama_perusahaan;
    private String id_ticket;
    private String apps_name;
    private String type;
    private String priority;
    private String subject;
    private String detail;
    private String status;
    private String created_at;
    private String assign_at;


    public BugsData(){

    }

    protected BugsData(Parcel in) {
        nama_perusahaan = in.readString();
        id_ticket = in.readString();
        apps_name = in.readString();
        type = in.readString();
        priority = in.readString();
        subject = in.readString();
        detail = in.readString();
        status = in.readString();
        created_at = in.readString();
        assign_at = in.readString();
    }

    public static final Creator<BugsData> CREATOR = new Creator<BugsData>() {
        @Override
        public BugsData createFromParcel(Parcel in) {
            return new BugsData(in);
        }

        @Override
        public BugsData[] newArray(int size) {
            return new BugsData[size];
        }
    };

    public String getNama_perusahaan() {
        return nama_perusahaan;
    }

    public void setNama_perusahaan(String nama_perusahaan) {
        this.nama_perusahaan = nama_perusahaan;
    }

    public String getId_ticket() {
        return id_ticket;
    }

    public void setId_ticket(String id_ticket) {
        this.id_ticket = id_ticket;
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

    public String getAssign_at() {
        return assign_at;
    }

    public void setAssign_at(String assign_at) {
        this.assign_at = assign_at;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nama_perusahaan);
        parcel.writeString(id_ticket);
        parcel.writeString(apps_name);
        parcel.writeString(type);
        parcel.writeString(priority);
        parcel.writeString(subject);
        parcel.writeString(detail);
        parcel.writeString(status);
        parcel.writeString(created_at);
        parcel.writeString(assign_at);
    }
}
