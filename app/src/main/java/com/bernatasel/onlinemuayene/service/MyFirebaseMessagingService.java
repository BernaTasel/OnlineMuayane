package com.bernatasel.onlinemuayene.service;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bernatasel.onlinemuayene.utils.MyFCM;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private LocalBroadcastManager localBroadcastManager;

    @Override
    public void onCreate() {
        super.onCreate();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();

        if (data.size() > 0) {
            Intent intent = new Intent(MyFCM.INTENT_ACTION);
            for (Map.Entry<String, String> entry : data.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                intent.putExtra(key, value);
            }
            localBroadcastManager.sendBroadcast(intent);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
//        Log.d(TAG, "Refreshed token: " + token);

        Intent intent = new Intent(MyFCM.INTENT_ACTION);
        intent.putExtra(MyFCM.BUNDLE_TOKEN, token);
        localBroadcastManager.sendBroadcast(intent);
    }
}