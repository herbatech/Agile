package com.ads.agile.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class AgileReceiver extends BroadcastReceiver {

    private String TAG = this.getClass().getSimpleName();
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

      /*  String action = intent.getAction();
        Log.d(TAG,"Action  ="+action);

        if (!Intent.ACTION_PACKAGE_ADDED.equals(action)
                && !Intent.ACTION_PACKAGE_REMOVED.equals(action)) {

            return;
        }
      *//*  String packageName = context.getPackageNameFromIntent(intent);
        if (TextUtils.isEmpty(packageName)) {
            return;
        }*//*
        boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
        if (replacing) {
            return;
        }*/


        this.context = context;

        // when package removed
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            Log.e(" BroadcastReceiver ", "onReceive called "
                    + " PACKAGE_REMOVED ");
            Toast.makeText(context, " onReceive !!!! PACKAGE_REMOVED",
                    Toast.LENGTH_LONG).show();

        }
        // when package installed
        else if (intent.getAction().equals(
                "android.intent.action.PACKAGE_ADDED")) {

            Log.e(" BroadcastReceiver ", "onReceive called " + "PACKAGE_ADDED");
            Toast.makeText(context, " onReceive !!!!." + "PACKAGE_ADDED",
                    Toast.LENGTH_LONG).show();

        }



    }
}