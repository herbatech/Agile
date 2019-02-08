package com.ads.agile;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;

import com.ads.agile.room.LogEntity;
import com.ads.agile.room.LogModel;
import com.ads.agile.system.AdvertisingIdClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ads.agile.AgileConfiguration.AGILE_ID;
import static com.ads.agile.AgileConfiguration.AGILE_PREF;
import static com.ads.agile.AgileConfiguration.MONETIZE_FILENAME;


public class AgileLog extends Activity {

    private final String TAG = this.getClass().getSimpleName();

    private Context context;
    private FragmentActivity activity;
    private LogModel logModel;
    private int size;
    private SynchroniseLogEvent synchroniseLogEvent;
    private static JSONObject jsonObject = new JSONObject();

    /**
     * parametric constructor
     *
     * @param context  from the activity
     * @param activity from the activity
     */
    public AgileLog(@NonNull Context context, @NonNull FragmentActivity activity) {

        this.context = context;
        this.activity = activity;

        /*UtilConfig.scheduleJob(context);

        if(UtilConfig.isJobServiceOn(context)) {
            UtilConfig.scheduleJob(context);
        }
        else {
            Log.d(TAG,"Service with job id "+JOB_ID+" already running");
        }*/

        logModel = ViewModelProviders.of(activity).get(LogModel.class);
        logModel.getLiveListAllLog().observe(activity, new Observer<List<LogEntity>>() {
            @Override
            public void onChanged(List<LogEntity> notes) {
                Log.d(TAG, "size count = " + notes.size());
                size = notes.size();
            }
        });

        //get add id while initialization
        getAdvertisingId();

        //initialization of transaction
        //initTransaction();
    }

    /**
     * get Google advertising id on background thread
     *
     * @return the google adv id
     */
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

    /**
     * check for play service before access Google advertising id
     *
     * @param context from the parametric constructor
     * @return
     */
    public boolean checkForPlayService(@NonNull Context context) {
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
    public LiveData<List<LogEntity>> getAllLog() {
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

    /**
     * validate input param
     *
     * @param appId     is application id which is provided by us to the developer
     * @param eventType define the type of event
     */
    public void trackLog(@NonNull final String eventType, @NonNull final String appId) {

        String advertising_id = getAdvertisingId();
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String time = "0";

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
            sendLog(appId, android_id, eventType, getEvent(), time, advertising_id);
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

                    //clear the log
                    clear();


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
        clear();
    }

    /**
     * delete single data from the database
     *
     * @param unique_id is identifier
     */
    public void deleteLog(@NonNull int unique_id) {
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
     * initialize transactions from the constructor
     *
     * @see AgileConfiguration for file name
     */
    private void initTransaction() {

        try {
            if (isFileExist().exists()) {
                //file is exist
                Log.d(TAG, "(initTransaction) file is exist");
            } else {
                //file is not exist , now create
                Log.d(TAG, "(initTransaction) file is not exist , now create");
                createNewFile();
            }
        } catch (Exception e) {
            Log.d(TAG, "(initTransaction) catch error = " + e.getMessage());
        }
    }

    /**
     * return instance of
     *
     * @return File
     * @see AgileConfiguration for file name
     **/
    private File isFileExist() {
        File file = new File(context.getFilesDir() + File.separator, MONETIZE_FILENAME);
        Log.d(TAG, "(isFileExist) file path = " + file.toString());
        return file;
    }

    /**
     * create fresh new file
     *
     * @see AgileConfiguration for file name
     */
    private void createNewFile() {
        try {
            FileWriter writer = new FileWriter(isFileExist());
            writer.append("#BEGINS\n");
            writer.flush();
            writer.close();
        } catch (Exception e) {
            Log.d(TAG, "(createNewFile) catch error = " + e.getMessage());
        }
    }

    /**
     * add log to log file
     *
     * @param checkout   is responsible for the finish the log i.e. if true it will add #END at the end of log or else do nothing
     * @param eventType  define the type of event
     * @param eventValue define the event value , whose input took from where this function will call.
     * @see AgileConfiguration for file name
     **/
    public void addTransaction(@NonNull String eventType, @NonNull String eventValue, @NonNull boolean checkout) {

        if (isFileExist().exists()) {

            argumentValidation(eventType);

            writeInFile(eventType, eventValue, checkout);

        } else {
            Log.d(TAG, "(addTransaction) file is not exist , now create");
            createNewFile();
        }
    }

    /**
     * method will notify if there is any duplicate key inside array
     */
    private void checkForExistanceEntry() {

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
        } catch (Exception e) {
            Log.d(TAG, "(checkForExistanceEntry) catch error = " + e.getMessage());
        }
    }

    /**
     * @param checkout   is responsible for the end the log i.e. if true it will add #END at the end of log or else do nothing
     * @param eventType  define the type of event
     * @param eventValue define the event value , whose input took from where this function will call.
     * @see AgileConfiguration for file name
     **/
    private void writeInFile(String eventType, String eventValue, boolean checkout) {
        try {
            FileWriter writer = new FileWriter(isFileExist(), true);
            writer.append(eventType + ":" + eventValue);
            writer.append("\n");

            if (checkout) {
                writer.append("#END");
                writer.append("\n");
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            Log.d(TAG, "(writeInFile) catch error = " + e.getMessage());
        }
    }

    /**
     * clear the log
     *
     * @see AgileConfiguration for file name
     */
    public void removeTransaction() {


/*        if (isFileExist().exists()) {
            removeLog();
        } else {
            Log.d(TAG, "(removeTransaction) file is not exist , now create");
            createNewFile();
        }*/
    }

    /**
     * flush all information from the log file
     *
     * @see AgileConfiguration for file name
     */
    private void removeLog() {
        try {
            FileWriter writer = new FileWriter(isFileExist(), false);
            writer.append("#BEGINS\n");
            writer.flush();
            writer.close();
        } catch (Exception e) {
            Log.d(TAG, "(removeLog) catch error = " + e.getMessage());
        }
    }

    /**
     * commit the changes
     *
     * @see AgileConfiguration for file name
     */
    public void commitTransaction() {

        if (isFileExist().exists()) {
            commitLog();
        } else {
            Log.d(TAG, "(commitTransaction) file is not exist , now create");
            createNewFile();
        }
    }

    /**
     * this method will append the <#END> at the end of log before commit
     *
     * @see AgileConfiguration for file name
     */
    private void commitLog() {
        try {
            FileWriter writer = new FileWriter(isFileExist(), true);
            writer.append("#END\n");
            writer.flush();
            writer.close();
        } catch (Exception e) {
            Log.d(TAG, "(commitLog) catch error = " + e.getMessage());
        }
    }

    /**
     * delete the log file
     *
     * @see AgileConfiguration for file name
     */
    public void terminateTransaction() {
        if (isFileExist().exists()) {
            isFileExist().delete();
        } else {
            Log.d(TAG, "(terminateTransaction) file is not exist , now create");
            createNewFile();
        }
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
    public String getEvent() {
        return jsonObject.toString();
    }

    /**
     * clear the object value
     */
    public void clear() {
        jsonObject = new JSONObject();
    }
}