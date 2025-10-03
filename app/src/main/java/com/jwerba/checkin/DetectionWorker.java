package com.jwerba.checkin;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.jwerba.checkin.model.Day;
import com.jwerba.checkin.model.DayType;
import com.jwerba.checkin.storage.DataManager;
import com.jwerba.checkin.strategies.DetectionResult;
import com.jwerba.checkin.strategies.DetectionStrategy;
import com.jwerba.checkin.strategies.GeoFencingDetectionStrategy;
import com.jwerba.checkin.strategies.WifiDetectionStrategy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class DetectionWorker extends Worker {

    private Context context;
    private DetectionStrategyManager manager;
    public DetectionWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
        manager = DetectionStrategyManager.getInstance(context);
    }

    @Override
    public Result doWork() {
        String x = getInputData().getString("param1");
        List<DetectionResult> results = manager.runDetections();
        boolean detected = false;
        for (DetectionResult result: results) {
            detected = result.isDetected();
            if (detected) break;
        }
        Data outputData = new Data.Builder().putString("detected", detected? "true": "false").build();
        return Result.success(outputData);
    }

    private void saveToStorage(DetectionResult result) {
        //DataManager.getInstance(this.getApplicationContext()).add(new Day(LocalDate.now(), DayType.OFFICE_DAY));
    }
}