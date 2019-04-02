package com.ads.agile;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import java.util.List;


public class AgileApplication   {

    private AgileApplication() {}
    public static  String TAG= AgileApplication.class.getSimpleName();

    public static void init(Application application)
    {
        application.registerActivityLifecycleCallbacks(AgileApplication.lifecycleCallbacks);
    }


    private static Application.ActivityLifecycleCallbacks lifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {


        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }
        @Override
        public void onActivityStarted(Activity activity) {

        }
        @Override
        public void onActivityResumed(Activity activity) {

        }
        @Override
        public void onActivityPaused(Activity activity) {

        }
        @Override
        public void onActivityStopped(Activity activity) {

        }
        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

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
