package com.bernatasel.onlinemuayene.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;

public class UtilsAndroid {
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isEmail(String email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public static String objectToJson(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public static <T> T jsonToObject(String jsonString, Type type) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, type);
    }

    public static AlertDialog showAlertDialog(Context context, String title, String message, boolean cancelable,
                                              Integer positiveButtonTextId, Integer neutralButtonTextId, Integer negativeButtonTextId,
                                              DialogInterface.OnClickListener positiveButton, DialogInterface.OnClickListener neutralButton, DialogInterface.OnClickListener negativeButton) {
        MaterialAlertDialogBuilder madb = new MaterialAlertDialogBuilder(context);

        if (title != null) madb.setTitle(title);
        if (message != null) madb.setMessage(message);
        madb.setCancelable(cancelable);
        if (positiveButtonTextId != null)
            madb.setPositiveButton(positiveButtonTextId, positiveButton);
        if (neutralButtonTextId != null) madb.setNeutralButton(neutralButtonTextId, neutralButton);
        if (negativeButtonTextId != null)
            madb.setNegativeButton(negativeButtonTextId, negativeButton);

        return madb.show();
    }

    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] decodedString = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
