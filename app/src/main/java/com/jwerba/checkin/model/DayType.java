package com.jwerba.checkin.model;

import android.content.Intent;

public enum DayType {
    OFFICE_DAY("OFFICE_DAY", 0),
    WFA_DAY("WFA_DAY", 1),
    REGULAR_DAY("REGULAR_DAY", -1),
    HOLIDAY_DAY("HOLIDAY_DAY", 2),
    VERIFIED_OFFICE_DAY("VERIFIED_OFFICE_DAY", 3);

    private String code;
    private Integer id;
    private DayType(String code,  Integer id){
        this.code = code;
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public Integer getId() {
        return id;
    }

    public static DayType fromId(int id){
        DayType d = DayType.REGULAR_DAY;
        switch (id){
            case 0:
                d = OFFICE_DAY;
                break;
            case 1:
                d = WFA_DAY;
                break;
            case 2:
                d = HOLIDAY_DAY;
                break;
            case 3:
                d = VERIFIED_OFFICE_DAY;
                break;
        }
        return d;
    }
}
