package com.herba.sdk.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ads.agile.Agile;
import com.ads.agile.utils.AgileEvent;

public class SecondActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    private Agile agile;
    private AgileEvent agileEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        agile = new Agile(getApplicationContext(), this);
        agileEvent = new AgileEvent("Event01");

        //agile.addTransaction("HomeActivity_launched", "blank", false);

        findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //agile.addTransaction("Go-To-HomeActivity","blank",true);
                agileEvent.unset("bouns_name");
                Log.d(TAG, "final list = " + agileEvent.getEvent());
                //finish();
            }
        });

        findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agileEvent.set("bouns_name","sample");
                Log.d(TAG, "final list = " + agileEvent.getEvent());
            }
        });


        findViewById(R.id.btnAdd2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agileEvent.set("bouns_name","sample_1");
                Log.d(TAG, "final list = " + agileEvent.getEvent());
            }
        });

        findViewById(R.id.btnDisplay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "display final list = " + agileEvent.getEvent());
            }
        });


        findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agileEvent.clear();
            }
        });
    }
}