package com.jwerba.checkin.model;

public enum DayType {
    OFFICE_DAY("OFFICE_DAY"),
    WFA_DAY("WFA_DAY"),
    REGULAR_DAY("REGULAR_DAY"),
    HOLIDAY_DAY("HOLIDAY_DAY");

    private String code;
    private DayType(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
