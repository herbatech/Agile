package com.ads.agile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.LiveData;
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
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ads.agile.room.LogEntity;
import com.ads.agile.room.LogModel;
import com.ads.agile.system.AdvertisingIdClient;
import com.ads.agile.utils.AgileStateMonitor;
import com.ads.agile.utils.AppLocationService;
import com.ads.agile.utils.UtilConfig;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ads.agile.AgileConfiguration.AGILE_ID;
import static com.ads.agile.AgileConfiguration.AGILE_PREF;
import static com.ads.agile.AgileConfiguration.isLog;
import static com.ads.agile.AgileConfiguration.isTransaction;


public class AgileLog extends Activity implements AgileStateMonitor.NetworkCallBack {

    private final String TAG = this.getClass().getSimpleName();

    private Context context;
    private FragmentActivity activity;
    private LogModel logModel;
    private int size;
    private SynchroniseLogEvent synchroniseLogEvent;
    private static JSONObject jsonObject = new JSONObject();

    private Date date1;
    long seconds;
    SharedPreferences prefs;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor1;
    public static final String MyPREFERENCES = "myprefs";
    public static final  String value = "key";
    int i=0;

    SharedPreferences event_screen_onsharedpreferences;
    SharedPreferences.Editor event_screen_oneditor1;
    public static final String event_screen_onMyPREFERENCES = "event_screen_onmyprefs";
    public static final  String event_screen_onvalue = "key";
    int j=0;

    SharedPreferences last_screen_onsharedpreferences;
    SharedPreferences.Editor last_screen_oneditor1;
    public static final String last_screen_onMyPREFERENCES = "last_screen_onmyprefs";
    public static final  String last_screen_onvalue = "lastkey";




    String dateTimeKey = "time_duration";
    String screendateTimeKey = "screentime_duration";
    String EventCountKey = "EventCountKey";
    private Boolean firstTime = false;
    AgileTransaction agileTransaction;
    private String AppId;
    private String WifiState = "";
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

    private Location mCurrentLocation;
    private String _latitude = "false", _longitude = "false";
    private String _curlatitude = "false", _curlongitude = "false";
    long installed;
    AppLocationService appLocationService;
    UtilConfig dataProccessor;
    String packagename;

    int Transcationcount;

    long starttime;
    long  endtime ;
    long t3;

    private int seconds11=0;
    private boolean startRun;

    private static final int DEFAULT_BLOCK_TIME = 1000;
    private boolean mIsBlockClick;

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
      /*  Bundle metadata = getMetaData(context);
        AppId = metadata.getString("com.agile.sdk.ApplicationId");*/

        //check for transaction instance
        if (agileTransaction instanceof AgileTransaction) {
           // Log.d(TAG, "instance agileTransaction exist");
            isTransaction = true;
        } else {
            //Log.d(TAG, "instance not agileTransaction exist");
            isTransaction = false;
        }
        appLocationService = new AppLocationService(context);
        FirebaseApp.initializeApp(context);



        try{
            Location nwLocation = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);

            if (nwLocation != null) {
                _latitude = String.valueOf(nwLocation.getLatitude());
                _longitude = String.valueOf(nwLocation.getLongitude());

            }
        }
        catch (Exception e){

        }
        new AgileStateMonitor(this).enable(context);


        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONObject m_obj = obj.getJSONObject("app");
            String IdPacakageName = m_obj.getString("name");
            String google_playstore = m_obj.getString("available_on_google_playstore");
            String trace_app_uninstall = m_obj.getString("trace_app_uninstall");

            if (google_playstore.equalsIgnoreCase("1")){
                AppId = m_obj.getString("id");
                packagename="";
               // Log.d(TAG,"DAta GET    ="+AppId+"\n"+IdPacakageName);
                if (trace_app_uninstall.equalsIgnoreCase("1")){
                    agileUninstall();
                }

            }
            else {
                AppId = m_obj.getString("id");
                packagename=context.getPackageName();

                if (trace_app_uninstall.equalsIgnoreCase("1")){
                    agileUninstall();
                }
                Log.e(TAG,"Warning : Googgle Playstore not available on Your App");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        prefs = context.getSharedPreferences("com.ads.agile", Context.MODE_PRIVATE);
        Date dato = new Date();
        prefs.edit().putLong(dateTimeKey, dato.getTime()).commit();
        long l = prefs.getLong(dateTimeKey, new Date().getTime());
        date1 = new Date(l);

        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor1 = sharedpreferences.edit();

        event_screen_onsharedpreferences = context.getSharedPreferences(event_screen_onMyPREFERENCES, Context.MODE_PRIVATE);
        event_screen_oneditor1 = event_screen_onsharedpreferences.edit();

        last_screen_onsharedpreferences = context.getSharedPreferences(last_screen_onMyPREFERENCES, Context.MODE_PRIVATE);
        last_screen_oneditor1 = last_screen_onsharedpreferences.edit();


        dataProccessor = new UtilConfig(context);

        try {
            installed = context
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .firstInstallTime;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        logModel = ViewModelProviders.of(activity).get(LogModel.class);
        logModel.getLiveListAllLog().observe(activity, new Observer<List<LogEntity>>() {
            @Override
            public void onChanged(List<LogEntity> notes) {
               // Log.d(TAG, "size count = " + notes.size());
                size = notes.size();
            }
        });

        //get add id while initialization
        //initialization of transaction
        //initTransaction();

        getAdvertisingId();



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
        return json;
    }


    public String ApkInstallDate(long timestamp) {
        String sDate = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Calendar c = Calendar.getInstance();
        Date netDate = null;
        try {
            netDate = (new Date(timestamp));
            sdf.format(netDate);
            sDate = sdf.format(netDate);
            String currentDateTimeString = sdf.format(c.getTime());
            c.add(Calendar.DATE, -1);

        } catch (Exception e) {
            System.err.println("There's an error in the Date!");
        }
        return sDate;
    }

    private static String getOsVersionName() {
        Field[] fields = Build.VERSION_CODES.class.getFields();
        String name = fields[Build.VERSION.SDK_INT + 1].getName();

        if (name.equals("O")) name = "Oreo";
        if (name.equals("N")) name = "Nougat";
        if (name.equals("M")) name = "Marshmallow";

        if (name.startsWith("O_")) name = "Oreo++";
        if (name.startsWith("N_")) name = "Nougat++";

        return name;
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
          //  Log.d(TAG, "google adv id found, now accesing to it");
            result[0] = getPreferences(context, AGILE_ID);
        } else//if it is empty
        {
          //  Log.d(TAG, "google adv id not found, now accesing to it");
            if (checkForPlayService(context)) {

                new Thread(new Runnable() {
                    public void run() {
                        try {
                            AdvertisingIdClient.AdInfo adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                            String advertisingId = adInfo.getId();
                            result[0] = advertisingId;
                          //  Log.d(TAG, "(getAdvertisingId) Google advertisingId = " + advertisingId);
                            boolean optOutEnabled = adInfo.isLimitAdTrackingEnabled();
                            //save google ad id into shared preference
                            setPreferences(context, AGILE_ID, advertisingId);
                            firstTime = true;
                           // Log.d(TAG, "(getAdvertisingId) Google optOutEnabled = " + optOutEnabled);
                        } catch (Exception e) {
                           // Log.d(TAG, "(getAdvertisingId) catch error" + e.getMessage());
                        }
                    }
                }).start();
            } else {
               // Log.d(TAG, "play service have some issue");
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

    public void sessionComplete() {
      //  Log.d(TAG,"Timer Date  ="+seconds11);
        try {

            Date date2 = new Date();
            long mills = date2.getTime() - date1.getTime();
            seconds = TimeUnit.MILLISECONDS.toSeconds(mills);
            i = sharedpreferences.getInt(value,0);
            j = event_screen_onsharedpreferences.getInt(event_screen_onvalue,0);
            Transcationcount=dataProccessor.getInt("TransactionCount",0);

            set(AgEventParameter.AG_PARAMS_DURATION, seconds+1);
            set(AgEventParameter.AG_PARAMS_EVENT_COUNT,i);
            set(AgEventParameter.AG_PARAMS_TRANSACTION_COUNT,Transcationcount);
            set(AgEventParameter.AG_PARAMS_INSTANCE_COUNT,j+1);
            set(AgEventParameter.AG_PARAMS_SCREEN_DURATION,seconds11);
            trackEvent(AgEventType.AG_EVENT_SESSION);


            editor1.clear();
            editor1.commit();
            event_screen_oneditor1.clear();
            event_screen_oneditor1.commit();
            startRun=false;
            seconds11=0;
            UtilConfig.clearprefernce();
           // Log.d(TAG, "ApkInstaaled   =" + ApkInstallDate(installed));
        }
        catch (Exception e){

        }

    }

    public void agileUninstall(){
        try {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                // send it to server
                boolean isFirstTime = MyPreferencesToken.isFirstToken(context);
                if (isFirstTime) {
                    set(AgEventParameter.AG_PARAMS_INSTALL_TOKEN, token);
                    trackEvent(AgEventType.AG_EVENT_UNINSTALL);
                }
            }
        });

    }
    catch (Exception e){

        //Log.d(TAG,"Excption  ="+e.getMessage());

        }
    }

    public void agileInstall() {
       if (getAdvertisingId() != null) {

            boolean isFirstTime = MyPreferences.isFirst(context);
            if (isFirstTime) {
                set(AgEventParameter.AG_PARAMS_INSTALL_DATE, ApkInstallDate(installed));
                trackEvent(AgEventType.AG_EVENT_INSTALL);

            }
       }
    }

    public void agileAppStart() {
        startRun=true;
        Timer();
        trackEvent(AgEventType.AG_EVENT_SCRREN_START);

    }

    public void agileAppScreenOn() {
        startRun=true;
        trackEvent(AgEventType.AG_EVENT_SCRREN_ON);

      }

    public void agileAppScreenOff() {
        startRun=false;
        Date date2 = new Date();
        long mills = date2.getTime() - date1.getTime();
        seconds = TimeUnit.MILLISECONDS.toSeconds(mills);

        try {
            i = sharedpreferences.getInt(value, 0);
            Transcationcount=dataProccessor.getInt("TransactionCount",0);
            //  Log.d(TAG,"doneCount  ="+evntdonecount);
            set(AgEventParameter.AG_PARAMS_DURATION,seconds);
            set(AgEventParameter.AG_PARAMS_EVENT_COUNT,i);
            set(AgEventParameter.AG_PARAMS_TRANSACTION_COUNT,Transcationcount);
            trackEvent(AgEventType.AG_EVENT_SCRREN_OFF);
        }
        catch (Exception e){

        }



    }

    private void Timer(){

        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
            if(startRun){
                    seconds11++;

            }

                handler.postDelayed(this, 1000);
            }
        });

    }

    @Override
    public void onConnected() {
       // Log.d(TAG, "MainActivity connected to network via AgileLog");

        try {
            syncLog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisconnected() {

    }

    public static class MyPreferences {

        private static final String MY_PREFERENCES = "my_preferences";

        public static boolean isFirst(Context context) {
            final SharedPreferences reader = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
            final boolean first = reader.getBoolean("is_first", true);
            if (first) {
                final SharedPreferences.Editor editor = reader.edit();
                editor.putBoolean("is_first", false);
                editor.commit();
            }
            return first;
        }

    }


    public static class MyPreferencesToken {

        private static final String MY_PREFERENCES_TOKEN = "my_preferences_TOKEN";

        public static boolean isFirstToken(Context context) {
            final SharedPreferences reader = context.getSharedPreferences(MY_PREFERENCES_TOKEN, Context.MODE_PRIVATE);
            final boolean first = reader.getBoolean("is_first_toktn", true);
            if (first) {
                final SharedPreferences.Editor editor = reader.edit();
                editor.putBoolean("is_first_toktn", false);
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
    public void trackEvent(@NonNull final String eventType) {

/*        long mLastClickTime = 1;
        if (SystemClock.elapsedRealtime() - mLastClickTime <1) {
            Log.d(TAG,"do your action  ");
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        Log.d(TAG,"do your action  ="+mLastClickTime);*/

        /**
         * if the transaction is enable
         */
        if (eventType.equalsIgnoreCase(AgEventType.AG_EVENT_CLICK)){
            i += 1;
            editor1.putInt(value, i);
            editor1.apply();
        }
        if (eventType.equalsIgnoreCase(AgEventType.AG_EVENT_LOG_PAGE)){
            set(AgEventParameter.AG_PARAMS_ACTIVITY_PAGE,activity.getClass().getSimpleName());

        }
        if (eventType.equalsIgnoreCase(AgEventType.AG_EVENT_SCRREN_ON)){
            j += 1;
            event_screen_oneditor1.putInt(event_screen_onvalue, j);
            event_screen_oneditor1.apply();
        }
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
            validateLog(eventType, AppId);
        }
    }

    private String checkNetworkStatus(Context context) {

        WifiState = "";
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnectedOrConnecting()) {
            WifiState = "wifi";
            //  Toast.makeText(this, "Wifi", Toast.LENGTH_LONG).show();
        } else if (mobile.isConnectedOrConnecting()) {
            WifiState = "Data";
            //  Toast.makeText(this, "Mobile 3G ", Toast.LENGTH_LONG).show();
        } else {
            WifiState = "false";
            //   Toast.makeText(this, "No Network ", Toast.LENGTH_LONG).show();
        }
        return WifiState;
    }

    /**
     * @param eventType
     * @param appId
     */
    private void validateLog(String eventType, String appId) {
        String advertising_id = getAdvertisingId();
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String time = "0";


        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String version = pInfo.versionName;
            String verCode = String.valueOf(pInfo.versionCode);
            TimeZone timeZone = TimeZone.getDefault();
            DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString2 = dateFormat2.format(new Date()).toString();

//            String simOperatorName = telephonyManager.getSimOperatorName();

            DeviceBrand = Build.BRAND;
            localDateTime = dateString2;
            localTimezone = timeZone.getID();
            DeviceLanguage = Locale.getDefault().getDisplayLanguage();
            DeviceType = Build.TYPE;
            DeviceModel = Build.MODEL;
            DeviceOsVersion = Build.VERSION.RELEASE;
            DeviceOsName = getOsVersionName();
            DeviceAppVersion = verCode;
            AndroidPlatform = "Android";
            Latittude = _latitude;
            Longitude = _longitude;
            SDkVersion = "1.1.3";
            WifiState = checkNetworkStatus(context);
            argumentValidation(eventType);  //validation in trackEvent

            //validate input params
            if (!TextUtils.isEmpty(appId)
                    && !TextUtils.isEmpty(android_id)
                    && !TextUtils.isEmpty(eventType)
                    && !TextUtils.isEmpty(advertising_id)
            ) {
                sendLog(appId, android_id, eventType, getLogEvent(), time, advertising_id);
            } else {
                //  Log.d(TAG, "params is empty");

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
     //   Log.d(TAG, "currentTimeValue11     ==" + appId);
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
                            advertising_id,
                            WifiState, DeviceLanguage, DeviceType, DeviceModel, DeviceOsVersion, DeviceOsName,
                            DeviceAppVersion, Latittude, Longitude, AndroidPlatform, localDateTime, localTimezone,
                            DeviceBrand, SDkVersion
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
    private void sendLogToServer(@NonNull final String appId, @NonNull String android_id, @NonNull final String eventType, @NonNull final String values, @NonNull final String time, @NonNull String advertising_id,
                                 @NonNull String wifiState, @NonNull String deviceLanguage, @NonNull String deviceType, @NonNull String deviceModel, @NonNull String deviceOsVersion,
                                 @NonNull String deviceOsName, @NonNull String deviceAppVersion, @NonNull String latittude, @NonNull String longitude, @NonNull String androidPlatform,
                                 @NonNull String localDateTime, @NonNull String localTimezone, @NonNull String deviceOperator, @NonNull String sdkversion) {

        argumentValidation(eventType);  //validation in sendLogToServer
        try{
            Location nwLocation = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);

            if (nwLocation != null) {
                _curlatitude = String.valueOf(nwLocation.getLatitude());
                _curlongitude = String.valueOf(nwLocation.getLongitude());


            }
        }
        catch (Exception e){

        }


        AgileConfiguration.ServiceInterface service = AgileConfiguration.getRetrofit().create(AgileConfiguration.ServiceInterface.class);
        Call<ResponseBody> responseBodyCall = service.createUser
                (appId,
                        android_id,
                        eventType,
                        values,
                        time,
                        advertising_id, wifiState, deviceOperator, deviceLanguage, deviceModel, deviceOsName, deviceOsVersion,
                        deviceAppVersion, sdkversion,_longitude, _latitude, androidPlatform, localDateTime, localTimezone,"","","","",packagename
                );
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //  Log.d(TAG, "response code = " + response.code());
                try {

                    String responseString = response.body().string();

                    // Log.d(TAG, "response body = " + responseString);

                    JSONObject object = new JSONObject(responseString);
                    boolean status = object.getBoolean("status");
                    clearLogEvent();
                    agileInstall();


                } catch (IOException e) {
                   // Log.d(TAG, "IOException = " + e.getMessage());
                } catch (JSONException e) {
                   // Log.d(TAG, "JSONException = " + e.getMessage());
                } finally {
                    response.body().close();
                    //     Log.d(TAG, "retrofit connection closed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //   Log.d(TAG, "onFailure = " + t.getMessage());
               // sendLogToDatabase(eventType, appId, values);
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

        // Log.d(TAG, "insert log into database");
        LogEntity logEntity = new LogEntity();
        logEntity.setApp_id(appId);
        logEntity.setEvent_type(eventType);
        logEntity.setValue(values);
        logEntity.setDate_time(localDateTime);
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

        //  Log.d(TAG, "(syncLog) called ,size = " + size);
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
                String date_time = logModel.getLiveListAllLog().getValue().get(i).getDate_time();
                long time = Long.parseLong(logModel.getLiveListAllLog().getValue().get(i).getTime());

             /*    Log.d(TAG, "id               = " + id);
                Log.d(TAG, "event type       = " + eventType);
                Log.d(TAG, "app id           = " + appId);
                Log.d(TAG, "event value      = " + value);
                Log.d(TAG, "event time       = " + time);
                Log.d(TAG, "****************************************`*********************************************");*/

                //call webservice to add data to database
                eventProductLogServiceOffline(id, appId, eventType, value, time,
                        WifiState, DeviceLanguage, DeviceType, DeviceModel, DeviceOsVersion, DeviceOsName,
                        DeviceAppVersion, _latitude, _longitude, AndroidPlatform, date_time, localTimezone,
                        DeviceBrand, SDkVersion);
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
    private void eventProductLogServiceOffline(@NonNull final int id, @NonNull String appId, @NonNull String eventType, @NonNull String values, @NonNull long time,
                                               @NonNull String wifiState, @NonNull String deviceLanguage, @NonNull String deviceType, @NonNull String deviceModel, @NonNull String deviceOsVersion,
                                               @NonNull String deviceOsName, @NonNull String deviceAppVersion, @NonNull String latittude, @NonNull String longitude, @NonNull String androidPlatform,
                                               @NonNull String localDateTime, @NonNull String localTimezone, @NonNull String deviceOperator, @NonNull String sdkversion) {

        String advertising_id = getAdvertisingId();
        time = (System.currentTimeMillis() - time) / 1000;
        String wifi = checkNetworkStatus(context);


           argumentValidation(eventType);  //validation in eventProductLogServiceOffline

            Location nwLocation = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);

            if (nwLocation != null) {
                _curlatitude = String.valueOf(nwLocation.getLatitude());
                _curlongitude = String.valueOf(nwLocation.getLongitude());

            }

            DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString2 = dateFormat2.format(new Date()).toString();


            AgileConfiguration.ServiceInterface service = AgileConfiguration.getRetrofit().create(AgileConfiguration.ServiceInterface.class);
            Call<ResponseBody> responseBodyCall = service.createUser
                    (appId,
                            Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID),
                            eventType,
                            values,
                            String.valueOf(time),
                            advertising_id, wifi, deviceOperator, deviceLanguage, deviceModel, deviceOsName, deviceOsVersion,
                            deviceAppVersion, sdkversion, _curlongitude, _curlatitude, androidPlatform,localDateTime,localTimezone,_longitude,_latitude,dateString2,localTimezone,packagename
                    );

            responseBodyCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                // Log.d(TAG, "response code, id = " + id + " = " + response.code());

                    try {

                        String responseString = response.body().string();
                         //   Log.d(TAG, "response body " + id + " = " + responseString);

                        JSONObject object = new JSONObject(responseString);
                        boolean status = object.getBoolean("status");
                        //     Log.d(TAG, "status = " + status);

                        if (status) {
                            //delete record from the database if the response is true
                            logModel.singleDeleteLog(id);
                        } else {
                            //do not delete record from the database if the response is false
                        }

                    } catch (IOException e) {
                        //     Log.d(TAG, "IOException = " + e.getMessage());
                        synchroniseLogEvent.cancel(true);
                    } catch (JSONException e) {
                        //    Log.d(TAG, "JSONException = " + e.getMessage());
                        synchroniseLogEvent.cancel(true);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //  Log.d(TAG, "onFailure = " + t.getMessage());
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
            //  Log.d(TAG, "(getPreferences) catch exception = " + e.getMessage());
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
            //Log.d(TAG, "(set) String int catch error = " + e.getMessage());
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
           // Log.d(TAG, "(set) String float catch error = " + e.getMessage());
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
            //Log.d(TAG, "(set) String long catch error = " + e.getMessage());
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
           // Log.d(TAG, "(set) String String catch error = " + e.getMessage());
        }
    }

    public void set(String key, JSONObject value) {
        try {
            jsonObject.put(key, value);
        } catch (Exception e) {
           // Log.d(TAG, "(set) String String catch error = " + e.getMessage());
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
           // Log.d(TAG, "(set) String boolean catch error = " + e.getMessage());
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
            //Log.d(TAG, "(set) String short catch error = " + e.getMessage());
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
           // Log.d(TAG, "(set) String short catch error = " + e.getMessage());
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