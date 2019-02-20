package com.ads.agile;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;

import com.ads.agile.myapplication.BuildConfig;
import com.ads.agile.room.LogEntity;
import com.ads.agile.room.LogModel;
import com.ads.agile.system.AdvertisingIdClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ads.agile.AgileConfiguration.AGILE_ID;
import static com.ads.agile.AgileConfiguration.AGILE_PREF;
import static com.ads.agile.AgileConfiguration.isLog;
import static com.ads.agile.AgileConfiguration.isTransaction;


public class AgileLog extends Activity {

    private final String TAG = this.getClass().getSimpleName();

    private Context context;
    private FragmentActivity activity;
    private LogModel logModel;
    private int size;
    private SynchroniseLogEvent synchroniseLogEvent;
    private static JSONObject jsonObject = new JSONObject();

    private Date date1;
    long seconds ;
    SharedPreferences prefs;
    String dateTimeKey = "time_duration";
    private Boolean firstTime = false;
    AgileTransaction agileTransaction;

    String AppId;



    /**
     * parametric constructor
     *
     * @param context          from the activity
     * @param activity         from the activity
     * @param agileTransaction
     */
    public AgileLog(@NonNull Context context, @NonNull FragmentActivity activity, AgileTransaction agileTransaction) {

        this.context = context;
        this.activity = activity;
        Bundle metadata = getMetaData(context);
       AppId= metadata.getString("com.agile.sdk.ApplicationId");


        /*UtilConfig.scheduleJob(context);

        if(UtilConfig.isJobServiceOn(context)) {
            UtilConfig.scheduleJob(context);
        }
        else {
            Log.d(TAG,"Service with job id "+JOB_ID+" already running");
        }*/

       /* if (activity.onBackPressed().isFinishing()) {
            // Here  you can be sure the Activity will be destroyed eventually

            Log.d(TAG, "finishing Activity   =");
        }*/


/*
      if(activity != null) {
            try{
                Log.d(TAG,"Service with job id already running");
                //activity.finish();
            }catch(Throwable e){
                e.printStackTrace();
                Log.d(TAG,"Service  =="+e.getMessage());
            }
        }*/



        prefs = context.getSharedPreferences("com.ads.agile", Context.MODE_PRIVATE);
        Date dato = new Date();
        prefs.edit().putLong(dateTimeKey, dato.getTime()).commit();
        long l = prefs.getLong(dateTimeKey, new Date().getTime());
       // Log.d(TAG, "currentTimeValue     =="+l);
        date1 = new Date(l);



/*

        Timer updateTimer = new Timer();
        updateTimer.schedule(new TimerTask()
        {
            public void run()
            {
                try
                {




                    // txtCurrentTime.setText(diff);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

        }, 0, 1000);
*/


        PackageManager packageManager= context.getPackageManager();
        try {
            String appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo("com.ads.agile",PackageManager.GET_META_DATA));
            Log.d(TAG, "currentTimeValue3333     =="+appName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "currentTimeValue3333     =="+e.getMessage());
        }

        logModel = ViewModelProviders.of(activity).get(LogModel.class);
        logModel.getLiveListAllLog().observe(activity, new Observer<List<LogEntity>>() {
            @Override
            public void onChanged(List<LogEntity> notes) {
                Log.d(TAG, "size count = " + notes.size());
                size = notes.size();
            }
        });

        //get add id while initialization




        //initialization of transaction
        //initTransaction();

        //check for transaction instance
        if (agileTransaction instanceof AgileTransaction) {
            Log.d(TAG, "instance agileTransaction exist");
            isTransaction = true;
        } else {
            Log.d(TAG, "instance not agileTransaction exist");
            isTransaction = false;
        }

        getAdvertisingId();

    }

    public static Bundle getMetaData(Context context) {
        try {
            return context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * get Google advertising id on background thread
     *
     * @return the google adv id
     */
    private String getAdvertisingId() {

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
                            firstTime=true;
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

    /**
     * check for play service before access Google advertising id
     *
     * @param context from the parametric constructor
     * @return
     */
    private boolean checkForPlayService(@NonNull Context context) {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);

        switch (resultCode) {

            case ConnectionResult.SUCCESS:
                //Log.d(TAG, "Google Play Services is ready to go!");
                return true;

            case ConnectionResult.SERVICE_DISABLED:
                //Log.d(TAG, "Google Play services is disable update, Please enable it " + getResources().getText(R.string.Google_Play_is_disable));
                return false;

            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                //Log.d(TAG, "Google Play services is require update, Please update it , message = " + getResources().getText(R.string.Google_Play_is_not_update));
                return false;

            case ConnectionResult.SERVICE_MISSING:
                //Log.d(TAG, "Google Play services is missing in your device, message = " + getResources().getText(R.string.Google_Play_is_missing));
                return false;

            case ConnectionResult.SERVICE_MISSING_PERMISSION:
                //Log.d(TAG, "Google Play services not having enough permission, message = " + getResources().getText(R.string.Google_Play_is_permission_missing));
                return false;

            default:
                //Log.d(TAG, "Google Play Services have some issue , error = " + resultCode);
        }

        return false;
    }

    /**
     * get the total count of local database row
     *
     * @return the size of database record i.e. number of offline entry into room database
     * @see com.ads.agile.room.LogDatabase
     * @see com.ads.agile.room.LogModel
     * @see com.ads.agile.room.LogDao
     * @see com.ads.agile.room.LogEntity
     */
    public int getCount() {
        return this.size;
    }

    /**
     * get all record from the local database
     *
     * @return the list of all record from the room database
     * @see com.ads.agile.room.LogDatabase
     * @see com.ads.agile.room.LogModel
     * @see com.ads.agile.room.LogDao
     * @see com.ads.agile.room.LogEntity
     */
    private LiveData<List<LogEntity>> getOfflineLog() {
        return logModel.getLiveListAllLog();
    }

    /**
     * return the boolean value i.e true = connected to internet otherwise return false
     *
     * @param context init from parametric constructor
     * @return the network connectivity state
     */
    private boolean isConnected(@NonNull Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected());
    }

    /**
     * help to get network information
     *
     * @param context init from parametric constructor
     * @return the instance of NetworkInfo class
     * @see NetworkInfo for more info
     */
    private static NetworkInfo getNetworkInfo(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    public void sessionComplete(){
        Date date2 =  new Date();
        long mills = date2 .getTime() - date1.getTime();
        int hours = (int) (mills/(1000 * 60 * 60));
        int mins = (int) (mills/(1000*60)) % 60;
        String diff = hours + ":" + mins; // updated value every1 second
        seconds = TimeUnit.MILLISECONDS.toSeconds(mills);

        set("duration",seconds);
        trackLog("ag_session");
    }

    public void agileInstall(){

        //  isFirstTime();
        Log.d(TAG, "log cant send to server  ="+ AppId);

        if (getAdvertisingId()!=null){
            boolean isFirstTime = MyPreferences.isFirst(context);
            if (isFirstTime){
                trackLog("ag_install");
                //  Log.d(TAG, "log cant send to server, due to the validation failed in set method of AgileTransaction class111111111");
            }
        }


      }
    public static class MyPreferences {

        private static final String MY_PREFERENCES = "my_preferences";

        public static boolean isFirst(Context context){
            final SharedPreferences reader = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
            final boolean first = reader.getBoolean("is_first", true);
            if(first){
                final SharedPreferences.Editor editor = reader.edit();
                editor.putBoolean("is_first", false);
                editor.commit();
            }
            return first;
        }

    }


    /**
     * validate input param
     *
     * @param eventType define the type of event
     */
    public void trackLog(@NonNull final String eventType) {

        /**
         * if the transaction is enable
         */



       // Log.d(TAG,"packegeId     >"+eventType);

        if (isTransaction) {
            if (isLog) {
                validateLog(eventType, AppId);
            } else {

            }
        }
        /**
         * if the transaction is disable
         */
        else {
            validateLog(eventType,AppId);
        }
    }



    /**
     * @param eventType
     * @param appId
     */
    private void validateLog(String eventType, String appId) {
        String advertising_id = getAdvertisingId();
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String time = "0";
       // agileInstall();
        Log.d(TAG, "appId           = " + appId);
        Log.d(TAG, "android_id      = " + android_id);
        Log.d(TAG, "eventType       = " + eventType);
        Log.d(TAG, "time            = " + time);
        Log.d(TAG, "advertising_id  = " + advertising_id);

        argumentValidation(eventType);  //validation in trackLog

        //validate input params
        if (!TextUtils.isEmpty(appId)
                && !TextUtils.isEmpty(android_id)
                && !TextUtils.isEmpty(eventType)
                && !TextUtils.isEmpty(advertising_id)
        ) {
            sendLog(appId, android_id, eventType, getLogEvent(), time, advertising_id);
        } else {
            Log.d(TAG, "params is empty");

        }
    }

    /**
     * check for internet connection then perform required operation
     *
     * @param appId          is application id which is provided by us to the developer
     * @param android_id     is unique identification of android device
     * @param eventType      define the type of event
     * @param values         could be additional information which describe the eventType in more details
     * @param time           would be always zero if it send directly to the server or else will send the difference of current and stored entry into room database
     * @param advertising_id is google adv id
     */
    private void sendLog(@NonNull final String appId, @NonNull String android_id, @NonNull final String eventType, @NonNull final String values, @NonNull final String time, @NonNull String advertising_id) {
        Log.d(TAG, "currentTimeValue11     =="+appId);
       // Log.d(TAG, "android_Id     =="+packgeId);
        argumentValidation(eventType);  //validation in sendLog



        if (isConnected(context)) {
            sendLogToServer
                    (
                            appId,
                            android_id,
                            eventType,
                            values,
                            time,
                            advertising_id
                    );
        } else {
            //save data into sqlite database
            Log.d(TAG, "network not connected");
            sendLogToDatabase
                    (
                            appId,
                            eventType,
                            values
                    );
        }
    }

    /**
     * upload data to server
     *
     * @param appId          is application id which is provided by us to the developer
     * @param android_id     is unique identification of android device
     * @param eventType      define the type of event
     * @param values         could be additional information which describe the eventType in more details
     * @param time           would be always zero if it send directly to the server or else will send the difference of current and stored entry into room database
     * @param advertising_id is google adv id
     */
    private void sendLogToServer(@NonNull final String appId, @NonNull String android_id, @NonNull final String eventType, @NonNull final String values, @NonNull final String time, @NonNull String advertising_id) {

        argumentValidation(eventType);  //validation in sendLogToServer

        AgileConfiguration.ServiceInterface service = AgileConfiguration.getRetrofit().create(AgileConfiguration.ServiceInterface.class);
        Call<ResponseBody> responseBodyCall = service.createUser
                (appId,
                        android_id,
                        eventType,
                        values,
                        time,
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

                    //clearLogEvent the log
                    clearLogEvent();


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
                sendLogToDatabase(eventType, appId, values);
            }
        });
    }

    /**
     * upload data to local database
     *
     * @param appId     is application id which is provided by us to the developer
     * @param eventType define the type of event
     * @param values    could be additional information which describe the eventType in more details
     */
    private void sendLogToDatabase(@NonNull String appId, @NonNull String eventType, @NonNull String values) {

        argumentValidation(eventType);  //validation in sendLogToDatabase

        Log.d(TAG, "insert log into database");
        LogEntity logEntity = new LogEntity();
        logEntity.setApp_id(appId);
        logEntity.setEvent_type(eventType);
        logEntity.setValue(values);
        logEntity.setAndroid_id(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        logModel.insertLog(logEntity);
        clearLogEvent();
    }

    /**
     * delete single data from the database
     *
     * @param unique_id is identifier
     */
    private void deleteLog(@NonNull int unique_id) {
        logModel.singleDeleteLog(unique_id);
    }

    /**
     * perform operation on background thread
     */
    public void syncLog() throws Exception {

        Log.d(TAG, "(syncLog) called ,size = " + size);
        synchroniseLogEvent = new SynchroniseLogEvent(size);
        synchroniseLogEvent.execute();
    }

    /**
     * to sync the local database information with server database on background thread
     */
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
                String value = logModel.getLiveListAllLog().getValue().get(i).getValue();
                long time = Long.parseLong(logModel.getLiveListAllLog().getValue().get(i).getTime());

                Log.d(TAG, "id               = " + id);
                Log.d(TAG, "event type       = " + eventType);
                Log.d(TAG, "app id           = " + appId);
                Log.d(TAG, "event value      = " + value);
                Log.d(TAG, "event time       = " + time);
                Log.d(TAG, "*************************************************************************************");

                //call webservice to add data to database
                eventProductLogServiceOffline(id, appId, eventType, value, time);
            }
            return null;
        }
    }

    /**
     * send data from local database to live database when connected to internet
     *
     * @param id        is primary key of every database entry
     * @param appId     is application id which is provided by us to the developer
     * @param eventType define the type of event
     * @param values    could be additional information which describe the eventType in more details
     * @param time      would be always zero if it send directly to the server or else will send the difference of current and stored entry into room database
     */
    private void eventProductLogServiceOffline(@NonNull final int id, @NonNull String appId, @NonNull String eventType, @NonNull String values, @NonNull long time) {

        String advertising_id = getAdvertisingId();
        time = (time - System.currentTimeMillis()) / 1000;

        Log.d(TAG, "id              = " + id);
        Log.d(TAG, "eventType       = " + eventType);
        Log.d(TAG, "appId           = " + appId);
        Log.d(TAG, "eventalues      = " + values);
        Log.d(TAG, "android_id      = " + Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        Log.d(TAG, "time            = " + time);
        Log.d(TAG, "advertising_id  = " + advertising_id);

        argumentValidation(eventType);  //validation in eventProductLogServiceOffline

        AgileConfiguration.ServiceInterface service = AgileConfiguration.getRetrofit().create(AgileConfiguration.ServiceInterface.class);
        Call<ResponseBody> responseBodyCall = service.createUser
                (appId,
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

    /**
     * set value to shared preference i.e. Google advertising id
     *
     * @param context init from parametric constructor
     * @param key     is SharedPreference key
     * @param value   is value associated with @param key
     **/
    private void setPreferences(@NonNull Context context, @NonNull String key, @NonNull String value) {
        SharedPreferences DevicePref = context.getSharedPreferences(AGILE_PREF, 0);
        SharedPreferences.Editor DeviceEditor = DevicePref.edit();
        DeviceEditor.putString(key, value);
        DeviceEditor.commit();
    }

    /**
     * get value from shared preference i.e. Google advertising id
     *
     * @param context init from parametric constructor
     * @param key     is SharedPreference key
     **/
    private String getPreferences(@NonNull Context context, @NonNull String key) {
        String data = null;
        try {
            SharedPreferences DevicePref = context.getSharedPreferences(AGILE_PREF, 0);
            data = DevicePref.getString(key, "");
        } catch (Exception e) {
            Log.d(TAG, "(getPreferences) catch exception = " + e.getMessage());
        }
        return data;
    }


    /**
     * this method will validate
     *
     * @param param to ensure it only contain
     *              a-z or A-Z or 0-9 or _ or -
     *              character
     */
    private void argumentValidation(@NonNull String param) {

        //check each index of string
        for (int i = 0; i < param.length(); i++) {
            char c = param.charAt(i);

            if (Character.isLetterOrDigit(c) || c == '_' || c == '-') {
                //print valid character
                //Log.d(TAG, c + " is valid");
            } else {
                //print invalid character and
                //break the loop and throw exception
                //Log.d(TAG, c + " is invalid, break the loop");
                throw new IllegalArgumentException(param + " only supports a-z or A-Z or 0-9 or _ or - ");
            }
        }
    }

    /**
     * @param key   String data type
     * @param value int data type
     */
    public void set(String key, int value) {
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            Log.d(TAG, "(set) String int catch error = " + e.getMessage());
        }
    }

    /**
     * @param key   String data type
     * @param value float data type
     */
    public void set(String key, float value) {
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            Log.d(TAG, "(set) String float catch error = " + e.getMessage());
        }
    }

    /**
     * @param key   String data type
     * @param value long data type
     */
    public void set(String key, long value) {
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            Log.d(TAG, "(set) String long catch error = " + e.getMessage());
        }
    }

    /**
     * @param key   String data type
     * @param value String data type
     */
    public void set(String key, String value) {
        try {
            jsonObject.put(key, value);
        } catch (Exception e) {
            Log.d(TAG, "(set) String String catch error = " + e.getMessage());
        }
    }

    /**
     * @param key   String data type
     * @param value boolean data type
     */
    public void set(String key, boolean value) {
        try {
            jsonObject.put(key, value);
        } catch (Exception e) {
            Log.d(TAG, "(set) String boolean catch error = " + e.getMessage());
        }
    }

    /**
     * @param key   String data type
     * @param value short data type
     */
    public void set(String key, short value) {
        try {
            jsonObject.put(key, value);
        } catch (Exception e) {
            Log.d(TAG, "(set) String short catch error = " + e.getMessage());
        }
    }

    /**
     * remove the value from the list
     *
     * @param value
     */
    public void unset(String value) {
        jsonObject.remove(value);
    }

    /**
     * @return
     */
    public String getLogEvent() {
        return jsonObject.toString();
    }

    /**
     * clearLogEvent the object value
     */
    public void clearLogEvent() {
        jsonObject = new JSONObject();
    }



    public void setEventType(String key, String value) {
        try {
            jsonObject.put(key, value);
        } catch (Exception e) {
            Log.d(TAG, "(set) String short catch error = " + e.getMessage());
        }
    }

    /**
     * remove the value from the list
     *
     * @param value
     */
    public void unsetEventType(String value) {
        jsonObject.remove(value);
    }

    /**
     * @return
     */
    public String getLogEventType() {
        return jsonObject.toString();
    }

    /**
     * clearLogEvent the object value
     */
    public void clearLogEventType() {
        jsonObject = new JSONObject();
    }
}