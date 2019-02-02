package com.ads.agile.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AgileEvent {

    private final String TAG = this.getClass().getSimpleName();

    private static String eventName;

    private static JSONObject jsonObject = new JSONObject();
    private static JSONArray jsonArray = new JSONArray();


    public AgileEvent(@NonNull String eventName) {
        this.eventName = eventName;
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


//        Iterator myVeryOwnIterator = list.keySet().iterator();
//        while (myVeryOwnIterator.hasNext()) {
//
//            String key = (String) myVeryOwnIterator.next();
//            String value1 = (String) list.get(key);
//            Log.d(TAG, "Key     : " + key);
//            Log.d(TAG, "Value   : " + value1);
//
//            if (value1 == value) {
//                Log.d(TAG, "match found = " + value + " & " + value1);
//                list.remove(key);
//                break;
//            } else {
//                Log.d(TAG, "not found the value");
//            }
//        }

    }

    /**
     * get the existing list which
     *
     * @return the Map instance
     */
    public JSONArray getList() {
        return jsonArray;
    }

    /**
     * @return
     */
    public String getEvent() {
        return jsonObject.toString();
    }

    /**
     * to add event into the database
     *
     * @param goToSecondActivity
     * @param event
     * @param checkout
     */
    public void addEvent(String goToSecondActivity, AgileEvent event, boolean checkout) {
        Log.d(TAG, "(addEvent) list String = " + jsonArray.toString());
        Log.d(TAG, "(addEvent) list size = " + jsonArray.length());
    }

    /**
     * clear the object value
     */
    public void clear() {
        jsonObject = new JSONObject();
    }

    /**
     * get the event name
     *
     * @return
     */
    public String putExtras() {
        return jsonObject.toString();
    }

    /**
     * add jsonObject to jsonArray
     */
    public void commit() {
        //jsonArray.put(jsonObject);
        Log.d(TAG, "(commit) list = " + jsonObject.toString());
    }
}