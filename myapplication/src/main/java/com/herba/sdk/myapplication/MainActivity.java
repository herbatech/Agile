package com.herba.sdk.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.ads.agile.AgileCrashAnalytic.AgileCrashReporter;
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
    EditText editText;
    Context context;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AgileCrashReporter.initialize(this);


        agileTransaction = new AgileTransaction(this.getApplicationContext(), this, AgileEventType.AGILE_EVENT_TRANSACTION);
        agileLog = new AgileLog(this.getApplicationContext(), this, agileTransaction);


        if (getIntent().getExtras() != null) {
            try {

                String external_url = getIntent().getExtras().getString(AgileEventParameter.AGILE__NOTIFICATION_URL);
                String external_url_flag = getIntent().getExtras().getString(AgileEventParameter.AGILE__NOTIFICATION_FLAG);
                String click_action = getIntent().getExtras().getString(AgileEventParameter.AGILE__NOTIFICATION_ACTION);


                if (external_url_flag!=null &&  click_action.equalsIgnoreCase("agile_click_action")){
                    JSONObject jsonObj = new JSONObject(getIntent().getExtras().getString(AgileEventParameter.AGILE__NOTIFICATION_CONTENT));
                    agileLog.set(AgileEventParameter.AGILE_PARAMS_NOTIFICATION_CONTENT,jsonObj);
                    agileLog.trackEvent(AgileEventType.AGILE_NOTIFICATION_LOG);
                    if (external_url_flag!=null && external_url_flag.equalsIgnoreCase("1")){
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(external_url));
                        startActivity(browserIntent);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }



        findViewById(R.id.indexOutOfBound).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double num1 = Double.parseDouble(editText.getText().toString());
               /* Object x[] = new String[3];
                x[0] = new Integer(0);*/
            }
        });

        findViewById(R.id.tagEvent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject page_details=new JSONObject();
                    page_details.put("Fullname", "username");
                    page_details.put("Gender", "Male");
                    page_details.put("Address", "Mumbai");
                    agileLog.tagEvent(page_details);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.book1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText bonusId=findViewById(R.id.event_type);
                EditText bonusname=findViewById(R.id.event_type1);

                agileLog.set("bouns_id",bonusId.getText().toString());
                agileLog.set("bouns_name",bonusname.getText().toString());
                agileLog.trackEvent(AgileEventType.AGILE_EVENT_USER_PROPERTIES);

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
                Intent i=new Intent(MainActivity.this,FirstActivity.class);
                startActivity(i);
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