package com.jwerba.checkin.strategies;

import java.util.Map;

public interface DetectionListener {
    void OnDetected(Class detectionStrategy, Map<String, String> context);
}
