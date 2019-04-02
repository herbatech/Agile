package com.herba.sdk.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.ads.agile.AgileEventParameter;
import com.ads.agile.AgileEventType;
import com.ads.agile.AgileLog;
import com.ads.agile.AgileTransaction;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private String TAG = this.getClass().getSimpleName();
    private AgileLog agileLog;
    private AgileTransaction agileTransaction;
    boolean screen_on=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        agileTransaction = new AgileTransaction(getApplicationContext(), this, AgileEventType.AGILE_EVENT_TRANSACTION);
        agileLog = new AgileLog(getApplicationContext(), this, agileTransaction);

        findViewById(R.id.book1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText bonusId=findViewById(R.id.event_type);
                EditText bonusname=findViewById(R.id.event_type1);

                agileLog.set("bouns_id",bonusId.getText().toString());
                agileLog.set("bouns_name",bonusname.getText().toString());
                agileLog.trackEvent(AgileEventType.AGILE_EVENT_CLICK);

            }
        });

        findViewById(R.id.Transaction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,FirstActivity.class);
                startActivity(i);
            }
        });

        findViewById(R.id.PageLoad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {
                    JSONObject page_details=new JSONObject();
                    page_details.put("ProductId", "10");
                    page_details.put("cost", "500");

                    agileLog.set(AgileEventParameter.AGILE_PARAMS_PAGE_NAME,"ProductPage");
                    agileLog.set(AgileEventParameter.AGILE_PARAMS_PAGE_DETAILS,page_details);
                    agileLog.trackEvent(AgileEventType.AGILE_EVENT_LOG_PAGE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (screen_on){
            agileLog.agileAppScreenOn();
        }
        else {
            agileLog.agileAppStart();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        agileLog.sessionComplete();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        screen_on=true;
        agileLog.agileAppScreenOff();
        super.onSaveInstanceState(outState);
    }


}