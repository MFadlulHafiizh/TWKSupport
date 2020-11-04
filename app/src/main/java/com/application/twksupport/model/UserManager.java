package com.application.twksupport.model;

import android.content.Context;
import android.content.SharedPreferences;


public class UserManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int mode = 0;
    private Context context;
    private static final String REFNAME = "userInformation";
    private static final String KEY_ID = "id";
    private static final String KEY_PHOTO = "photo";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";
    private static final String KEY_NOHP = "no_hp";

    public UserManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(REFNAME, mode);
        editor = sharedPreferences.edit();
    }

    public void addUserInformation(String id, int photo, String name, String email, String role, String no_hp){
        editor.putString(KEY_ID, id);
        editor.putInt(KEY_PHOTO, photo);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_NOHP, no_hp);
        editor.apply();
    }
}
