package com.application.twksupport.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FeatureData implements Parcelable {
    private String nama_perusahaan;
    private String id_ticket;
    private String apps_name;
    private String priority;
    private String subject;
    private String detail;
    private String status;
    private String created_at;
    private String time_periodic;
    private int price;
    private String aproval_stat;
    private String assign_at;

    public FeatureData(){

    }

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

    public String getTime_periodic() {
        return time_periodic;
    }

    public void setTime_periodic(String time_periodic) {
        this.time_periodic = time_periodic;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getAproval_stat() {
        return aproval_stat;
    }

    public void setAproval_stat(String aproval_stat) {
        this.aproval_stat = aproval_stat;
    }

    public String getAssign_at() {
        return assign_at;
    }

    public void setAssign_at(String assign_at) {
        this.assign_at = assign_at;
    }

    protected FeatureData(Parcel in) {
        nama_perusahaan = in.readString();
        apps_name = in.readString();
        id_ticket = in.readString();
        priority = in.readString();
        subject = in.readString();
        detail = in.readString();
        status = in.readString();
        created_at = in.readString();
        time_periodic = in.readString();
        price = in.readInt();
        aproval_stat = in.readString();
        assign_at = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nama_perusahaan);
        parcel.writeString(apps_name);
        parcel.writeString(id_ticket);
        parcel.writeString(priority);
        parcel.writeString(subject);
        parcel.writeString(detail);
        parcel.writeString(status);
        parcel.writeString(created_at);
        parcel.writeString(time_periodic);
        parcel.writeInt(price);
        parcel.writeString(aproval_stat);
        parcel.writeString(assign_at);
    }

    public static final Creator<FeatureData> CREATOR = new Creator<FeatureData>() {
        @Override
        public FeatureData createFromParcel(Parcel in) {
            return new FeatureData(in);
        }

        @Override
        public FeatureData[] newArray(int size) {
            return new FeatureData[size];
        }
    };
}
