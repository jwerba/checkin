package com.jwerba.checkin;

import android.content.Context;

import com.jwerba.checkin.strategies.DetectionListener;
import com.jwerba.checkin.strategies.DetectionStrategy;

import java.util.ArrayList;
import java.util.List;

public class DetectionManager {
    private static DetectionManager INSTANCE = null;
    private List<DetectionListener> listeners = new ArrayList<>();

    public static DetectionManager getInstance(Context context) {
        if (INSTANCE == null) INSTANCE = new DetectionManager();
        return INSTANCE;
    }



}
