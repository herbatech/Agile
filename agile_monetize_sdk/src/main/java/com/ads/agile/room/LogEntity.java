package com.ads.agile.room;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "tblLog")
public class LogEntity {


    /*id______event_type________app_id________eventid________values__________android_id
    * 1
    * 2
    * 3
    * 4
    * 5
    * */


    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int id;

    @NonNull
    public String event_type;

    @NonNull
    public String app_id;

    @NonNull
    public String event_id;

    @NonNull
    public String value;

    @NonNull
    public String android_id;

    @NonNull
    public String time = new LogTime().getTime();

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setAndroid_id(String android_id) {
        this.android_id = android_id;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEvent_type() {
        return event_type;
    }

    public String getApp_id() {
        return app_id;
    }

    public String getEvent_id() {
        return event_id;
    }

    public String getValue() {
        return value;
    }

    public String getAndroid_id() {
        return android_id;
    }

    public String getTime() {
        return time;
    }

    public LogEntity(String event_type, String app_id, String event_id, String value, String android_id, String time) {
        this.event_type = event_type;
        this.app_id = app_id;
        this.event_id = event_id;
        this.value = value;
        this.android_id = android_id;
        this.time = time;
    }

    public LogEntity() {
    }

}