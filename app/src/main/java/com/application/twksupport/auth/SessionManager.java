package com.application.twksupport.auth;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int mode = 0;
    private  static final String REFNAME = "JWTTOKEN";
    private static final String KEY_USER_EMAIL = "email";
    private static final String KEY_JWT_TOKEN = "jwttoken";
    private Context context;

    public SessionManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(REFNAME,mode);
        editor = sharedPreferences.edit();
    }

    public void createSession(String email, String jwtvalue){
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_JWT_TOKEN, jwtvalue);
        editor.commit();
    }



}
