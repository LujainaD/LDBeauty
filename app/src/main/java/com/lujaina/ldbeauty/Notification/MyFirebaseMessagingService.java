package com.lujaina.ldbeauty.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.lujaina.ldbeauty.HomeActivity;
import com.lujaina.ldbeauty.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    private static final String TAG = "fcmExample";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
   /*     FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        Log.d(TAG, token);

                    }
                });*/

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        String title = "LD Beauty";
        String body= "a new Client booked an appointment";
       // notifyUser(remoteMessage.getFrom(), remoteMessage.getNotification().getBody());
        notifyUser(title,body);
    }

    /*   public void showNotification(String from, String notification, Intent intent){

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
    }*/

 public void notifyUser(String from, String notification){
     MyNotificationManager myNotificationManager = new MyNotificationManager(getApplicationContext());
     myNotificationManager.showNotification(from,notification,new Intent(getApplicationContext(),HomeActivity.class));
 }
}