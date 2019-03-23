package com.herba.sdk.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.ads.agile.AgileLog;
import com.ads.agile.AgileTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = this.getClass().getSimpleName();
    private AgileLog agileLog;
    private AgileTransaction agileTransaction;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        agileTransaction = new AgileTransaction(getApplicationContext(), this, "ag_transaction");
        agileLog = new AgileLog(getApplicationContext(), this, agileTransaction);
        findViewById(R.id.book1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText bonusId=findViewById(R.id.event_type);
                EditText bonusname=findViewById(R.id.event_type1);
                agileLog.set("bouns_id",bonusId.getText().toString());
                agileLog.set("bouns_name",bonusname.getText().toString());
                agileLog.trackEvent("ag_clicked");
            }
        });

        findViewById(R.id.Transaction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,FirstActivity.class);
                startActivity(i);
            }
        });

    }

}