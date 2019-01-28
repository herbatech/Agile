package com.ads.agile.utils;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import com.ads.agile.Agile;

public class AgileService extends JobService {

    private static final String TAG = AgileService.class.getSimpleName();

    @Override
    public boolean onStartJob(JobParameters params) {

        Intent service = new Intent(getApplicationContext(), Agile.class);
        getApplicationContext().startService(service);
        Log.d(TAG,"AgileService job started");

        //UtilConfig.scheduleJob(getApplicationContext()); // reschedule the job

        /*if(UtilConfig.isJobServiceOn(getApplicationContext())) {
            UtilConfig.scheduleJob(getApplicationContext()); // reschedule the job
        }
        else {
            Log.d(TAG,"Service with job id "+JOB_ID+" already running");
        }*/

        //completeJob(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    public void completeJob(final JobParameters parameters) {
        try {
            //This task takes 5 seconds to complete.
            Log.d(TAG,"(completeJob) thread sleep called");
            Thread.sleep(5000);
            initTask();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d(TAG,"(completeJob) catch error = "+e.getMessage());
        } finally {
            //Tell the framework that the job has completed and doesnot needs to be reschedule
            Log.d(TAG,"(completeJob) finally block executed");
            jobFinished(parameters, true);
        }
    }

    //start operation
    private void initTask() {

        try
        {
            Log.d(TAG,"(InitTask) init called");

//            Intent broadCastIntent = new Intent();
//            broadCastIntent.setAction(Agile.ROOM_ACTION);
//            broadCastIntent.putExtra(NEW_DATA, "yes");
//            sendBroadcast(broadCastIntent);

            Intent intent = new Intent();
            intent.setAction("com.journaldev.AN_INTENT");
            intent.setComponent(new ComponentName(getPackageName(),"com.ads.agile.utils.AgileReceiver"));
            getApplicationContext().sendBroadcast(intent);

        }
        catch (Exception e)
        {
            Log.d(TAG,"(InitTask) catch error = "+e.getMessage());
        }
    }
}