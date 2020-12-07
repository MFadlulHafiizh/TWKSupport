package com.application.twksupport.notification;

import android.content.SharedPreferences;
import android.util.Log;

import com.application.twksupport.auth.SessionManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import static android.content.ContentValues.TAG;
public class NotifFirebase extends FirebaseInstanceIdService {
    private SessionManager sessionManager;

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.createNotificationReceiver(refreshedToken);
        Log.d("TAG", "Refreshed token: " + refreshedToken);
    }
}
