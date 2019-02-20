package com.herba.sdk.myapplication;

import android.app.Application;
import android.content.res.Configuration;

public class MyApplication extends Application {
    private static final String AF_DEV_KEY = "K2aMGPY3SkC9WckYUgHJ99";
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}