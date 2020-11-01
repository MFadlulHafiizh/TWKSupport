package com.application.twksupport.model;

import com.google.gson.annotations.SerializedName;

public class TokenResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("token")
    private String token;

    @SerializedName("role")
    private String role;

    public TokenResponse(){

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }
}
