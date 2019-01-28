package com.ads.agile.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AgileReceiver extends BroadcastReceiver {

    private String TAG = this.getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG,"AgileReceiver called , action is = "+intent.getAction());

        //UtilConfig.scheduleJob(context);

//        //send data from the receiver to the service
//        Intent intent1 = new Intent();
//        intent1.putExtra("data","hello World !");
//        intent1.setAction("com.journaldev.AN_INTENT");
//        intent1.setComponent(new ComponentName(context.getPackageName(),"com.ads.agile.utils.AgileReceiver"));
//        context.sendBroadcast(intent1);

        /*if(UtilConfig.isJobServiceOn(context)) {
            UtilConfig.scheduleJob(context);
        }
        else {
            Log.d(TAG,"Service with job id "+JOB_ID+" already running");
        }*/
    }
}