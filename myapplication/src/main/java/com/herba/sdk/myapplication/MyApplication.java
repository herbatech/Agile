package com.herba.sdk.myapplication;

import android.app.Application;
import android.content.res.Configuration;

import com.ads.agile.AgileApplication;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
          AgileApplication.init(this);
    }



}