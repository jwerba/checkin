package com.jwerba.checkin.strategies;

import android.app.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class ServiceDetectionStrategy {
    Set<DetectionListener> listeners = new HashSet<>();
    protected Service service;
    public ServiceDetectionStrategy(Service service){
        this.service = service;
    }

    public boolean addListener(DetectionListener listener){
        return listeners.add(listener);
    }

    protected abstract DetectionResult check();

    public void trigger(){
        DetectionResult result = check();
        if (result.isDetected()){
            OnDetected(result.getStrategy(), result.getContext());
        }
    }

    protected void OnDetected(Class strategy, Map<String, String> context){
        this.listeners.forEach(listener->{
            listener.OnDetected(strategy, context);
        });
    }
}
