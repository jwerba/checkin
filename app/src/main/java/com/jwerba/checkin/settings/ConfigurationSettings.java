package com.jwerba.checkin.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ConfigurationSettings {
    private static ConfigurationSettings ourInstance = null;
    public static final String SSID = "pref_user_ssid";

    private ConfigurationSettings(){}
    public static ConfigurationSettings getInstance() {
        if(ourInstance == null) {
            ourInstance = new ConfigurationSettings();
        }
        return ourInstance;
    }

    private String get( Context context, String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, "" +
                "");
    }

    public String getSsid(Context context){
        return get(context, SSID);
        //return "corp";
        //return "TP-Link_D984_5G";
        //return "AndroidWifi";
    }

}
