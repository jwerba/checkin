package com.jwerba.checkin.geolocation;

import static android.widget.Toast.LENGTH_SHORT;
import static androidx.constraintlayout.widget.Constraints.TAG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    public GeofenceBroadcastReceiver(){

    }

    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if (Geofence.GEOFENCE_TRANSITION_ENTER == geofenceTransition) {
            String triggeredGeoFenceId = geofencingEvent.getTriggeringGeofences().get(0)
                    .getRequestId();
            Toast.makeText(context, "entering_geofence", LENGTH_SHORT).show();
        } else if (Geofence.GEOFENCE_TRANSITION_EXIT == geofenceTransition) {
            // Delete the data item when leaving a geofence region.
            Toast.makeText(context, "exiting_geofence", LENGTH_SHORT).show();
        }
    }


    /**
     * Showing a toast message, using the Main thread
     */
    private void showToast(final Context context, final int resourceId) {
        Handler mainThread = new Handler(Looper.getMainLooper());
        mainThread.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, context.getString(resourceId), LENGTH_SHORT).show();
            }
        });
    }

    private void sendNotification(String geofenceTransitionDetails) {

    }

    private String getGeofenceTransitionDetails(GeofenceBroadcastReceiver geofenceBroadcastReceiver, int geofenceTransition, List<Geofence> triggeringGeofences) {
        return "getGeofenceTransitionDetails";
    }
}
