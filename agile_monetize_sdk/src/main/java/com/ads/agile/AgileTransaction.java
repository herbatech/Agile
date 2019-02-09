package com.ads.agile;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;

import com.ads.agile.room.LogEntity;
import com.ads.agile.room.LogModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ads.agile.AgileConfiguration.AGILE_CRASH_COUNTER;
import static com.ads.agile.AgileConfiguration.argumentValidation;
import static com.ads.agile.AgileConfiguration.getAdvertisingId;
import static com.ads.agile.AgileConfiguration.getPreferences;
import static com.ads.agile.AgileConfiguration.isConnected;
import static com.ads.agile.AgileConfiguration.isLog;
import static com.ads.agile.AgileConfiguration.isTransaction;
import static com.ads.agile.AgileConfiguration.setPreferences;

public class AgileTransaction {

    private final String TAG = this.getClass().getSimpleName();
    private Context context;
    private int size;
    private static JSONObject jsonObject = new JSONObject();
    private static boolean transactionInitFlag = false;
    private LogModel logModel;
    private static String appId;
    private static String eventType;
    private int counter;

    public AgileTransaction(@NonNull Context context, @NonNull FragmentActivity activity, @NonNull String eventType, @NonNull String appId) {
        this.context = context;
        this.eventType = eventType;
        this.appId = appId;

        transactionInitFlag = true;

        logModel = ViewModelProviders.of(activity).get(LogModel.class);
        logModel.getLiveListAllLog().observe(activity, new Observer<List<LogEntity>>() {
            @Override
            public void onChanged(List<LogEntity> notes) {
                Log.d(TAG, "size count = " + notes.size());
                size = notes.size();
            }
        });

        isLog = true;

        if (getPreferences(context, AGILE_CRASH_COUNTER).equalsIgnoreCase("1")) {
            counter = 1;
        } else if (getPreferences(context, AGILE_CRASH_COUNTER).equalsIgnoreCase("2")) {
            counter = 2;
        } else {
            setPreferences(context, AGILE_CRASH_COUNTER, "1");
            counter = 1;
        }
    }

    public AgileTransaction(@NonNull Context context, @NonNull FragmentActivity activity) {
        this.context = context;

        logModel = ViewModelProviders.of(activity).get(LogModel.class);
        logModel.getLiveListAllLog().observe(activity, new Observer<List<LogEntity>>() {
            @Override
            public void onChanged(List<LogEntity> notes) {
                Log.d(TAG, "size count = " + notes.size());
                size = notes.size();
            }
        });

        isLog = true;

        if (getPreferences(context, AGILE_CRASH_COUNTER).equalsIgnoreCase("2")) {
            counter = 2;
        } else {
            setPreferences(context, AGILE_CRASH_COUNTER, "1");
            counter = 1;
        }
    }

    /**
     * @param key   String data type
     * @param value int data type
     */
    public void set(String key, int value) {

        if (transactionInitFlag) {
            try {
                jsonObject.put(key, value);
            } catch (JSONException e) {
                Log.d(TAG, "(set) String int catch error = " + e.getMessage());
            }
        } else {
            if (String.valueOf(counter).equalsIgnoreCase("1")) {
                setPreferences(context, AGILE_CRASH_COUNTER, "2");
                throw new IllegalStateException(key + " => " + value + ", transaction can not be initiated, since the transaction is not started");
            } else {
                //setPreferences(context,AGILE_CRASH_COUNTER,"1");
                Log.d(TAG, "crash suppressed");
            }
        }
    }

    /**
     * @param key   String data type
     * @param value float data type
     */
    public void set(String key, float value) {

        if (transactionInitFlag) {
            try {
                jsonObject.put(key, value);
            } catch (JSONException e) {
                Log.d(TAG, "(set) String float catch error = " + e.getMessage());
            }
        } else {
            if (String.valueOf(counter).equalsIgnoreCase("1")) {
                setPreferences(context, AGILE_CRASH_COUNTER, "2");
                throw new IllegalStateException(key + " => " + value + ", transaction can not be initiated, since the transaction is not started");
            } else {
                //setPreferences(context,AGILE_CRASH_COUNTER,"1");
                Log.d(TAG, "crash suppressed");
            }
        }
    }

    /**
     * @param key   String data type
     * @param value long data type
     */
    public void set(String key, long value) {

        if (transactionInitFlag) {
            try {
                jsonObject.put(key, value);
            } catch (JSONException e) {
                Log.d(TAG, "(set) String long catch error = " + e.getMessage());
            }
        } else {
            if (String.valueOf(counter).equalsIgnoreCase("1")) {
                setPreferences(context, AGILE_CRASH_COUNTER, "2");
                throw new IllegalStateException(key + " => " + value + ", transaction can not be initiated, since the transaction is not started");
            } else {
                //setPreferences(context,AGILE_CRASH_COUNTER,"1");
                Log.d(TAG, "crash suppressed");
            }
        }
    }

    /**
     * @param key   String data type
     * @param value String data type
     */
    public void set(String key, String value) {

        if (transactionInitFlag) {
            try {
                jsonObject.put(key, value);
            } catch (Exception e) {
                Log.d(TAG, "(set) String String catch error = " + e.getMessage());
            }
        } else {
            if (String.valueOf(counter).equalsIgnoreCase("1")) {
                setPreferences(context, AGILE_CRASH_COUNTER, "2");
                throw new IllegalStateException(key + " => " + value + ", transaction can not be initiated, since the transaction is not started");
            } else {
                //setPreferences(context,AGILE_CRASH_COUNTER,"1");
                Log.d(TAG, "crash suppressed");
            }
        }
    }

    /**
     * @param key   String data type
     * @param value boolean data type
     */
    public void set(String key, boolean value) {

        if (transactionInitFlag) {
            try {
                jsonObject.put(key, value);
            } catch (Exception e) {
                Log.d(TAG, "(set) String boolean catch error = " + e.getMessage());
            }
        } else {
            if (String.valueOf(counter).equalsIgnoreCase("1")) {
                setPreferences(context, AGILE_CRASH_COUNTER, "2");
                throw new IllegalStateException(key + " => " + value + ", transaction can not be initiated, since the transaction is not started");
            } else {
                //setPreferences(context,AGILE_CRASH_COUNTER,"1");
                Log.d(TAG, "crash suppressed");
            }
        }
    }

    /**
     * @param key   String data type
     * @param value short data type
     */
    public void set(String key, short value) {

        if (transactionInitFlag) {
            try {
                jsonObject.put(key, value);
            } catch (Exception e) {
                Log.d(TAG, "(set) String short catch error = " + e.getMessage());
            }
        } else {
            if (String.valueOf(counter).equalsIgnoreCase("1")) {
                setPreferences(context, AGILE_CRASH_COUNTER, "2");
                throw new IllegalStateException(key + " => " + value + ", transaction can not be initiated, since the transaction is not started");
            } else {
                //setPreferences(context,AGILE_CRASH_COUNTER,"1");
                Log.d(TAG, "crash suppressed");
            }
        }
    }

    /**
     * remove the value from the list
     *
     * @param key
     */
    public void unset(String key) {
        jsonObject.remove(key);
    }

    /**
     * @return
     */
    public String getTransaction() {
        return jsonObject.toString();
    }

    /**
     * clearLogEvent the object value
     */
    public void rollbackTransaction() {
        jsonObject = new JSONObject();
        transactionInitFlag = false;
    }

    /**
     * add jsonObject to jsonArray
     */
    public void commitTransaction() {

        if (isTransaction) {
            transactionInitFlag = false;
            Log.d(TAG, "(commitTransaction) data object = " + jsonObject.toString());
            trackTransaction(eventType, appId);
        } else {
            Log.d(TAG, "transaction terminated, due to not found instance of AgileTransaction");
        }
    }

    /**
     * validate input param
     *
     * @param appId     is application id which is provided by us to the developer
     * @param eventType define the type of event
     */
    private void trackTransaction(@NonNull final String eventType, @NonNull final String appId) {


        String advertising_id = getAdvertisingId(context);
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
            sendLog(appId, android_id, eventType, getTransaction(), time, advertising_id);
        } else {
            Log.d(TAG, "params is empty");
            throw new IllegalArgumentException("param is empty for trackTransaction method");
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

                    //clearLogEvent the log
                    clearTransaction();


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
        clearTransaction();
    }

    /**
     * clearLogEvent the object value
     */
    public void clearTransaction() {
        transactionInitFlag = false;
        jsonObject = new JSONObject();
    }
}