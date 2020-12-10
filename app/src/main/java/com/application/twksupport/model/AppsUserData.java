package com.application.twksupport.model;

public class AppsUserData {
    public static final String defaultChoose = "Choose your apps";
    private String apps_name;
    private int id_apps;

    public String getDefaultChoose() {
        return defaultChoose;
    }

    public void setApps_name(String apps_name) {
        this.apps_name = apps_name;
    }

    public String getApps_name() {
        return apps_name;
    }

    public int getId_apps() {
        return id_apps;
    }

    public void setId_apps(int id_apps) {
        this.id_apps = id_apps;
    }

    public AppsUserData (String appName, int id_apps){
        this.apps_name = appName;
        this.id_apps = id_apps;
    }
    public String toString(){
        return apps_name;
    }
}
