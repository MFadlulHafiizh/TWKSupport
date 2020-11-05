package com.application.twksupport.model;

public class AppsUserData {
    private String apps_name;

    public void setApps_name(String apps_name) {
        this.apps_name = apps_name;
    }

    public String getApps_name() {
        return apps_name;
    }

    public AppsUserData (String appName){
        this.apps_name = appName;
    }
    public String toString(){
        return apps_name;
    }
}
