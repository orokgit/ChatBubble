package com.javapapers.android.chat;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by orok on 25.11.2014.
 */
public class GcmIntentService extends IntentService {

    NotificationCompat.Builder notification;
    NotificationManager manager;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {  // has effect of unparcelling Bundle
            // Since we're not using two way messaging, this is all we really to check for
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Logger.getLogger("GCM_RECEIVED").log(Level.INFO, extras.toString());

//                showToast(extras.getString("message"));

                Intent msgrcv = new Intent(extras.getString("type"));
                msgrcv.putExtra("content", extras.getString("content"));
                msgrcv.putExtra("date", extras.getString("date"));

                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(msgrcv);
                sendNotification(extras.getString("type"), extras.getString("content"), extras.getString("date"));
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

//    protected void showToast(final String message) {
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//            }
//        });
//    }

    private void sendNotification(String type, String content, String date) {
        Bundle args = new Bundle();
        args.putString("type", type);
        args.putString("content", content);
        args.putString("date", date);
        Intent act = new Intent(this, MainActivity.class);
        act.putExtra("INFO", args);

        String notificationTitle = "Новое событие!";
        String notificationText = "В приложении Вместе произошло что то новое!";
        String notificationTicker = "Новое событие!";
        if (type.equals("vmeste-gcm-message"))
        {
            notificationTitle = "Новое сообщение";
            notificationTicker = "Новое сообщение!";
            notificationText = content;
            act = new Intent(this, ChatBubbleActivity.class);
//            act = new Intent(this, MainActivity.class);
        }
        if (type.equals("vmeste-gcm-invite")){
            notificationTitle = "Приглашение быть вместе";
            notificationTicker = "Приглашение быть вместе!";
            notificationText = content;
//            act = new Intent(this, InviteActivity.class);
            act = new Intent(this, MainActivity.class);
        }
        notification = new NotificationCompat.Builder(this);
        notification.setContentTitle(notificationTitle);
        notification.setContentText(notificationText);
        notification.setTicker(notificationTicker);
        notification.setSmallIcon(R.drawable.ic_launcher);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 1000, act, PendingIntent.FLAG_CANCEL_CURRENT);
        notification.setContentIntent(contentIntent);
        notification.setAutoCancel(true);
        manager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification.build());
    }
}
