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
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.ads.agile.room.LogEntity;
import com.ads.agile.room.LogModel;
import com.ads.agile.utils.AppLocationService;
import com.ads.agile.utils.UtilConfig;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
/*import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;*/
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
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
    String packagename;

    private String ImeiFirstslot;
    private String ImeiSecondslot;


    private String GPSAddress;
    private String GPSLocality;
    private String GPSPostalCode;
    private String GPSCountryName;
    private String GPSCountryCode;
    String gpsAdd,gpslocality,gpspostalcode,gpscountryname,gpscountrycode;


    private String                      _latitude  = "false", _longitude = "false";
    private String                      _curlatitude  = "false", _curlongitude = "false";

    AppLocationService appLocationService;
    UtilConfig dataProccessor;
    String ValidateInterface,trace_app_sandbox;

    int i=0;
    public AgileTransaction(@NonNull Context context, @NonNull FragmentActivity activity, @NonNull String eventType) {
        this.context = context;
        this.eventType = eventType;
        transactionInitFlag = true;

        dataProccessor = new UtilConfig(context);

        try {
            if (loadJSONFromAsset()==null){
                Log.e(context.getPackageName(),"agile-sdk-config.json is Required");
            }
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONObject m_jArry = obj.getJSONObject("app");
            String IdPacakageName = m_jArry.getString("name");
            String google_playstore = m_jArry.getString("available_on_google_playstore");
            trace_app_sandbox = m_jArry.getString("sandbox");


            if (google_playstore.equalsIgnoreCase("1")){

                appId = m_jArry.getString("id");
                packagename="";
               // Log.d(TAG,"DAta GET    ="+AppId+"\n"+IdPacakageName);

            }
            else {
                appId = m_jArry.getString("id");
                packagename=context.getPackageName();
                Log.e(TAG,"Warning : Googgle Playstore not available in playstore ");
            }

            if(trace_app_sandbox.equalsIgnoreCase("1")){

                ValidateInterface ="log.php";

            }

            else {

                ValidateInterface= "log.js";
            }

        } catch (JSONException e) {
            e.printStackTrace();


        }

        IMEINUMBER();
        GPSADDRESS();

        appLocationService = new AppLocationService(context);
        try{
            Location nwLocation = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);

            if (nwLocation != null) {
                _latitude = String.valueOf(nwLocation.getLatitude());
                _longitude = String.valueOf(nwLocation.getLongitude());

            }
        }
        catch (Exception e){

        }

        logModel = ViewModelProviders.of(activity).get(LogModel.class);
        logModel.getLiveListAllLog().observe(activity, new Observer<List<LogEntity>>() {
            @Override
            public void onChanged(List<LogEntity> notes) {
               // Log.d(TAG, "size count = " + notes.size());
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
       // initLocation(activity);
    }

    public static void trackTransaction(String agileEventScrrenCrash) {
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = context.getAssets().open("agile-sdk-config.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        //  Log.d(TAG,"Excption  ="+json);
        return json;
    }

    @SuppressLint("MissingPermission")
    public   void IMEINUMBER(){

        try {

            //IMEI
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ImeiFirstslot=telephonyManager.getDeviceId(0);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ImeiSecondslot=telephonyManager.getDeviceId(1);
            }
           // Log.d(TAG,"IMEI NUMBER   ="+  ImeiFirstslot+"\n"+ImeiSecondslot);



        }
        catch (Exception e){
            ImeiFirstslot="";
            ImeiSecondslot="";
           // Log.d(TAG,"IMEI NUMBER   ="+  ImeiFirstslot);
            //Log.d(TAG,"IMEI NUMBER   =11"+ ImeiSecondslot);

        }


    }

    public  void GPSADDRESS(){

        try {



            //location
            Location nwLocation = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);
            if (nwLocation != null) {
                _latitude = String.valueOf(nwLocation.getLatitude());
                _longitude = String.valueOf(nwLocation.getLongitude());

                getAddress(nwLocation.getLatitude(),nwLocation.getLongitude());
              //  Log.d(TAG, "Address  =" + GPSLocality);
            }
            else {
                GPSLocality="";
                GPSPostalCode= "";
                GPSCountryName="";
                GPSCountryCode="";
             //   Log.d(TAG, "Address  =" + GPSLocality);

            }
        }
        catch (Exception e){

            // Log.d(TAG,"IMEI NUMBER   =11"+   e.getMessage());
        }
    }

    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);

            GPSAddress=obj.getAddressLine(0);
            GPSLocality=obj.getLocality();
            GPSPostalCode= obj.getPostalCode();
            GPSCountryName=obj.getCountryName();
            GPSCountryCode=obj.getCountryCode();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         //   Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public AgileTransaction(@NonNull Context context, @NonNull FragmentActivity activity) {
        this.context = context;

        logModel = ViewModelProviders.of(activity).get(LogModel.class);
        logModel.getLiveListAllLog().observe(activity, new Observer<List<LogEntity>>() {
            @Override
            public void onChanged(List<LogEntity> notes) {
              //  Log.d(TAG, "size count = " + notes.size());
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
               // Log.d(TAG, "(set) String int catch error = " + e.getMessage());
            }
        } else {
            if (String.valueOf(counter).equalsIgnoreCase("1")) {
                setPreferences(context, AGILE_CRASH_COUNTER, "2");
                throw new IllegalStateException(key + " => " + value + ", transaction can not be initiated, since the transaction is not started");
            } else {
                //setPreferences(context,AGILE_CRASH_COUNTER,"1");
              //  Log.d(TAG, "crash suppressed");
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
               // Log.d(TAG, "(set) String float catch error = " + e.getMessage());
            }
        } else {
            if (String.valueOf(counter).equalsIgnoreCase("1")) {
                setPreferences(context, AGILE_CRASH_COUNTER, "2");
                throw new IllegalStateException(key + " => " + value + ", transaction can not be initiated, since the transaction is not started");
            } else {
                //setPreferences(context,AGILE_CRASH_COUNTER,"1");
                //Log.d(TAG, "crash suppressed");
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
               // Log.d(TAG, "(set) String long catch error = " + e.getMessage());
            }
        } else {
            if (String.valueOf(counter).equalsIgnoreCase("1")) {
                setPreferences(context, AGILE_CRASH_COUNTER, "2");
                throw new IllegalStateException(key + " => " + value + ", transaction can not be initiated, since the transaction is not started");
            } else {
                //setPreferences(context,AGILE_CRASH_COUNTER,"1");
               // Log.d(TAG, "crash suppressed");
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
                //Log.d(TAG, "(set) String String catch error = " + e.getMessage());
            }
        } else {
            if (String.valueOf(counter).equalsIgnoreCase("1")) {
                setPreferences(context, AGILE_CRASH_COUNTER, "2");
                throw new IllegalStateException(key + " => " + value + ", transaction can not be initiated, since the transaction is not started");
            } else {
                //setPreferences(context,AGILE_CRASH_COUNTER,"1");
               // Log.d(TAG, "crash suppressed");
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
               // Log.d(TAG, "(set) String boolean catch error = " + e.getMessage());
            }
        } else {
            if (String.valueOf(counter).equalsIgnoreCase("1")) {
                setPreferences(context, AGILE_CRASH_COUNTER, "2");
                throw new IllegalStateException(key + " => " + value + ", transaction can not be initiated, since the transaction is not started");
            } else {
                //setPreferences(context,AGILE_CRASH_COUNTER,"1");
                //Log.d(TAG, "crash suppressed");
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
               // Log.d(TAG, "(set) String short catch error = " + e.getMessage());
            }
        } else {
            if (String.valueOf(counter).equalsIgnoreCase("1")) {
                setPreferences(context, AGILE_CRASH_COUNTER, "2");
                throw new IllegalStateException(key + " => " + value + ", transaction can not be initiated, since the transaction is not started");
            } else {
                //setPreferences(context,AGILE_CRASH_COUNTER,"1");
              //  Log.d(TAG, "crash suppressed");
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
        Log.d(TAG, "(commitTransaction) data object = " + jsonObject.toString());
        if (eventType.equalsIgnoreCase("ag_transaction")){
            i += 1;
            dataProccessor.setInt("TransactionCount",i);
        }


        if (isConnected(context)) {
           ////////////////////////

            if(ValidateInterface.equalsIgnoreCase("log.php")){
                AgileConfiguration.ServiceInterfaceEnable service = AgileConfiguration.getRetrofit().create(AgileConfiguration.ServiceInterfaceEnable.class);
                Call<ResponseBody> responseBodyCall = service.createUser1(appId);
                responseBodyCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.d(TAG, "response code enable = " + response.code());
                        try {

                            String responseString = response.body().string();

                            Log.d(TAG, "response body enable = " + responseString);

                            JSONObject object = new JSONObject(responseString);
                            boolean status = object.getBoolean("get_app_status");

                            if (status){
                                if (isTransaction) {
                                    transactionInitFlag = false;
                                    //  Log.d(TAG, "(commitTransaction) data object = " + jsonObject.toString());
                                    trackTransaction(eventType, appId);
                                } else {
                                    //    Log.d(TAG, "transaction terminated, due to not found instance of AgileTransaction");
                                }
                            }


                        } catch (IOException e) {
                            Log.d(TAG, "IOException = " + e.getMessage());
                        } catch (JSONException e) {
                            Log.d(TAG, "JSONException = " + e.getMessage());
                        } finally {
                            response.body().close();
                            //      Log.d(TAG, "retrofit connection closed");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d(TAG, "onFailure = " + t.getMessage());
                        // sendLogToDatabase(eventType, appId, values);
                    }
                });
            }
            else {
                AgileConfiguration.ServiceInterfaceEnable1 service = AgileConfiguration.getRetrofit().create(AgileConfiguration.ServiceInterfaceEnable1.class);
                Call<ResponseBody> responseBodyCall = service.createUser1(appId);
                responseBodyCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.d(TAG, "response code enable = " + response.code());
                        try {

                            String responseString = response.body().string();

                            Log.d(TAG, "response body enable = " + responseString);

                            JSONObject object = new JSONObject(responseString);
                            boolean status = object.getBoolean("get_app_status");

                            if (status){
                                if (isTransaction) {
                                    transactionInitFlag = false;
                                    //  Log.d(TAG, "(commitTransaction) data object = " + jsonObject.toString());
                                    trackTransaction(eventType, appId);
                                } else {
                                    //    Log.d(TAG, "transaction terminated, due to not found instance of AgileTransaction");
                                }
                            }
                        } catch (IOException e) {
                            Log.d(TAG, "IOException = " + e.getMessage());
                        } catch (JSONException e) {
                            Log.d(TAG, "JSONException = " + e.getMessage());
                        } finally {
                            response.body().close();
                            //      Log.d(TAG, "retrofit connection closed");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d(TAG, "onFailure = " + t.getMessage());
                        // sendLogToDatabase(eventType, appId, values);
                    }
                });
            }


            ///////////////////////
        } else {
            //save data into sqlite database
            //   Log.d(TAG, "network not connected");
            sendLogToDatabase
                    (
                            appId,
                            eventType,
                            getTransaction()
                    );
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

    private static String getOsVersionName() {
        Field[] fields = Build.VERSION_CODES.class.getFields();
        String name = "UNKNOWN";
        for (Field field : fields) {
            try {
                if (field.getInt(Build.VERSION_CODES.class) == Build.VERSION.SDK_INT) {
                    name = field.getName();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if(name.equals("P")) name = "Pie";
        if(name.equals("O")) name = "Oreo";
        if(name.equals("N")) name = "Nougat";
        if(name.equals("M")) name = "Marshmallow";

        if(name.startsWith("O_")) name = "Oreo";
        if(name.startsWith("N_")) name = "Nougat";

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
        Log.d(TAG, "params is empty  ="+advertising_id);

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
        DeviceAppVersion=version;
        AndroidPlatform="Android";
        Latittude=_latitude;
        Longitude=_longitude;
        SDkVersion = "2.0.2";
        WifiState=checkNetworkStatus(context);

        argumentValidation(eventType);  //validation in trackEvent

        //validate input params
        if (!TextUtils.isEmpty(appId)
                && !TextUtils.isEmpty(android_id)
                && !TextUtils.isEmpty(eventType)
                && !TextUtils.isEmpty(advertising_id)
        ) {
            sendLog(appId, android_id, eventType, getTransaction(), time, advertising_id);
        } else {
           // Log.d(TAG, "params is empty");
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

      //  Log.d(TAG, "currentTimeValue     =="+l);

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
         //   Log.d(TAG, "network not connected");
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

        if (GPSAddress !=null){

            gpsAdd=GPSAddress;
            gpslocality=GPSLocality;
            gpspostalcode=GPSPostalCode;
            gpscountryname=GPSCountryName;
            gpscountrycode=GPSCountryCode;


        }
        else {
            gpsAdd="";
            gpslocality="";
            gpspostalcode="";
            gpscountryname="";
            gpscountrycode="";


        }

        if (ValidateInterface.equalsIgnoreCase("log.php")){
            Log.d(TAG, "ValidateInterface response code = " + ValidateInterface);

            AgileConfiguration.ServiceInterface service = AgileConfiguration.getRetrofit().create(AgileConfiguration.ServiceInterface.class);
            Call<ResponseBody> responseBodyCall = service.createUser
                    (appId,
                            android_id,
                            eventType,
                            values,
                            time,
                            advertising_id, wifiState, deviceOperator, deviceLanguage, deviceModel, deviceOsName, deviceOsVersion,
                            deviceAppVersion, sdkversion,_longitude, _latitude, androidPlatform, localDateTime, localTimezone,"","","","",packagename,gpsAdd,gpslocality,
                            gpspostalcode,gpscountryname,gpscountrycode,ImeiFirstslot,ImeiSecondslot,"","",""
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
        else{

            AgileConfiguration.ServiceInterface1 service = AgileConfiguration.getRetrofit().create(AgileConfiguration.ServiceInterface1.class);
            Call<ResponseBody> responseBodyCall = service.createUser
                    (appId,
                            android_id,
                            eventType,
                            values,
                            time,
                            advertising_id, wifiState, deviceOperator, deviceLanguage, deviceModel, deviceOsName, deviceOsVersion,
                            deviceAppVersion, sdkversion,_longitude, _latitude, androidPlatform, localDateTime, localTimezone,"","","","",packagename,gpsAdd,gpslocality,
                            gpspostalcode,gpscountryname,gpscountrycode,ImeiFirstslot,ImeiSecondslot,"","",""
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


        String androidId=Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String appIdencodedString = AgileAESHelper.encryption(appId);
        String eventTypeencodedString =AgileAESHelper.encryption(eventType);
        String valuesencodedString = AgileAESHelper.encryption(values);;
        String localDateTimeencodedString =AgileAESHelper.encryption(localDateTime);;
        String androidIdencodedString = AgileAESHelper.encryption(androidId);;

        //Log.d(TAG, "insert log into database");
        LogEntity logEntity = new LogEntity();
        logEntity.setApp_id(appIdencodedString);
        logEntity.setEvent_type(eventTypeencodedString);
        logEntity.setValue(valuesencodedString);
        logEntity.setAndroid_id(androidIdencodedString);
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