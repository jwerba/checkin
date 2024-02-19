package com.jwerba.checkin.strategies;

import android.app.Service;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.Collections;
import java.util.HashMap;

public class WifiDetectionStrategy extends ServiceDetectionStrategy {
    private String expectedSSID;

    public WifiDetectionStrategy(Service service, String expectedSSID) {
        super(service);
        this.expectedSSID = expectedSSID;
    }

    @Override
    protected DetectionResult check() {
        final WifiManager wifiManager = (WifiManager) this.service.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String mac = info.getMacAddress();
        String ssid = info.getSSID();
        ssid = ssid.replace("\"", "").trim();
        expectedSSID = expectedSSID.trim();
        if (expectedSSID != null && ssid != null && expectedSSID.equals(ssid)){
            HashMap<String, String> context = new HashMap<String, String>();
            context.put("ssid", ssid);
            return new DetectionResult(this.getClass(), true, Collections.unmodifiableMap(context));
        }else{
            return new DetectionResult(this.getClass(), false);
        }
    }
}
