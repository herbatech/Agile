package com.ads.agile;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.util.List;


public class AgileApplication   {

    private AgileApplication() {}

    public static  String TAG= AgileApplication.class.getSimpleName();
    public static void init(Application application)

    {



        application.registerActivityLifecycleCallbacks(AgileApplication.lifecycleCallbacks);


    }


    private static Application.ActivityLifecycleCallbacks lifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {

        private AgileLog agileLog;
        private AgileTransaction agileTransaction;
        boolean screen_on=false;
        private int numStarted = 0;
        SharedPreferences sharedpreferences;
        public static final String MyPREFERENCES = "myprefs";
        public static final  String value = "key";
        String i;


        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {


             sharedpreferences = activity.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
             SharedPreferences.Editor editor = sharedpreferences.edit();
             editor.putString(value, getLauncherActivityName(activity));
             editor.apply();
              i = sharedpreferences.getString(value, "");

            Log.d(TAG,"LauncherActivity   ="+  i);

            try {
                if (i.equalsIgnoreCase(getLauncherActivityName(activity))){

                    agileTransaction = new AgileTransaction(activity, (FragmentActivity) activity, "ag_transaction");
                    agileLog = new AgileLog(activity, (FragmentActivity) activity, agileTransaction);
                    Log.d(TAG, "onActivityCreated");
                }
            }
            catch (Exception e){

            }





        }
        @Override
        public void onActivityStarted(Activity activity) {

            try {
                if (i.equalsIgnoreCase(getLauncherActivityName(activity))){
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

            }
            catch (Exception e){

            }




        }

        @Override
        public void onActivityResumed(Activity activity) {

  try {
      if (i.equalsIgnoreCase(getLauncherActivityName(activity))){
          Log.d(TAG,"onActivityResumed");
          if (screen_on){
              agileLog.agileAppScreenOn();
              // Log.d(TAG,"agileAppScreenOn");
          }
      }
  }
  catch ( Exception e){


  }




        }

        @Override
        public void onActivityPaused(Activity activity) {

            if (i.equalsIgnoreCase(getLauncherActivityName(activity))){
                Log.d(TAG,"onActivityPaused");
            }


        }

        @Override
        public void onActivityStopped(Activity activity) {
            if (i.equalsIgnoreCase(getLauncherActivityName(activity))){
                Log.d(TAG,"onActivityStopped");
            }


        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            try {
            if (i.equalsIgnoreCase(getLauncherActivityName(activity))){
                Log.d(TAG,"onActivitySaveInstanceState");
                screen_on=true;
                agileLog.agileAppScreenOff();
            }
        }
        catch (Exception e){

        }


        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            try {
                if (i.equalsIgnoreCase(getLauncherActivityName(activity))){
                    Log.d(TAG,"onActivityDestroyed");
                    agileLog.set("param_screen_instance_count",numStarted);
                    agileLog.sessionComplete();
                    numStarted=0;

                }
            }
            catch ( Exception e){
            }

        }

    };

    private static String getLauncherActivityName(Activity activity){
        String activityName = "";
        final PackageManager pm = activity.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(activity.getPackageName());
        List<ResolveInfo> activityList = pm.queryIntentActivities(intent,0);
        if(activityList != null){
            activityName = activityList.get(0).activityInfo.name;
        }
        return activityName;
    }


}
