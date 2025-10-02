package com.jwerba.checkin.storage;

import android.content.Context;

import com.jwerba.checkin.model.Day;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DataManager {
    private final String TAG = getClass().getSimpleName();
    private static DataManager ourInstance = null;
    private boolean isDirty = true;
    private final Object lockObject = new Object();
    private List<Day> inMemoryList = new ArrayList<>();

    private DataManager(){}

    private Storage storage = null;

    public static DataManager getInstance(Context context) {
        if(ourInstance == null) {
            ourInstance = new DataManager(context);
        }
        return ourInstance;
    }


    private DataManager(Context context) {
        this.storage = SQLLiteHelper.getInstance(context);
    }

    public List<Day> get(int year, int month) {
        try {
            List<Day> list = checkDirtinessAndGet();
            return list.stream().filter(d-> { return d.getDate().getYear() == year && d.getDate().getMonthValue() == month; }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<Day> checkDirtinessAndGet() {
        if (isDirty){
            this.inMemoryList = this.storage.getAll();
            synchronized (lockObject) {
                isDirty = false;
            }
        }
         return this.inMemoryList;
    }

    public List<Day> getAll() {
        try {
            return checkDirtinessAndGet();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void add(Day day) {
        try {
            storage.add(day);
            synchronized (lockObject) {
                isDirty = true;
            }
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    checkDirtinessAndGet();
                }
            });
            thread.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
