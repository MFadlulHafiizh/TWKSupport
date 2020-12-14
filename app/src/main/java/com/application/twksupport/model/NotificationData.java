package com.application.twksupport.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class NotificationData implements Parcelable {
    private String id_notif;
    private String nama_perusahaan;
    private String from;
    private String id_ticket;
    private String apps_name;
    private String type;
    private String priority;
    private String subject;
    private String detail;
    private String status;
    private String updated_at;
    private String created_at;
    private int price;
    private String time_periodic;
    private String dead_line;
    private int read_at;
    private String aproval_stat;
    private String assign_at;

    protected NotificationData(Parcel in) {
        id_notif = in.readString();
        nama_perusahaan = in.readString();
        from = in.readString();
        id_ticket = in.readString();
        apps_name = in.readString();
        type = in.readString();
        priority = in.readString();
        subject = in.readString();
        detail = in.readString();
        status = in.readString();
        created_at = in.readString();
        read_at = in.readInt();
        aproval_stat = in.readString();
        price = in.readInt();
        time_periodic = in.readString();
        dead_line = in.readString();
        updated_at = in.readString();
        assign_at = in.readString();
    }

    public static final Creator<NotificationData> CREATOR = new Creator<NotificationData>() {
        @Override
        public NotificationData createFromParcel(Parcel in) {
            return new NotificationData(in);
        }

        @Override
        public NotificationData[] newArray(int size) {
            return new NotificationData[size];
        }
    };

    public String getId_notif() {
        return id_notif;
    }

    public void setId_notif(String id_notif) {
        this.id_notif = id_notif;
    }

    public String getNama_perusahaan() {
        return nama_perusahaan;
    }

    public void setNama_perusahaan(String nama_perusahaan) {
        this.nama_perusahaan = nama_perusahaan;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
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

    public int getRead_at() {
        return read_at;
    }

    public void setRead_at(int read_at) {
        this.read_at = read_at;
    }

    public String getAproval_stat() {
        return aproval_stat;
    }

    public void setAproval_stat(String aproval_stat) {
        this.aproval_stat = aproval_stat;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getTime_periodic() {
        return time_periodic;
    }

    public void setTime_periodic(String time_periodic) {
        this.time_periodic = time_periodic;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getDead_line() {
        return dead_line;
    }

    public void setDead_line(String dead_line) {
        this.dead_line = dead_line;
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id_notif);
        dest.writeString(nama_perusahaan);
        dest.writeString(from);
        dest.writeString(id_ticket);
        dest.writeString(apps_name);
        dest.writeString(type);
        dest.writeString(priority);
        dest.writeString(subject);
        dest.writeString(detail);
        dest.writeString(status);
        dest.writeString(created_at);
        dest.writeInt(read_at);
        dest.writeString(aproval_stat);
        dest.writeInt(price);
        dest.writeString(time_periodic);
        dest.writeString(dead_line);
        dest.writeString(updated_at);
        dest.writeString(assign_at);
    }
}
