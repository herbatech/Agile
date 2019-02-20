package com.herba.sdk.myapplication;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.ads.agile.AgileLog;
import com.ads.agile.AgileTransaction;


public class SecondActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private AgileLog agileLog;
    private AgileTransaction agileTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        agileTransaction = new AgileTransaction(getApplicationContext(), this, "SecondActivityEventType");
        agileLog = new AgileLog(getApplicationContext(), this, agileTransaction);

        findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //agileLog.addTransaction("Go-To-HomeActivity","blank",true);
                agileLog.unset("bouns_name");
                Log.d(TAG, "final list = " + agileLog.getLogEvent());
                //finish();
            }
        });

        findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agileLog.set("bouns_name", "sample");
                Log.d(TAG, "final list = " + agileLog.getLogEvent());
            }
        });


        findViewById(R.id.btnAdd2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agileLog.set("bouns_name", "sample_1");
                Log.d(TAG, "final list = " + agileLog.getLogEvent());
            }
        });

        findViewById(R.id.btnDisplay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "display final list = " + agileLog.getLogEvent());
            }
        });


        findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agileLog.clearLogEvent();
            }
        });
    }
}