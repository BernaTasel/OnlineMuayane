package com.bernatasel.onlinemuayene.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bernatasel.onlinemuayene.MainActivity;
import com.bernatasel.onlinemuayene.MyApp;
import com.bernatasel.onlinemuayene.utils.MyFM;
import com.bernatasel.onlinemuayene.utils.UtilsPermissions;

public class FragmentBase extends MyFM.MyFragment {
    protected Handler handler = new Handler();

    protected MyApp getApp() {
        return MyApp.getInstance();
    }

    protected MainActivity getMA() {
        return getApp().ma;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getView().setBackgroundColor(Color.WHITE);
//        getView().setBackground(getResources().getDrawable(R.drawable.bg));
//        getView().setBackgroundColor(getResources().getColor(R.color.windowBackground));
        getView().setClickable(true);
        getView().setFocusable(true);
    }

    @Override
    public boolean onBack() {
        return getMA().myFM.popBackStack();
    }

    protected void showToast(String message) {
        getApp().showToast(message);
    }

    protected void etError(EditText et, String text) {
        et.requestFocus();
        et.setError(text);
    }

    protected void etClearError(EditText... editTexts) {
        for (EditText et : editTexts) {
            et.setError(null);
            et.clearFocus();
        }
    }

    protected void etClear(EditText... editTexts) {
        for (EditText et : editTexts) et.setText(null);
    }

    protected boolean permissionCameraCheckElseAsk() {
        if (!UtilsPermissions.checkPermissionCameraElseAsk(getMA())) {
            showToast("Camera Yetkisi Gerekli");
            return false;
        }
        return true;
    }

    protected boolean permissionRecordAudioCheckElseAsk() {
        if (!UtilsPermissions.checkPermissionRecordAudioElseAsk(getMA())) {
            showToast("RecordAudio Yetkisi Gerekli");
            return false;
        }
        return true;
    }

    protected void setHeaderTitle(String title) {
        getMA().setHeaderTitle(title);
    }

    public void updateContent() {
    }
}
