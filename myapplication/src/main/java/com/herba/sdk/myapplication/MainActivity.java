package com.herba.sdk.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.ads.agile.AgileLog;
import com.ads.agile.AgileTransaction;
import com.ads.agile.room.LogEntity;
import com.ads.agile.room.LogModel;
import com.ads.agile.utils.AgileStateMonitor;

import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements AgileStateMonitor.NetworkCallBack {

    private String TAG = this.getClass().getSimpleName();
    private AgileLog agileLog;
    private LogModel logModel;
    private EditText event_type;
    private AgileTransaction agileTransaction;
    private static final String AG_App_KEY = "1234567890";

    boolean aBoolean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "TimeZone id   = " + TimeZone.getDefault().getID());
        Log.d(TAG, "TimeZone name = " + TimeZone.getDefault().getDisplayName());
        Log.d(TAG, "TimeStamp All = " + TimeZone.getTimeZone(TimeZone.getDefault().getID()));
        Log.d(TAG, "TimeStamp     = " + System.currentTimeMillis());
        Log.d(TAG, "boolean       = " + aBoolean);

        new AgileStateMonitor(this).enable(getApplicationContext());

        agileTransaction = new AgileTransaction(getApplicationContext(), this, "MainActivityEventType",AG_App_KEY);
        agileLog = new AgileLog(getApplicationContext(), this, agileTransaction);

        event_type = findViewById(R.id.event_type);
        agileLog.agileInstall(AG_App_KEY);

        buttonClickEvent();

    }

    private void buttonClickEvent() {

        findViewById(R.id.book1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agileLog.set("bouns_id", "01");
                agileLog.set("bouns_name", "sample");
                agileLog.set("bouns_type", "coins");
                agileLog.trackLog("ag_clicked",AG_App_KEY);
                agileTransaction.set("MainActivityTransaction", "yes it is");
            }
        });
    }

    private void listAllData(List<LogEntity> notes) {
        for (int i = 0; i < notes.size(); i++) {
            Log.d(TAG, "eventType = " + notes.get(i).getEvent_type() + "\t Time = " + notes.get(i).getTime());
        }
    }

    @Override
    public void onConnected() {
        Log.d(TAG, "MainActivity connected to network via AgileLog");

        try {
            agileLog.syncLog();
        } catch (Exception e) {
            Log.d(TAG, "(onConnected) catch error = " + e.getMessage());
        }
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "MainActivity disconnected to network via AgileLog");
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        agileLog.sessionComplete(AG_App_KEY);
    }
}