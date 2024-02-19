package com.jwerba.checkin.services;

public enum Action
{
    START_SERVICE_DETECTION("com.jwerba.attendance.services.ACTION_START_SERVICE_DETECTION"),
    WIFI_DETECTED("com.jwerba.attendance.services.ACTION_WIFI_DETECTED");

    private String value;
    private Action(String value){
        this.value = value;
    }
}
