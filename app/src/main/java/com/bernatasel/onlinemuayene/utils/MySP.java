package com.bernatasel.onlinemuayene.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.bernatasel.onlinemuayene.pojo.firestore.MyChatMessage;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MySP {

    public static final String CHAT_TITLES = "CHAT_TITLES";//DOCTORUID_PATIENTUID
    public static final String CHAT_PREFIX = "CHAT";
    public static final String NAME_PREFIX = "NAMESURNAME_";
    public static final String LOGGED_IN_USER = "LOGGED_IN_USER";

    private final SharedPreferences sp;

    public MySP(Context context) {
        sp = context.getSharedPreferences(context.getPackageName(), ContextWrapper.MODE_PRIVATE);
    }

    public String getString(String key, String defaultValue) {
        return sp.getString(key, defaultValue);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public ArrayList<String> getArrayListString(String key, Set<String> defaultValue) {
        Set<String> stringSet = sp.getStringSet(key, defaultValue);
        if (stringSet == null) return null;
        return new ArrayList<>(stringSet);
    }

    public void putArrayListString(String key, ArrayList<String> arr) {
        SharedPreferences.Editor editor = sp.edit();
        Set<String> set = new HashSet<>(arr);
        editor.putStringSet(key, set);
        editor.commit();
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

    //
    public String getNameSurname(String uid) {
        return getString(NAME_PREFIX + uid, null);
    }

    public void putNameSurname(String uid, String name, String surname) {
        putString(NAME_PREFIX + uid, name + " " + surname);
    }

    public @NonNull
    ArrayList<String> getChatTitles() {
        ArrayList<String> chatTitles = getArrayListString(CHAT_TITLES, null);
        if (chatTitles == null) chatTitles = new ArrayList<>();
        return chatTitles;
    }

    public ArrayList<MyChatMessage> getMyChatMessages(String doctorUid, String patientUid) {
        String chatData = sp.getString(CHAT_PREFIX + "_" + doctorUid + "_" + patientUid, null);
        if (chatData == null) return null;
        Type type = new TypeToken<ArrayList<MyChatMessage>>() {
        }.getType();
        return UtilsAndroid.jsonToObject(chatData, type);
    }

    public void putMyChatMessages(String doctorUid, String patientUid, ArrayList<MyChatMessage> chatMessages) {
        putString(CHAT_PREFIX + "_" + doctorUid + "_" + patientUid, UtilsAndroid.objectToJson(chatMessages));

        String title = doctorUid + "_" + patientUid;

        ArrayList<String> chatTitles = getChatTitles();
        if (!chatTitles.contains(title)) chatTitles.add(title);
        putArrayListString(CHAT_TITLES, chatTitles);
    }
}
