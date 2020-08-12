package com.bernatasel.onlinemuayene.utils;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bernatasel.onlinemuayene.MyApp;
import com.bernatasel.onlinemuayene.R;
import com.google.firebase.iid.FirebaseInstanceId;

public class MyFCM {
    public static final String INTENT_ACTION = "INTENT_FCM";
    public static final String BUNDLE_TOKEN = "new_token";

    public interface IMyFCM {
        void onMessageReceived(@NonNull Bundle bundle);

        void onToken(String token);
    }

    private final IMyFCM iMyFCM;

    public MyFCM(Activity activity, IMyFCM iMyFCM) {
        this.iMyFCM = iMyFCM;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = activity.getString(R.string.default_notification_channel_id);
            String channelName = activity.getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager = activity.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW));
        }

        Bundle bundle = activity.getIntent().getExtras();
        if (bundle != null) iMyFCM.onMessageReceived(bundle);
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String newToken = bundle.getString(BUNDLE_TOKEN);
                if (newToken != null) {
                    iMyFCM.onToken(newToken);
                } else {
                    iMyFCM.onMessageReceived(bundle);
                }
            }
        }
    };

    public void onStart(Context context) {
        LocalBroadcastManager.getInstance(context).registerReceiver((broadcastReceiver), new IntentFilter(INTENT_ACTION));
    }

    public void onStop(Context context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
    }

    public void requestInstanceId() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Exception e = task.getException();
                        if (e != null) MyApp.recordException(e);
                        return;
                    }
                    String token = task.getResult().getToken();
                    iMyFCM.onToken(token);
                });
    }
}
