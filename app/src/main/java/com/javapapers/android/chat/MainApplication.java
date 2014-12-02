package com.javapapers.android.chat;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by orok on 16.10.2014.
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        UsersData.getInstance().setContext(this);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("vmeste-gcm-message"));
    }

    // Our handler for received Intents. This will be called whenever an Intent
// with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("content");
            Log.d("receiver", "Got message: " + message);
        }
    };

    @Override
    public void onTerminate() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onTerminate();
    }
}
