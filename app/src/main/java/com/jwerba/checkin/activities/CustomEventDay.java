package com.jwerba.checkin.activities;

import android.graphics.drawable.Drawable;

import com.applandeo.materialcalendarview.CalendarDay;
import com.applandeo.materialcalendarview.EventDay;
import com.jwerba.checkin.model.DayType;

import java.util.Calendar;

public class CustomEventDay { // extends EventDay {
    private DayType type;
    CalendarDay c;
    public CustomEventDay(Calendar day, DayType type) {
        //super(day);
        this.setType(type);
    }

    public CustomEventDay(Calendar day, DayType type, int drawable) {
        //super(day, drawable);
        this.setType(type);
    }

    public CustomEventDay(Calendar day, DayType type, Drawable drawable) {
        //super(day, drawable);
        this.setType(type);
    }

    public CustomEventDay(Calendar day, DayType type, int drawable, int labelColor) {
        //super(day, drawable, labelColor);
        this.setType(type);
    }

    public CustomEventDay(Calendar day, DayType type, Drawable drawable, int labelColor) {
        //super(day, drawable, labelColor);
        this.setType(type);
    }

    public DayType getType() {
        return type;
    }

    public void setType(DayType type) {
        this.type = type;
    }
}
