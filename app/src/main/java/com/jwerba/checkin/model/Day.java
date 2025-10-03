package com.jwerba.checkin.model;

import androidx.annotation.Nullable;

import java.time.LocalDate;

public class Day {
    private LocalDate date;
    private String description;
    private DayType dayType;


    public Day(LocalDate date, DayType dayType){
        this.date = date;
        this.dayType = dayType;
    }
    public Day(LocalDate date, DayType dayType, String description){
        this.date = date;
        this.description = description;
        this.dayType = dayType;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if (!obj.getClass().isAssignableFrom(Day.class)){
            return false;
        }
        Day other = (Day)obj;
        if (other.getDate() == null) return false;
        return other.getDate().equals(this.getDate());
    }

    public DayType getDayType() {
        return dayType;
    }

    public void setDayType(DayType dayType) {
        this.dayType = dayType;
    }
}
