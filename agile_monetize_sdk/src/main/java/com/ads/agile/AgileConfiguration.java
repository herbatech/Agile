package com.ads.agile;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class AgileConfiguration {

    private static final String TAG = "AgileConfiguration";
    public static final int JOB_ID = 101;
    static String url = "http://192.168.1.37:8080/";
    //static String url = "http://log.agileadnetwork.com:8080/";

    //retrofit instance
    public static Retrofit getRetrofit() {

//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    //.client(client)
                    .build();
            return retrofit;
    }

    //interface for retrofit
    public interface ServiceInterface {
        @FormUrlEncoded
        @POST("log.js")
        Call<ResponseBody> createUser
                (
                        @Field("app_id") String app_id,
                        @Field("android_id") String android_id,
                        @Field("event_type") String event_type,
                        @Field("event_value") String event_value,
                        @Field("timestamp_diff") String timestamp_diff,
                        @Field("advertising_id") String advertising_id
                );
    }
}