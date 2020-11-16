package com.application.twksupport.model;

import android.content.Context;
import android.content.SharedPreferences;

public class CacheData {
    //for assignment
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int mode = 0;
    private Context context;
    private static final String REFNAME = "assignment";
    private static final String KEY_ID = "staff_id";
    private static final String KEY_TICKET= "ticket_id";

    public CacheData(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(REFNAME, mode);
        editor = sharedPreferences.edit();
    }

    public void assignmentSetIdStaff(String staff_id){
        editor.putString(KEY_ID, staff_id);
        editor.apply();
    }

    public void assignmentSetIdTicket(String ticket_id){
        editor.putString(KEY_TICKET, ticket_id);
        editor.apply();
    }
}
