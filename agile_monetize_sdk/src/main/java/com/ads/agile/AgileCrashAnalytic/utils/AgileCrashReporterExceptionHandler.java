package com.ads.agile.AgileCrashAnalytic.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;

import com.ads.agile.AgileCrash;
import com.ads.agile.AgileEventParameter;
import com.ads.agile.AgileEventType;
import com.ads.agile.AgileTransaction;

public class AgileCrashReporterExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler exceptionHandler;
    public Context context1;
    AgileCrash agileCrash;
   // AgileLog agileCrash;
    Activity activity;
    AgileTransaction agileTransaction;
    private static final String TAG = AgileCrashReporterExceptionHandler.class.getSimpleName();
    public AgileCrashReporterExceptionHandler(Context context) {
        this.exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        this.context1=context;

        agileCrash = new AgileCrash(context1);

       /* if(context1 instanceof FragmentActivity){
            agileTransaction = new AgileTransaction(context1.getApplicationContext(), (FragmentActivity) context1, AgileEventType.AGILE_EVENT_TRANSACTION);
            agileCrash = new AgileLog(context1.getApplicationContext(),(FragmentActivity) context1, agileTransaction);
        }*/



    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {

        agileCrash.set(AgileEventParameter.AGILE_PARAMS_CRASH,throwable.getLocalizedMessage());
        agileCrash.trackEvent(AgileEventType.AGILE_EVENT_CRASH);
        exceptionHandler.uncaughtException(thread, throwable);

    }

    public Activity getActivity(Context context)
    {
        if (context == null)
        {
            return null;
        }
        else if (context instanceof ContextWrapper)
        {
            if (context instanceof Activity)
            {
                return (Activity) context;
            }
            else
            {
                return getActivity(((ContextWrapper) context).getBaseContext());
            }
        }

        return null;
    }
}
