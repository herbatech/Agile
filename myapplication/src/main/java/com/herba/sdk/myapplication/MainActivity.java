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
    private AgileTransaction agileTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new AgileStateMonitor(this).enable(getApplicationContext());
        agileTransaction = new AgileTransaction(getApplicationContext(), this, "ag_transaction");
        agileLog = new AgileLog(getApplicationContext(), this, agileTransaction);
        findViewById(R.id.book1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agileLog.set("bouns_id", "01");
                agileLog.set("bouns_name", "sample");
                agileLog.set("bouns_type", "coins");
                agileLog.trackLog("ag_clicked");
                agileTransaction.set("buyer_name", "yes it is");
                //   agileTransaction.set("buyer_address", "buyer_address");
                //  agileTransaction.commitTransaction();
            }
        });

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
        agileLog.sessionComplete();
    }
}