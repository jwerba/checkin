package com.jwerba.checkin;

import android.content.Context;

import androidx.work.Data;
import androidx.work.ListenableWorker;

import com.jwerba.checkin.strategies.DetectionResult;
import com.jwerba.checkin.strategies.DetectionStrategy;
import com.jwerba.checkin.strategies.GeoFencingDetectionStrategy;
import com.jwerba.checkin.strategies.WifiDetectionStrategy;

import java.util.ArrayList;
import java.util.List;

public class DetectionStrategyManager {
    private static DetectionStrategyManager INSTANCE = null;
    private Context context;
    private List<DetectionStrategy> strategies = new ArrayList<>();

    private DetectionStrategyManager(Context context) {
        this.context = context;
        strategies.add(new WifiDetectionStrategy(context,"AndroidWifi"));
        strategies.add(new GeoFencingDetectionStrategy(context));
    }

    public static DetectionStrategyManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DetectionStrategyManager(context);
        }
        return INSTANCE;
    }


    public List<DetectionResult> runDetections() {
        List<DetectionResult> successfulResults = new ArrayList<>();
        for (DetectionStrategy strategy: strategies) {
            DetectionResult result = strategy.check();
            // Indicate whether the work finished successfully with the Result
            if (result.isDetected()){
                successfulResults.add(result);
            }
        }
        return successfulResults;
    }
}
