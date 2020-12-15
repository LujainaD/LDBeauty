package com.lujaina.ldbeauty.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;

import com.lujaina.ldbeauty.R;

public class MyNotificationManager {

    private static final int NOTIFICATION_ID = 234;
    private final Context mContext;


    public  MyNotificationManager(Context mContext){
        this.mContext = mContext;
    }

    public void showNotification(String from, String notification, Intent intent){

        PendingIntent pendingIntent = PendingIntent.getActivities(mContext,NOTIFICATION_ID, new Intent[]{intent}, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        Notification mNotification = builder.setSmallIcon(R.drawable.logpict)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setContentTitle(from)
                .setContentText(notification)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.logo))
                .build();

        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID,mNotification);
    }
}
