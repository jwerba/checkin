package com.jwerba.checkin.services;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;

import com.jwerba.checkin.helpers.CustomNotificationBuilder;
import com.jwerba.checkin.model.Day;
import com.jwerba.checkin.model.DayType;
import com.jwerba.checkin.activities.MainActivity;
import com.jwerba.checkin.storage.FileStorage;
import com.jwerba.checkin.strategies.DetectionListener;
import com.jwerba.checkin.strategies.WifiDetectionStrategy;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Map;

public class MainBackgroundService extends Service implements DetectionListener {


    private final String TAG = getClass().getTypeName();
    private final String ALARM_INTENT_ACTION = getClass().getTypeName() + ".ALARM_INTENT";
    private final Intent alarmIntent = new Intent(ALARM_INTENT_ACTION);
    private WifiDetectionStrategy detectionStrategy;
    private final CustomNotificationBuilder notificationBuilder = new CustomNotificationBuilder(this);


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (alarmIntent.getAction().equals(intent.getAction())){
                if (detectionStrategy != null)
                    detectionStrategy.trigger();
            }
        }
    };


    @Override
    public void onDestroy() {
        Log.i(TAG, "MainBackgroundService DESTROYED");
        this.unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i(TAG, "MainBackgroundService STARTED");


        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, alarmIntent, PendingIntent.FLAG_NO_CREATE | FLAG_IMMUTABLE);
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
        alarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_HALF_HOUR,
                10000,
                pendingIntent);
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "MainService CREATED");
        IntentFilter intentFilter = new IntentFilter(String.valueOf(ALARM_INTENT_ACTION));
        this.registerReceiver(broadcastReceiver, intentFilter);
        super.onCreate();
        String ssidToWatchFor = getConfiguredSSID();
        detectionStrategy = new WifiDetectionStrategy(this, ssidToWatchFor);
        detectionStrategy.addListener(this);
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "MainBackgroundService ON_START_COMMAND");
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }


    private void saveToStorage(Day day){
        FileStorage storage = new FileStorage(this);
        try {
            storage.add(day);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private String getConfiguredSSID() {
        SharedPreferences pref = android.preference.PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String ssid = pref.getString("pref_user_ssid", "AndroidWifi");
        return ssid;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void OnDetected(Class detectionStrategy, Map<String, String> context) {
        saveToStorage(new Day(LocalDate.now(), DayType.OFFICE_DAY));
        notificationBuilder.notify(MainActivity.class, "bigText", "bigContentTitle", "summaryText","contentTitle", "contentText");
        String ssid = context.get("ssid");

        //Intent i = new Intent(String.valueOf(Action.WIFI_DETECTED));
        //i.putExtra("com.jwerba.attendance.SSID", ssid);
        //sendBroadcast(i);
    }
}
