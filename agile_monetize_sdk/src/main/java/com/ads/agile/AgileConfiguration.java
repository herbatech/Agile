package com.ads.agile;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class AgileConfiguration {

    private static final String TAG = "AgileConfiguration";
    public static final int JOB_ID = 101;
    static String url = "http://192.168.1.37:8080/";
    public static final String MONETIZE_FILENAME = "agile_monetize.txt";

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