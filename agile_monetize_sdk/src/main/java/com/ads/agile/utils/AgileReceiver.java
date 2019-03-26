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


    }
}