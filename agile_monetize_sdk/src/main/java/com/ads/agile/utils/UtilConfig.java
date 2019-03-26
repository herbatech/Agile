package com.ads.agile.utils;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static com.ads.agile.AgileConfiguration.JOB_ID;

public class UtilConfig {

    private static final String TAG = UtilConfig.class.getSimpleName();

    public final static String PREFS_NAME = "appname_prefs";

    private static Context context;

    public UtilConfig(Context context){
        this.context = context;
    }

    public static void setInt( String key, int value) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME,0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(String key, int val) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getInt(key, val);
    }

    public static void clearprefernce() {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME,0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();

    }

}