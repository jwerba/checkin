package com.jwerba.checkin.activities;

import android.graphics.drawable.Drawable;

import com.applandeo.materialcalendarview.CalendarDay;
import com.applandeo.materialcalendarview.EventDay;
import com.jwerba.checkin.model.DayType;

import java.util.Calendar;

public class CustomEventDay {
    private DayType type;
    private EventDay eventDay;
    
    public CustomEventDay(Calendar day, DayType type) {
        this.eventDay = new EventDay(day);
        this.setType(type);
    }

    public CustomEventDay(Calendar day, DayType type, int drawable) {
        this.eventDay = new EventDay(day, drawable);
        this.setType(type);
    }

    public CustomEventDay(Calendar day, DayType type, Drawable drawable) {
        this.eventDay = new EventDay(day, drawable);
        this.setType(type);
    }

    public DayType getType() {
        return type;
    }

    public void setType(DayType type) {
        this.type = type;
    }
    
    public EventDay getEventDay() {
        return eventDay;
    }
    
    public Calendar getCalendar() {
        return eventDay.getCalendar();
    }
}
