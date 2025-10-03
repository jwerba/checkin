package com.jwerba.checkin.strategies;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.jwerba.checkin.geolocation.GeofenceBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

public class GeoFencingDetectionStrategy extends BroadcastReceiver implements DetectionStrategy {

    Context context;
    private GeofencingClient geofencingClient;
    private GeofencingRequest request;
    List<Geofence> geofenceList = new ArrayList<>();

    public static final String TAG = "GeoFencingDetectionStrategy";

    // Constructor sin argumentos requerido por Android para BroadcastReceiver
    public GeoFencingDetectionStrategy() {
        // Constructor vacío para el sistema Android
    }

    public GeoFencingDetectionStrategy(Context context) {
        this.context = context;
        geofencingClient = LocationServices.getGeofencingClient(context);
        Intent intent = new Intent(this.context, GeofenceBroadcastReceiver.class);
        PendingIntent pendingIntent = null;
        try {
            pendingIntent = PendingIntent.getBroadcast(this.context, 2607, intent, 0 | PendingIntent.FLAG_MUTABLE);

            geofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId("fence01")
                    .setCircularRegion(-34.7814184, -55.970872, 500)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                    .setLoiteringDelay(5000)
                    .build());
            GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
            builder.addGeofences(geofenceList);
            builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
            this.request = builder.build();
            if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                this.geofencingClient.addGeofences(request, pendingIntent)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: Geofence Added...");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, e.getMessage());
                            }
                        });
            }
        } catch (Exception ex) {
            Log.i(TAG, ex.getMessage());
        }
    }

    @Override
    public DetectionResult check() {
        return new DetectionResult(GeoFencingDetectionStrategy.class, true);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
    }
}
