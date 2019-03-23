package com.ads.agile;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;


public class AgileApplication   {

    private AgileApplication() {}

    private static AgileApplication sAgileApplication;

    public static  String TAG= AgileApplication.class.getSimpleName();

   /* public static AgileApplication getInstance()
    {
        if (sAgileApplication == null)
        {
            sAgileApplication = new AgileApplication();
        }

        return sAgileApplication;
    }*/


    public static void init(Application application)
    {

        application.registerActivityLifecycleCallbacks(AgileApplication.lifecycleCallbacks);


    }


    private static Application.ActivityLifecycleCallbacks lifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {

        private AgileLog agileLog;
        private AgileTransaction agileTransaction;
        boolean screen_on=false;
        private int numStarted = 0;
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            agileTransaction = new AgileTransaction(activity, (FragmentActivity) activity, "ag_transaction");
            agileLog = new AgileLog(activity, (FragmentActivity) activity, agileTransaction);
            Log.d(TAG, "onActivityCreated");

        }
        @Override
        public void onActivityStarted(Activity activity) {
            agileLog.agileAppStart();
            agileLog.agileInstall();

            if (numStarted == 0) {
                //app went to foreground

                Log.d(TAG,"onActivityStarted  = "+numStarted);
            }
            else {
                Log.d(TAG,"onActivityStarted1  = "+numStarted);
            }
            numStarted++;


        }

        @Override
        public void onActivityResumed(Activity activity) {
            Log.d(TAG,"onActivityResumed");

            if (screen_on){
                agileLog.agileAppScreenOn();
               // Log.d(TAG,"agileAppScreenOn");
            }


        }

        @Override
        public void onActivityPaused(Activity activity) {

            Log.d(TAG,"onActivityPaused");

        }

        @Override
        public void onActivityStopped(Activity activity) {
            Log.d(TAG,"onActivityStopped");

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            Log.d(TAG,"onActivitySaveInstanceState");
            screen_on=true;
            agileLog.agileAppScreenOff();

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Log.d(TAG,"onActivityDestroyed");
            agileLog.agileAppScreenEnd();
            agileLog.sessionComplete();


        }

    };




}
