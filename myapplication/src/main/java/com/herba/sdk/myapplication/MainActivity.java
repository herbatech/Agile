package com.herba.sdk.myapplication;

import android.os.Bundle;
import android.print.PrinterId;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ads.agile.Agile;
import com.ads.agile.room.LogEntity;
import com.ads.agile.room.LogModel;
import com.ads.agile.utils.ConnectionStateMonitor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.TimeZone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class MainActivity extends AppCompatActivity implements ConnectionStateMonitor.NetworkCallBack {

    private String TAG = this.getClass().getSimpleName();
    private Agile agile;
    private LogModel logModel;
    private EditText event_id, event_type, delete_event_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "TimeZone id = " + TimeZone.getDefault().getID());
        Log.d(TAG, "TimeZone name = " + TimeZone.getDefault().getDisplayName());
        Log.d(TAG, "TimeStamp All = " + TimeZone.getTimeZone(TimeZone.getDefault().getID()));
        Log.d(TAG, "TimeStamp = " + System.currentTimeMillis());

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
        buttonClickEvent();
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

}