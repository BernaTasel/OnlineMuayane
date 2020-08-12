package com.bernatasel.onlinemuayene;

import android.widget.Toast;

import androidx.multidex.MultiDexApplication;

import com.bernatasel.onlinemuayene.pojo.firestore.user.FSUser;
import com.bernatasel.onlinemuayene.utils.MySP;
import com.bernatasel.onlinemuayene.utils.UtilsAndroid;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class MyApp extends MultiDexApplication {

    private static MyApp instance;

    public static MyApp getInstance() {
        return instance;
    }

    public MainActivity ma;
    private Toast toast;
    public MySP mySP;

    private FSUser loggedInPerson;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        mySP = new MySP(this);

        String loggedInUserJson = mySP.getString(MySP.LOGGED_IN_USER, null);
        if (loggedInUserJson != null) {
            Type type = new TypeToken<FSUser>() {
            }.getType();
            loggedInPerson = UtilsAndroid.jsonToObject(loggedInUserJson, type);
        }
    }

    public void showToast(String message, boolean isLong) {
        if (toast != null) toast.cancel();
        toast = Toast.makeText(this, message, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        toast.show();
    }

    public void setUser(FSUser person) {
        this.loggedInPerson = person;
        mySP.putString(MySP.LOGGED_IN_USER, UtilsAndroid.objectToJson(person));
    }

    public FSUser getLoggedInPerson() {
        return loggedInPerson;
    }

    public boolean isLoggedIn() {
        return loggedInPerson != null;
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        loggedInPerson = null;
        mySP.remove(MySP.LOGGED_IN_USER);
    }

    public void showToast(String message) {
        showToast(message, false);
    }

    public static void recordException(Throwable t) {
        t.printStackTrace();
        FirebaseCrashlytics.getInstance().recordException(t);
    }
}
