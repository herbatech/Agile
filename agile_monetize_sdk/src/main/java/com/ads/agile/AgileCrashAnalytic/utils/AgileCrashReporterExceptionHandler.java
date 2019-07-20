package com.ads.agile.AgileCrashAnalytic.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.ads.agile.AgileEventParameter;
import com.ads.agile.AgileEventType;
import com.ads.agile.AgileLog;
import com.ads.agile.AgileTransaction;

public class AgileCrashReporterExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler exceptionHandler;
    public Activity context1;
   // AgileCrash agileCrash;
    AgileLog agileCrash;
   // Activity activity;
    AgileTransaction agileTransaction;
    private static final String TAG = AgileCrashReporterExceptionHandler.class.getSimpleName();
    public AgileCrashReporterExceptionHandler(Activity context) {
        this.exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        this.context1=context;


        //agileCrash = new AgileCrash(context1);

        agileTransaction = new AgileTransaction(context1, (FragmentActivity) context1, AgileEventType.AGILE_EVENT_TRANSACTION);
        agileCrash = new AgileLog(context1,(FragmentActivity) context1, agileTransaction);

    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {

         Log.d(TAG,"Log Message  ="+ throwable.getLocalizedMessage());

        agileCrash.set(AgileEventParameter.AGILE_PARAMS_CRASH,throwable.getLocalizedMessage());
        agileCrash.trackEvent(AgileEventType.AGILE_EVENT_CRASH);

        exceptionHandler.uncaughtException(thread, throwable);

    }

}
