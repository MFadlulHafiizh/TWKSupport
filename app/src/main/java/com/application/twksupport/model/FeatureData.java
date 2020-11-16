package com.application.twksupport.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FeatureData implements Parcelable {
    private String id_ticket;
    private String apps_name;
    private String priority;
    private String subject;
    private String detail;
    private String status;
    private String created_at;
    private String time_periodic;
    private String price;

    public FeatureData(){

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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


    protected FeatureData(Parcel in) {
        apps_name = in.readString();
        id_ticket = in.readString();
        priority = in.readString();
        subject = in.readString();
        detail = in.readString();
        status = in.readString();
        created_at = in.readString();
        time_periodic = in.readString();
        price = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(apps_name);
        parcel.writeString(id_ticket);
        parcel.writeString(priority);
        parcel.writeString(subject);
        parcel.writeString(detail);
        parcel.writeString(status);
        parcel.writeString(created_at);
        parcel.writeString(time_periodic);
        parcel.writeString(price);
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
