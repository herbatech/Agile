package com.ads.agile.utils;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import static com.ads.agile.AgileConfiguration.JOB_ID;

public class UtilConfig {

    private static final String TAG = UtilConfig.class.getSimpleName();

    public static void scheduleJob(Context context) {

        Log.d(TAG,"(UtilConfig) rescheduleJob called ");

        ComponentName serviceComponent = new ComponentName(context,AgileService.class.getName());

        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceComponent);
        //builder.setMinimumLatency(1 * 20000); // 20 sec wait at least
        //builder.setOverrideDeadline(3 * 1000); // 3 sec maximum delay

        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY); // require unmetered network
        builder.setRequiresDeviceIdle(false); // device should be idle
        builder.setRequiresCharging(false); // we don't care if the device is charging or not
        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

    public static boolean isJobServiceOn( Context context ) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService( Context.JOB_SCHEDULER_SERVICE ) ;

        boolean hasBeenScheduled = false ;

        for ( JobInfo jobInfo : scheduler.getAllPendingJobs() ) {
            if ( jobInfo.getId() == JOB_ID ) {
                hasBeenScheduled = true ;
                break ;
            }
        }
        return hasBeenScheduled ;
    }
}