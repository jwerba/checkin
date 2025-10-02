package com.jwerba.checkin.storage;

import com.jwerba.checkin.model.Day;

import java.util.List;
import java.util.Set;

public interface Storage {
    List<Day> getMonthData(int year, int month) throws Exception;
    void add(Day day) throws Exception;

    List<Day> getAll();
}
