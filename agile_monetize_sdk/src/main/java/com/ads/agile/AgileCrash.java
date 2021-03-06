package com.ads.agile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.ads.agile.room.LogEntity;
import com.ads.agile.room.LogModel;
import com.ads.agile.system.AdvertisingIdClient;
import com.ads.agile.utils.AppLocationService;
import com.ads.agile.utils.UtilConfig;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
/*import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;*/

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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ads.agile.AgileConfiguration.AGILE_ID;
import static com.ads.agile.AgileConfiguration.AGILE_PREF;
import static com.ads.agile.AgileConfiguration.argumentValidation;
import static com.ads.agile.AgileConfiguration.isLog;
import static com.ads.agile.AgileConfiguration.isTransaction;


public class AgileCrash extends Activity  {

    private final String TAG = this.getClass().getSimpleName();
    private Context context;
    private LogModel logModel;
    private int size;
    private static JSONObject jsonObject = new JSONObject();
    private Date date1;
    SharedPreferences prefs;
    String dateTimeKey = "time_duration";
    private Boolean firstTime = false;
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
    private String ImeiFirstslot;
    private String ImeiSecondslot;
    private String GPSAddress;
    private String GPSLocality;
    private String GPSPostalCode;
    private String GPSCountryName;
    private String GPSCountryCode;
    private String _latitude = "false", _longitude = "false";
    private String _curlatitude = "false", _curlongitude = "false";
    long installed;
    AppLocationService appLocationService;
    UtilConfig dataProccessor;
    String packagename;
    String trace_app_uninstall;
    String gpsAdd,gpslocality,gpspostalcode,gpscountryname,gpscountrycode;
    String trace_app_sandbox;

    /**
     * parametric constructor
     *
     * @param context          from the activity

     */
    public AgileCrash(@NonNull Context context) {

        this.context = context;

        appLocationService = new AppLocationService(context);

        try {
            if (loadJSONFromAsset()==null){
                Log.e(context.getPackageName(),"agile-sdk-config.json is Required");
            }
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONObject m_obj = obj.getJSONObject("app");
            String IdPacakageName = m_obj.getString("name");
            String google_playstore = m_obj.getString("available_on_google_playstore");
            trace_app_uninstall = m_obj.getString("trace_app_uninstall");
            trace_app_sandbox = m_obj.getString("sandbox");

            if (google_playstore.equalsIgnoreCase("1")){
                AppId = m_obj.getString("id");
                packagename="";

            }
            else {
                AppId = m_obj.getString("id");
                packagename=context.getPackageName();
                Log.e(TAG,"Warning : Googgle Playstore not available on Your App");
            }

        } catch (JSONException e) {
            e.printStackTrace();

        }

        IMEINUMBER();
        GPSADDRESS();

        prefs = context.getSharedPreferences("com.ads.agile", Context.MODE_PRIVATE);
        Date dato = new Date();
        prefs.edit().putLong(dateTimeKey, dato.getTime()).commit();
        long l = prefs.getLong(dateTimeKey, new Date().getTime());
        date1 = new Date(l);

        dataProccessor = new UtilConfig(context);

        try {
            installed = context
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .firstInstallTime;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        getAdvertisingId();

    }

    @SuppressLint({"MissingPermission", "NewApi"})
    public   void IMEINUMBER(){

        try {

            //IMEI
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            ImeiFirstslot=telephonyManager.getDeviceId(0);
            ImeiSecondslot=telephonyManager.getDeviceId(1);

            if (ImeiFirstslot.equals(ImeiSecondslot)){
                ImeiSecondslot="";
            }
        }
        catch (Exception e){
            ImeiFirstslot="";
            ImeiSecondslot="";
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
            }
            else {
                GPSLocality="";
                GPSPostalCode= "";
                GPSCountryName="";
                GPSCountryCode="";

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            GPSAddress=obj.getAddressLine(0);
            GPSLocality=obj.getLocality();
            GPSPostalCode= obj.getPostalCode();
            GPSCountryName=obj.getCountryName();
            GPSCountryCode=obj.getCountryCode();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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

        if (name.equals("P")) name = "Pie";
        if (name.equals("O")) name = "Oreo";
        if (name.equals("N")) name = "Nougat";
        if (name.equals("M")) name = "Marshmallow";
        if (name.startsWith("O_")) name = "Oreo";
        if (name.startsWith("N_")) name = "Nougat";
        return name;
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






    /**
     * validate input param
     *
     * @param eventType define the type of event
     */
    public  void trackEvent(@NonNull final String eventType) {

        /**
         * if the transaction is enable
         */

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
            SDkVersion = "2.0.9";
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
           /* sendLogToDatabase
                    (
                            appId,
                            eventType,
                            values
                    );*/
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


        try{
            argumentValidation(eventType);  //validation in sendLogToServer
            Location nwLocation = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);

            if (nwLocation != null) {
                _curlatitude = String.valueOf(nwLocation.getLatitude());
                _curlongitude = String.valueOf(nwLocation.getLongitude());

            }
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

            AgileConfiguration.ServiceInterface service = AgileConfiguration.getRetrofit().create(AgileConfiguration.ServiceInterface.class);
            Call<ResponseBody> responseBodyCall = service.createUser
                    (appId,
                            android_id,
                            eventType,
                            values,
                            time,
                            advertising_id, wifiState, deviceOperator, deviceLanguage, deviceModel, deviceOsName, deviceOsVersion,
                            deviceAppVersion, sdkversion,_longitude, _latitude, androidPlatform, localDateTime, localTimezone,"","","","",packagename,gpsAdd,gpslocality,
                            gpspostalcode,gpscountryname,gpscountrycode,ImeiFirstslot,ImeiSecondslot,"","","","0"
                    );
            responseBodyCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    //  Log.d(TAG, "response code = " + response.code());
                    try {
                        if (response.isSuccessful()){
                            String responseString = response.body().string();

                            //  Log.d(TAG, "response body = " + responseString);

                            JSONObject object = new JSONObject(responseString);
                            // boolean status = object.getBoolean("status");
                            clearLogEvent();
                        }

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
                    Log.d(TAG, "onFailure = " + t.getMessage());
                    // sendLogToDatabase(eventType, appId, values);
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();

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

        //Log.d(TAG, "insert log into database");
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


    /**
     * to sync the local database information with server database on background thread
     */


    /**
     * send data from local database to live database when connected to internet
     *
     * @param id        is primary key of every database entry
     * @param appId     is application id which is provided by us to the developer
     * @param eventType define the type of event
     * @param values    could be additional information which describe the eventType in more details
     * @param time      would be always zero if it send directly to the server or else will send the difference of current and stored entry into room database
     */

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
    public static void set(String key, String value) {
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