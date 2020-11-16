package com.application.twksupport.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StaffResponse {
    @SerializedName("user")
    private List<UserData> user;

    public List<UserData> getUser() {
        return user;
    }

    public void setUser(List<UserData> user) {
        this.user = user;
    }
}
