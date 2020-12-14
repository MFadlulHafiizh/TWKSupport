package com.application.twksupport.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.application.twksupport.NotificationActivity;
import com.application.twksupport.R;
import com.application.twksupport.auth.SessionManager;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.content.ContentValues.TAG;

public class NotificationServices extends FirebaseMessagingService {
    private SessionManager sessionManager;
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("onNewToken", "RefreshedToken:"+s);
        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.createNotificationReceiver(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "From : "+remoteMessage.getFrom());

        if (remoteMessage.getNotification() != null){
            Log.d(TAG, "Message notification body: "+remoteMessage.getNotification().getBody());
            showNotification(remoteMessage);
        }
    }

    private String channelId = "Default";
    private void showNotification(RemoteMessage remoteMessage) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent toNotif = new Intent(this, NotificationActivity.class);
        toNotif.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, toNotif, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(alarmSound)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel =new NotificationChannel(channelId, "Default Channel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{1000, 1000, 1000, 1000, 1000});
            manager.createNotificationChannel(channel);
        }
        manager.notify(0, builder.build());
    }
}
