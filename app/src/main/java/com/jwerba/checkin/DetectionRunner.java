package com.jwerba.checkin;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.work.BackoffPolicy;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.OutOfQuotaPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.jwerba.checkin.strategies.DetectionListener;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DetectionRunner {
    private AppCompatActivity context;
    private Set<DetectionListener> listeners =new HashSet<>();
    public DetectionRunner(AppCompatActivity context){
        this.context = context;
    }

    public void addListener(DetectionListener listener){
        listeners.add(listener);
    }

    private void enqueueWorker(Duration initialDelay) {
        final UUID DETECTION_WORKER = UUID.randomUUID();
        Data.Builder dataBuilder = new Data.Builder();
        dataBuilder.putString("param1", "X");
        Data requestData = dataBuilder.build();
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(DetectionWorker.class)
                .setId(DETECTION_WORKER)
                .setBackoffCriteria(
                        BackoffPolicy.LINEAR,
                        PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS)
                .setInitialDelay(initialDelay)
                .setInputData(requestData).build();
        WorkManager workManager = WorkManager.getInstance(this.context);
        workManager.getWorkInfoByIdLiveData(DETECTION_WORKER)
                .observe(this.context, workInfo -> {
                    if (workInfo.getState() != null &&
                            workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                        for(DetectionListener listener: this.listeners){
                            listener.OnDetected(null, null);
                        }
                        enqueueWorker(Duration.ofSeconds(10));
                    }
                });
        workManager.enqueueUniqueWork(DETECTION_WORKER.toString(), ExistingWorkPolicy.APPEND, workRequest);
    }

    public void start(){
        enqueueWorker(Duration.ofSeconds(10));
    }



}
