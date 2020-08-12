package com.bernatasel.onlinemuayene.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class UtilsPermissions {
    public static final int PERMISSION_REQUEST_CAMERA = 1234;
    public static final int PERMISSION_REQUEST_RECORD_AUDIO = 1235;

    public static boolean checkPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkPermissionBase(Activity activity, String string, int requestCode) {
        if (checkPermission(activity, string)) return true;
        ActivityCompat.requestPermissions(activity, new String[]{string}, requestCode);
        return false;
    }

    public static boolean checkPermissionCameraElseAsk(Activity activity) {
        return checkPermissionBase(activity, Manifest.permission.CAMERA, PERMISSION_REQUEST_CAMERA);
    }

    public static boolean checkPermissionRecordAudioElseAsk(Activity activity) {
        return checkPermissionBase(activity, Manifest.permission.RECORD_AUDIO, PERMISSION_REQUEST_RECORD_AUDIO);
    }
}
