package com.jwerba.checkin.strategies;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DetectionResult {

    private boolean detected;
    private Class strategy;
    private Map<String, String> context;

    public DetectionResult(Class strategy, boolean detected, Map<String, String> context){
        this.strategy = strategy;
        this.detected = detected;
        this.context = context;
    }

    public DetectionResult(Class strategy, boolean detected){
        this(strategy, detected, Collections.unmodifiableMap(new HashMap<String, String>()));
        this.detected = detected;
    }

    public boolean isDetected() {
        return detected;
    }

    public Map<String, String> getContext() {
        return context;
    }

    public Class getStrategy() {
        return strategy;
    }
}
