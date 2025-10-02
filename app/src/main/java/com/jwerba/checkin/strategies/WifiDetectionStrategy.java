package com.jwerba.checkin.strategies;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.Collections;
import java.util.HashMap;

public class WifiDetectionStrategy implements DetectionStrategy {
    private String expectedSSID;
    private Context context;


    public WifiDetectionStrategy(Context context, String expectedSSID) {
        this.expectedSSID = expectedSSID;
        this.context = context;
    }

    @Override
    public DetectionResult check() {
        final WifiManager wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String mac = info.getMacAddress();
        String ssid = info.getSSID();
        ssid = ssid.replace("\"", "").trim();
        expectedSSID = expectedSSID.trim();
        return new DetectionResult(this.getClass(), false, Collections.emptyMap());
        /*
        if (expectedSSID != null && ssid != null && expectedSSID.equals(ssid)){
            HashMap<String, String> context = new HashMap<String, String>();
            context.put("ssid", this.expectedSSID);
            HashMap<String, String> additionalInfo = new HashMap<String, String>();
            additionalInfo.put("ssid", ssid);
            return new DetectionResult(this.getClass(), true, Collections.unmodifiableMap(additionalInfo));
        }else{
            return new DetectionResult(this.getClass(), false);
        }*/
    }
}
