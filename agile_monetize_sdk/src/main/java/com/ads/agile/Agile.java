package com.ads.agile;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.ads.agile.myapplication.R;
import com.ads.agile.room.LogEntity;
import com.ads.agile.room.LogModel;
import com.ads.agile.system.AdvertisingIdClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Agile extends Activity {

    private Context context;
    private FragmentActivity activity;
    private final String TAG = this.getClass().getSimpleName();
    private LogModel logModel;
    public static final String AGILE_PREF = "agile_preference";
    public static final String AGILE_ID = "agile_google_adv_id";
    private int size;
    private SynchroniseLogEvent synchroniseLogEvent;

    //parametric constructor
    public Agile(Context context, FragmentActivity activity) {

        this.context = context;
        this.activity = activity;

//        UtilConfig.scheduleJob(context);
//
//        if(UtilConfig.isJobServiceOn(context)) {
//            UtilConfig.scheduleJob(context);
//        }
//        else {
//            Log.d(TAG,"Service with job id "+JOB_ID+" already running");
//        }

        logModel = ViewModelProviders.of(activity).get(LogModel.class);
        logModel.getLiveListAllLog().observe(activity, new Observer<List<LogEntity>>() {
            @Override
            public void onChanged(List<LogEntity> notes) {
                Log.d(TAG, "size count = " + notes.size());
                size = notes.size();
            }
        });

        getAdvertisingId();
    }

    //get Google advertising id on background thread
    public String getAdvertisingId() {

        final String[] result = new String[1];

        if (!TextUtils.isEmpty(getPreferences(context, AGILE_ID)))//if it is not empty
        {
            Log.d(TAG, "google adv id found, now accesing to it");
            result[0] = getPreferences(context, AGILE_ID);
        } else//if it is empty
        {
            Log.d(TAG, "google adv id not found, now accesing to it");
            if (checkForPlayService(context)) {

                new Thread(new Runnable() {
                    public void run() {
                        try {
                            AdvertisingIdClient.AdInfo adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                            String advertisingId = adInfo.getId();
                            result[0] = advertisingId;
                            Log.d(TAG, "(getAdvertisingId) Google advertisingId = " + advertisingId);
                            boolean optOutEnabled = adInfo.isLimitAdTrackingEnabled();
                            //save google ad id into shared preference
                            setPreferences(context, AGILE_ID, advertisingId);
                            Log.d(TAG, "(getAdvertisingId) Google optOutEnabled = " + optOutEnabled);
                        } catch (Exception e) {
                            Log.d(TAG, "(getAdvertisingId) catch error" + e.getMessage());
                        }
                    }
                }).start();
            } else {
                Log.d(TAG, "play service have some issue");
            }
        }
        return result[0];
    }

    //check for play service before access Google advertising id
    public boolean checkForPlayService(Context context) {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);

        switch (resultCode) {

            case ConnectionResult.SUCCESS:
                Log.d(TAG, "Google Play Services is ready to go!");
                return true;

            case ConnectionResult.SERVICE_DISABLED:
                Log.d(TAG, "Google Play services is disable update, Please enable it " + getResources().getText(R.string.Google_Play_is_disable));
                return false;

            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                Log.d(TAG, "Google Play services is require update, Please update it , message = " + getResources().getText(R.string.Google_Play_is_not_update));
                return false;

            case ConnectionResult.SERVICE_MISSING:
                Log.d(TAG, "Google Play services is missing in your device, message = " + getResources().getText(R.string.Google_Play_is_missing));
                return false;

            case ConnectionResult.SERVICE_MISSING_PERMISSION:
                Log.d(TAG, "Google Play services not having enough permission, message = " + getResources().getText(R.string.Google_Play_is_permission_missing));
                return false;

            default:
                Log.d(TAG, "Google Play Services have some issue , error = " + resultCode);
        }

        return false;
    }

    //get the total count of local database row
    public int getCount() {
        return this.size;
    }

    //get all record from the local database
    public LiveData<List<LogEntity>> getAllLog() {
        return logModel.getLiveListAllLog();
    }

    //return the boolean value i.e true = connected to internet otherwise return false
    private boolean isConnected(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected());
    }

    //help to get network information
    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    //validate input param
    /**
     *@param appId
     *@param eventType
     *@param eventType
     *@param eventId
     *@param values
    */
    public void eventLog(final String eventType, final String appId, final String eventId, final String values) {

        String advertising_id = getAdvertisingId();
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String time = "0";

        Log.d(TAG, "appId           = " + appId);
        Log.d(TAG, "eventId         = " + eventId);
        Log.d(TAG, "android_id      = " + android_id);
        Log.d(TAG, "eventType       = " + eventType);
        Log.d(TAG, "values          = " + values);
        Log.d(TAG, "time            = " + time);
        Log.d(TAG, "advertising_id  = " + advertising_id);

        //validate input params
        if (       !TextUtils.isEmpty(appId)
                && !TextUtils.isEmpty(eventId)
                && !TextUtils.isEmpty(android_id)
                && !TextUtils.isEmpty(eventType)
                && !TextUtils.isEmpty(values)
                && !TextUtils.isEmpty(advertising_id)
                ) {
            sendLog(appId, eventId, android_id, eventType, values, time, advertising_id);
        } else {
            Log.d(TAG, "params is empty");
        }
    }

    //check for internet connection then perform required operation
    private void sendLog(final String appId, final String eventId, String android_id, final String eventType, final String values, final String time, String advertising_id) {
        if (isConnected(context)) {
            sendLogToServer
                    (
                            appId,
                            eventId,
                            android_id,
                            eventType,
                            values,
                            advertising_id
                    );
        } else {
            //save data into sqlite database
            Log.d(TAG, "network not connected");
            sendLogToDatabase
                    (
                            appId,
                            eventId,
                            eventType,
                            values
                    );
        }
    }

    //upload data to server
    private void sendLogToServer(final String appId, final String eventId, String android_id, final String eventType, final String values, String advertising_id) {

        AgileConfiguration.ServiceInterface service = AgileConfiguration.getRetrofit().create(AgileConfiguration.ServiceInterface.class);
        Call<ResponseBody> responseBodyCall = service.createUser
                (       appId,
                        android_id,
                        eventType,
                        values,
                        "0",
                        advertising_id
                );
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "response code = " + response.code());
                try {

                    String responseString = response.body().string();

                    Log.d(TAG, "response body = " + responseString);

                    JSONObject object = new JSONObject(responseString);
                    boolean status = object.getBoolean("status");
                    Log.d(TAG, "status = " + status);

                } catch (IOException e) {
                    Log.d(TAG, "IOException = " + e.getMessage());
                } catch (JSONException e) {
                    Log.d(TAG, "JSONException = " + e.getMessage());
                } finally {
                    response.body().close();
                    Log.d(TAG, "retrofit connection closed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure = " + t.getMessage());
                sendLogToDatabase(eventType, appId, eventId, values);
            }
        });
    }

    //upload data to local database
    private void sendLogToDatabase(String appId, String eventId, String eventType, String values) {
        Log.d(TAG, "insert log into database");
        LogEntity logEntity = new LogEntity();
        logEntity.setApp_id(appId);
        logEntity.setEvent_id(eventId);
        logEntity.setEvent_type(eventType);
        logEntity.setValue(values);
        logEntity.setAndroid_id(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        logModel.insertLog(logEntity);
    }

    //delete single data from the database
    public void DeleteLog(int unique_id) {
        logModel.singleDeleteLog(unique_id);
    }

    //launch the background thread operation to sync
    public void SyncLog() {

        Log.d(TAG, "(SyncLog) called ,size = " + size);
        synchroniseLogEvent = new SynchroniseLogEvent(size);
        synchroniseLogEvent.execute();
    }

    //to sync the local database information with server database on background thread
    private class SynchroniseLogEvent extends AsyncTask<Void, Void, Void> {

        int size;

        public SynchroniseLogEvent(int size) {
            this.size = size;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            for (int i = 0; i < size; i++) {

                int id = logModel.getLiveListAllLog().getValue().get(i).getId();
                String eventType = logModel.getLiveListAllLog().getValue().get(i).getEvent_type();
                String appId = logModel.getLiveListAllLog().getValue().get(i).getApp_id();
                String eventId = logModel.getLiveListAllLog().getValue().get(i).getEvent_id();
                String value = logModel.getLiveListAllLog().getValue().get(i).getValue();
                long time = Long.parseLong(logModel.getLiveListAllLog().getValue().get(i).getTime());

                Log.d(TAG, "id               = " + id);
                Log.d(TAG, "event type       = " + eventType);
                Log.d(TAG, "app id           = " + appId);
                Log.d(TAG, "event id         = " + eventId);
                Log.d(TAG, "event value      = " + value);
                Log.d(TAG, "event time       = " + time);
                Log.d(TAG, "*************************************************************************************");

                //call webservice to add data to database
                eventProductLogServiceOffline(id, appId, eventId, eventType, value, time);
            }
            return null;
        }
    }

    //send data from local database to live database when connected to internet
    public void eventProductLogServiceOffline(final int id, String appId, String eventId, String eventType, String values, long time) {

        String advertising_id = getAdvertisingId();
        time = (time - System.currentTimeMillis()) / 1000;

        Log.d(TAG, "id              = " + id);
        Log.d(TAG, "eventType       = " + eventType);
        Log.d(TAG, "appId           = " + appId);
        Log.d(TAG, "eventId         = " + eventId);
        Log.d(TAG, "eventalues      = " + values);
        Log.d(TAG, "android_id      = " + Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        Log.d(TAG, "time            = " + time);
        Log.d(TAG, "advertising_id  = " + advertising_id);

        AgileConfiguration.ServiceInterface service = AgileConfiguration.getRetrofit().create(AgileConfiguration.ServiceInterface.class);
        Call<ResponseBody> responseBodyCall = service.createUser
                (       appId,
                        Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID),
                        eventType,
                        values,
                        String.valueOf(time),
                        advertising_id
                );

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                Log.d(TAG, "response code, id = " + id + " = " + response.code());

                try {

                    String responseString = response.body().string();
                    Log.d(TAG, "response body " + id + " = " + responseString);

                    JSONObject object = new JSONObject(responseString);
                    boolean status = object.getBoolean("status");
                    Log.d(TAG, "status = " + status);

                    if (status) {
                        //delete record from the database if the response is true
                        logModel.singleDeleteLog(id);
                    } else {
                        //do not delete record from the database if the response is false
                    }

                } catch (IOException e) {
                    Log.d(TAG, "IOException = " + e.getMessage());
                    synchroniseLogEvent.cancel(true);
                } catch (JSONException e) {
                    Log.d(TAG, "JSONException = " + e.getMessage());
                    synchroniseLogEvent.cancel(true);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure = " + t.getMessage());
                synchroniseLogEvent.cancel(true);
            }
        });
    }

    // set value to shared preference i.e. Google advertising id
    private void setPreferences(Context context, String key, String value) {
        SharedPreferences DevicePref = context.getSharedPreferences(AGILE_PREF, 0);
        SharedPreferences.Editor DeviceEditor = DevicePref.edit();
        DeviceEditor.putString(key, value);
        DeviceEditor.commit();
    }

    // get value from shared preference i.e. Google advertising id
    private String getPreferences(Context context, String key) {
        String data = null;
        try {
            SharedPreferences DevicePref = context.getSharedPreferences(AGILE_PREF, 0);
            data = DevicePref.getString(key, "");
        } catch (Exception e) {
            Log.d(TAG, "(getPreferences) catch exception = " + e.getMessage());
        }
        return data;
    }

}