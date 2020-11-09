package com.application.twksupport.model;

import android.content.Context;
import android.content.SharedPreferences;

public class UserAppManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int mode = 0;
    private Context context;
    private static final String REFNAME = "UserAppManager";
    private static final String KEY_ID = "id_apps";
    private static final String KEY_APP_PRIO = "priority";

    public UserAppManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(REFNAME, mode);
        editor = sharedPreferences.edit();
    }

    public void setPriority(String priority){
        editor.putString(KEY_APP_PRIO, priority);
        editor.apply();
    }

    public void setIdApps(int id){
        editor.putInt(KEY_ID, id);
        editor.apply();
    }

}
