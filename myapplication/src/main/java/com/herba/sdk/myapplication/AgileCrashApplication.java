package com.herba.sdk.myapplication;

import android.app.Application;

import com.ads.agile.AgileCrashAnalytic.AgileCrashReporter;

/**
 * Created by bali on 02/08/17.
 */

public class AgileCrashApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {

         //   AgileCrashReporter.initialize(this);
        }
    }
}
