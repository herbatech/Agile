package com.ads.agile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;

import com.ads.agile.room.LogEntity;
import com.ads.agile.room.LogModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

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

    private Date datata;
    long seconds ;
    SharedPreferences prefs;
    String dateTimeKey = "time_duration";

    private String WifiState="";
    private String DeviceType;
    private String DeviceBrand;
    private String DeviceCarier;
    private String DeviceLanguage;
    private String DeviceModel;
    private String DeviceOsName;
    private String DeviceOsVersion;
    private String DeviceAppVersion;
    private String SDkVersion;
    private String Latittude;
    private String Longitude;
    private String localDateTime;
    private String localTimezone;
    private String AndroidPlatform;


    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    public static final long   UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long   FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    public static final int    REQUEST_CHECK_SETTINGS = 100;

    private String                      mLastUpdateTime;
    private Geocoder                    geocoder;
    private List<Address> addresses;

    private String                      _latitude  = "false",
            _longitude = "false",
            address    = "unknown",
            city       = "unknown",
            state      = "unknown",
            country    = "unknown",
            postalCode = "unknown",
            _google_token;


    public AgileTransaction(@NonNull Context context, @NonNull FragmentActivity activity, @NonNull String eventType) {
        this.context = context;
        this.eventType = eventType;
        transactionInitFlag = true;
        Bundle metadata = getMetaData(context);
        appId= metadata.getString("com.agile.sdk.ApplicationId");






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
        initLocation(activity);
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

    public static Bundle getMetaData(Context context) {
        try {
            return context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
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

    private String checkNetworkStatus(Context context) {

        WifiState ="";
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnectedOrConnecting ()) {
            WifiState="wifi";
            //  Toast.makeText(this, "Wifi", Toast.LENGTH_LONG).show();
        } else if (mobile.isConnectedOrConnecting ()) {
            WifiState="Data";
            //  Toast.makeText(this, "Mobile 3G ", Toast.LENGTH_LONG).show();
        } else {
            WifiState="false";
            //   Toast.makeText(this, "No Network ", Toast.LENGTH_LONG).show();
        }
        return WifiState;
    }

    private void initLocation(final Activity context) {

        geocoder = new Geocoder(context, Locale.getDefault());

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mSettingsClient = LocationServices.getSettingsClient(context);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                updateLocation();

            }
        };

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

        startLocation(context);

    }

    private void startLocation(final Activity context) {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(context, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                        Log.d(TAG, "Started location updates!");

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());

                        updateLocation();
                    }
                })
                .addOnFailureListener(context, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult( context, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.d(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                        }

                        updateLocation();
                    }
                });
    }


    private void updateLocation(){
        if (mCurrentLocation != null) {
            _latitude    = String.valueOf(mCurrentLocation.getLatitude());
            _longitude   = String.valueOf(mCurrentLocation.getLongitude());

        }

    }
    private static String getOsVersionName() {
        Field[] fields = Build.VERSION_CODES.class.getFields();
        String name =  fields[Build.VERSION.SDK_INT + 1].getName();

        if(name.equals("O")) name = "Oreo";
        if(name.equals("N")) name = "Nougat";
        if(name.equals("M")) name = "Marshmallow";

        if(name.startsWith("O_")) name = "Oreo++";
        if(name.startsWith("N_")) name = "Nougat++";

        return name;
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
        try {


            PackageInfo pInfo =   context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String version = pInfo.versionName;
            String verCode = String.valueOf(pInfo.versionCode);
            TimeZone timeZone = TimeZone.getDefault();
            DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString2 = dateFormat2.format(new Date()).toString();

        DeviceBrand = Build.BRAND;
        localDateTime=dateString2;
        localTimezone=timeZone.getID();
        DeviceLanguage= Locale.getDefault().getDisplayLanguage();
        DeviceType= Build.TYPE;
        DeviceModel=Build.MODEL;
        DeviceOsVersion=Build.VERSION.RELEASE;
        DeviceOsName=getOsVersionName();
        DeviceAppVersion=verCode;
        AndroidPlatform="Android";
        Latittude=_latitude;
        Longitude=_longitude;
        SDkVersion="1.1.3";
        WifiState=checkNetworkStatus(context);

/*
        Log.d(TAG,"WifiState  ="+checkNetworkStatus(context));
        Log.d(TAG,"DeviceLanguage  ="+DeviceLanguage);
        Log.d(TAG,"DeviceType  ="+DeviceType);
        Log.d(TAG,"DeviceModel  ="+DeviceModel);
        Log.d(TAG,"DeviceOsVersion  ="+DeviceOsVersion);
        Log.d(TAG,"DeviceOsName  ="+DeviceOsName);
        Log.d(TAG,"DeviceAppVersion  ="+DeviceAppVersion);
        Log.d(TAG,"Latittude  ="+_latitude);
        Log.d(TAG,"Longitude  ="+_longitude);
        Log.d(TAG,"AndroidPlatform  ="+AndroidPlatform );
        Log.d(TAG,"localDateTime  ="+localDateTime);
        Log.d(TAG,"localTimezone  ="+localTimezone);
        Log.d(TAG,"DeviceBrand  ="+ DeviceBrand);
        Log.d(TAG,"SDkVersion  ="+SDkVersion);*/



        argumentValidation(eventType);  //validation in trackEvent

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

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
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

        SharedPreferences prefs = context.getSharedPreferences("com.ads.agile", Context.MODE_PRIVATE);
        String dateTimeKey = "time_duration";

        long l = prefs.getLong(dateTimeKey, new Date().getTime());

        Log.d(TAG, "currentTimeValue     =="+l);

       if (isConnected(context)) {
            sendLogToServer
                    (
                            appId,
                            android_id,
                            eventType,
                            values,
                            time,
                            advertising_id,seconds,
                            WifiState,DeviceLanguage,DeviceType,DeviceModel,DeviceOsVersion,DeviceOsName,
                            DeviceAppVersion,_latitude,_longitude,AndroidPlatform,localDateTime,localTimezone,
                            DeviceBrand,SDkVersion
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
    private void sendLogToServer(@NonNull final String appId, @NonNull String android_id, @NonNull final String eventType, @NonNull final String values, @NonNull final String time, @NonNull String advertising_id,@NonNull long seconds,
                                 @NonNull String wifiState, @NonNull String deviceLanguage, @NonNull String deviceType, @NonNull String deviceModel, @NonNull String deviceOsVersion,
                                 @NonNull String deviceOsName, @NonNull String deviceAppVersion, @NonNull String latittude, @NonNull String longitude, @NonNull String androidPlatform,
                                 @NonNull String localDateTime, @NonNull String localTimezone, @NonNull String deviceOperator, @NonNull String sdkversion) {

        argumentValidation(eventType);  //validation in sendLogToServer

        AgileConfiguration.ServiceInterface service = AgileConfiguration.getRetrofit().create(AgileConfiguration.ServiceInterface.class);
        Call<ResponseBody> responseBodyCall = service.createUser
                (appId,
                        android_id,
                        eventType,
                        values,
                        time,
                        advertising_id,wifiState,deviceOperator,deviceLanguage,deviceModel,deviceOsName,deviceOsVersion,
                        deviceAppVersion,sdkversion,latittude,longitude,androidPlatform,localDateTime,localTimezone
                );
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
              //  Log.d(TAG, "response code = " + response.code());
                try {

                    String responseString = response.body().string();

                    //Log.d(TAG, "response body = " + responseString);

                    JSONObject object = new JSONObject(responseString);
                    boolean status = object.getBoolean("status");
                 //   Log.d(TAG, "status = " + status);

                    //clearLogEvent the log
                    clearTransaction();


                } catch (IOException e) {
                  //  Log.d(TAG, "IOException = " + e.getMessage());
                } catch (JSONException e) {
                  //  Log.d(TAG, "JSONException = " + e.getMessage());
                } finally {
                    response.body().close();
                 //   Log.d(TAG, "retrofit connection closed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
             //   Log.d(TAG, "onFailure = " + t.getMessage());
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

        //Log.d(TAG, "insert log into database");
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