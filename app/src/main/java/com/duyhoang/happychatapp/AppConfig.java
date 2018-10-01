package com.duyhoang.happychatapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;


/**
 * Created by rogerh on 8/27/2018.
 */

public class AppConfig extends Application {

    private static final String PREF_UID = "PREF_UID";
    private static final String PREF_EMAIL = "PREF_EMAIL";
    private static final String PREF_DISPLAY_NAME = "PREF_DISPLAY_NAME";
    private static final String PREF_PHOTO_URL = "PREF_PHOTO_URL";



    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static Context context;



    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("preferences.xml", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        context = getApplicationContext();

        /*if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);*/

    }


    public static void saveLocalUserAccount(FirebaseUser firebaseUser) {

        editor.putString(PREF_UID, firebaseUser.getUid());
        editor.putString(PREF_EMAIL, firebaseUser.getEmail());
        editor.putString(PREF_DISPLAY_NAME, firebaseUser.getDisplayName());
        if(firebaseUser.getPhotoUrl() != null) editor.putString(PREF_PHOTO_URL, firebaseUser.getPhotoUrl().toString());
        else editor.putString(PREF_PHOTO_URL, null);
        editor.commit();
    }

    public static Context getAppContext() {
        return context;
    }

}
