package com.jwerba.checkin.helpers;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.jwerba.checkin.R;
import com.jwerba.checkin.activities.MainActivity;

import java.util.Random;


public class CustomNotificationBuilder {
    private Context context;
    public CustomNotificationBuilder(Context context){
        this.context = context;
    }
    private static int notificationCounter = 0;
    public void notify(Class<?> intentClass, String bigText, String bigContentTitle, String summaryText, String contentTitle, String contentText){
        NotificationManager mNotificationManager;
        String channelId = "CHECKIN_DETECTED_CHANNEL_001";

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.context.getApplicationContext(), channelId);
        Intent ii = new Intent(this.context, intentClass);
        PendingIntent pendingIntent = PendingIntent.getActivity(this.context.getApplicationContext(), 0, ii, 0 | FLAG_IMMUTABLE);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(bigText);
        bigTextStyle.setBigContentTitle(bigContentTitle);
        bigTextStyle.setSummaryText(summaryText);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(contentTitle);
        mBuilder.setContentText(contentText);
        mBuilder.setPriority(Notification.PRIORITY_MAX);

        mBuilder.setStyle(bigTextStyle);
        mNotificationManager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(channelId,"Channel human readable title", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Endless Service channel");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }
        notificationCounter++;
        mNotificationManager.notify(notificationCounter, mBuilder.build());


        //************************
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_stat_reminder)
                .setContentTitle("My notification")
                .setContentText("Much longer text that cannot fit one line...")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mBuilder.setChannelId(channelId);
        notificationCounter++;
        mNotificationManager.notify(notificationCounter, builder.build());

        //************************

    }

    public void noty(){
        float force = 0.8F;
        float speed = 0.5F;
        int notificationId = new Random().nextInt();
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this.context.getApplicationContext(), notificationId, intent, 0 | FLAG_IMMUTABLE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(Color.CYAN)
                .setContentTitle("Shake Detector")
                .setContentText(" Shake Force: " + force + " speed= " + speed)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.here_icon_2)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(Notification.PRIORITY_MAX)
                .setWhen(0)
                .setOngoing(false);


        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(notificationManager, notificationBuilder);
            notificationBuilder.setChannelId(CHANNEL_ID);
        }
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    public static String CHANNEL_ID = "shake_detector_3";
    public static String name = "Shake Detector";
    public static String description = "Notifications for Shake detector";
    @TargetApi(26)
    public static void createChannel(NotificationManager notificationManager, NotificationCompat.Builder builder) {
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        mChannel.setDescription(description);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        notificationManager.createNotificationChannel(mChannel);
    }
}
