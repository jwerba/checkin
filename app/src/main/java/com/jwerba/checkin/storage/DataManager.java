package com.jwerba.checkin.storage;

import android.content.Context;

import com.jwerba.checkin.model.Day;

import java.io.IOException;
import java.text.ParseException;
import java.util.Set;

public class DataManager {
    private final String TAG = getClass().getSimpleName();
    private static DataManager ourInstance = null;

    private DataManager(){}

    private FileStorage storage = null;

    public static DataManager getInstance(Context context) {
        if(ourInstance == null) {
            ourInstance = new DataManager(context);
        }
        return ourInstance;
    }


    private DataManager(Context context) {
        this.storage = new FileStorage(context);
    }

    public Set<Day> get(int year, int month) {
        try {
            return this.storage.getMonthData(year, month);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void add(Day day) {
        try {
            storage.add(day);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
