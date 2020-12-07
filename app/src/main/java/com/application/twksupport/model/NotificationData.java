package com.application.twksupport.model;

import android.os.Parcel;
import android.os.Parcelable;

public class NotificationData implements Parcelable {
    private String id_notif;
    private String from;
    private String id_ticket;
    private String apps_name;
    private String type;
    private String priority;
    private String subject;
    private String detail;
    private String status;
    private String created_at;
    private int read_at;
    private String aproval_stat;

    protected NotificationData(Parcel in) {
        id_notif = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id_notif);
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
    }
}
