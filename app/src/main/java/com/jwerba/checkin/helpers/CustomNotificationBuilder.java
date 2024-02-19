package com.jwerba.checkin.helpers;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.jwerba.checkin.R;


public class CustomNotificationBuilder {
    private Context context;
    public CustomNotificationBuilder(Context context){
        this.context = context;
    }
    public void notify(Class<?> intentClass, String bigText, String bigContentTitle, String summaryText, String contentTitle, String contentText){
        NotificationManager mNotificationManager;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.context, "notify_001");
        Intent ii = new Intent(this.context, intentClass);
        PendingIntent pendingIntent = PendingIntent.getActivity(this.context, 0, ii, 0 | FLAG_IMMUTABLE);

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
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(channelId,"Channel human readable title", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Endless Service channel");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }
        mNotificationManager.notify(0, mBuilder.build());
    }
}
