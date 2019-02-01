package com.ads.agile.utils;

import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import androidx.annotation.NonNull;

public class AgileEvent {

    private final String TAG = this.getClass().getSimpleName();

    private static Map<String, Object> list = new HashMap<>();
    private static String eventName;


    public AgileEvent(@NonNull String eventName) {
        this.eventName = eventName;
    }


    /**
     * @param key   String data type
     * @param value int data type
     */
    public void set(String key, int value) {
        list.put(key, value);
    }

    /**
     * @param key   String data type
     * @param value float data type
     */
    public void set(String key, float value) {
        list.put(key, value);
    }

    /**
     * @param key   String data type
     * @param value long data type
     */
    public void set(String key, long value) {
        list.put(key, value);
    }

    /**
     * @param key   String data type
     * @param value String data type
     */
    public void set(String key, String value) {
        list.put(key, value);
    }

    /**
     * @param key   String data type
     * @param value boolean data type
     */
    public void set(String key, boolean value) {
        list.put(key, value);
    }

    /**
     * @param key   String data type
     * @param value short data type
     */
    public void set(String key, short value) {
        list.put(key, value);
    }

    public void unset(String value) {

        Log.d(TAG, "list size = " + list.size());

        Iterator myVeryOwnIterator = list.keySet().iterator();
        while (myVeryOwnIterator.hasNext()) {

            String key = (String) myVeryOwnIterator.next();
            String value1 = (String) list.get(key);
            Log.d(TAG, "Key     : " + key);
            Log.d(TAG, "Value   : " + value1);

            if (value1 == value) {
                Log.d(TAG, "match found = " + value + " & " + value1);
                list.remove(key);
                break;
            } else {
                Log.d(TAG, "not found the value");
            }
        }
    }

    public Map<String, Object> getList() {
        return list;
    }

    public void addEvent(String goToSecondActivity, AgileEvent event, boolean checkout) {
        Log.d(TAG, "(addEvent) list String = " + list.toString());
        Log.d(TAG, "(addEvent) list size = " + list.size());
    }

    public void clear() {
        list.clear();
    }
}