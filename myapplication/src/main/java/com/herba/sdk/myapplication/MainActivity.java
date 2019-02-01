package com.herba.sdk.myapplication;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ads.agile.Agile;
import com.ads.agile.AgileConfiguration;
import com.ads.agile.annotations.MyAnnotation;
import com.ads.agile.room.LogEntity;
import com.ads.agile.room.LogModel;
import com.ads.agile.utils.AgileEvent;
import com.ads.agile.utils.ConnectionStateMonitor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class MainActivity extends AppCompatActivity implements ConnectionStateMonitor.NetworkCallBack {

    private String TAG = this.getClass().getSimpleName();
    private Agile agile;
    private LogModel logModel;
    private EditText event_id, event_type, delete_event_id;
    private TextView tvLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "TimeZone id   = " + TimeZone.getDefault().getID());
        Log.d(TAG, "TimeZone name = " + TimeZone.getDefault().getDisplayName());
        Log.d(TAG, "TimeStamp All = " + TimeZone.getTimeZone(TimeZone.getDefault().getID()));
        Log.d(TAG, "TimeStamp     = " + System.currentTimeMillis());

        new ConnectionStateMonitor(this).enable(getApplicationContext());

        agile = new Agile(getApplicationContext(), this);
        event_id = findViewById(R.id.event_id);
        event_type = findViewById(R.id.event_type);


        delete_event_id = findViewById(R.id.delete_event_id);

        findViewById(R.id.Deletebutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agile.DeleteLog(Integer.parseInt(delete_event_id.getText().toString().trim()));
                delete_event_id.setText("");
            }
        });

        //list count
        logModel = ViewModelProviders.of(this).get(LogModel.class);
        logModel.getLiveListAllLog().observe(this, new Observer<List<LogEntity>>() {
            @Override
            public void onChanged(List<LogEntity> notes) {
                Log.d(TAG, "size count = " + notes.size());
                //adapter.setNotes(notes);
                listAllData(notes);
            }
        });

        tvLog = findViewById(R.id.tvLog);
        //readLogCat();
        buttonClickEvent();

    }

    private void readLogCat() {

        Process logcat;
        final StringBuilder log = new StringBuilder();
        try {
            logcat = Runtime.getRuntime().exec(new String[]{"logcat", "-d"});
            BufferedReader br = new BufferedReader(new InputStreamReader(logcat.getInputStream()), 4 * 1024);
            String line;
            String separator = System.getProperty("line.separator");
            while ((line = br.readLine()) != null) {
                log.append(line);
                log.append(separator);
                tvLog.setText(log);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void buttonClickEvent() {

        findViewById(R.id.book1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //agile.DeleteLog(delete_event_id.getText().toString().trim());

                Log.d(TAG, "android id = " + Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID));

                JSONObject obj = new JSONObject();
                JSONArray arr = new JSONArray();
                try {
                    obj.put("ORDER_ID", "101");
                    obj.put("QUANTITY", "1");
                    obj.put("PAYMENT_ID", "P1");
                    obj.put("PAYMENT_AMOUNT", "500");
                    obj.put("PAYMENT_METHOD_TYPE", "CREDIT");
                    arr.put(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "JSONException, message = " + e.getMessage());
                }
                agile.eventLog(
                        event_type.getText().toString().trim(),
                        "1234567890",
                        event_id.getText().toString().trim(),
                        arr.toString());
            }
        });


        findViewById(R.id.goToNextPage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Map<String, Object> eventValue = new HashMap<>();
//                eventValue.put("bouns_id", "01");
//                eventValue.put("bouns_name", "sample");
//                eventValue.put("bouns_type", "coins");
                AgileEvent event = new AgileEvent("Event01");
                event.set("bouns_id", "01");
                event.set("bouns_name", "sample");
                event.set("bouns_type", "coins");
                event.addEvent("GoToSecondActivity", event, false);
                startActivity(new Intent(getApplicationContext(), SecondActivity.class));
            }
        });

        findViewById(R.id.getLog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLog();
            }
        });

        findViewById(R.id.getTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Map<String, Object> eventValue = new HashMap<>();
//                eventValue.put("bouns_id", "01");
//                eventValue.put("bouns_name", "sample");
//                eventValue.put("bouns_type", "coins");
//                Log.d(TAG, "example = " + eventValue);

                SampleMethod("A B");
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
        Log.d(TAG, "MainActivity connected to network via Agile");

        try {
            agile.SyncLog();
        } catch (Exception e) {
            Log.d(TAG, "(onConnected) catch error = " + e.getMessage());
        }
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "MainActivity disconnected to network via Agile");
    }

    @MyAnnotation(value1 = 1, value2 = "", value3 = "")
    private void getLog() {

        File file = new File(getFilesDir(), AgileConfiguration.MONETIZE_FILENAME);
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
            tvLog.setText(text);
        } catch (Exception e) {
            Log.d(TAG, "(getLog) catch error = " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //agile.terminateTransaction();
        agile.removeTransaction();
    }

    private void SampleMethod(String eventId) {

        for (int i = 0; i < eventId.length(); i++) {
            char c = eventId.charAt(i);

            //Log.d(TAG,i+" = "+c);
            //Log.d(TAG,i+" = "+Character.isLetterOrDigit(c));

            if (Character.isLetterOrDigit(c) || c == '_' || c == '-') {
                Log.d(TAG, c + " is valid");
            } else {
                Log.d(TAG, c + " is invalid, break the loop");
                break;
            }
        }
    }
}