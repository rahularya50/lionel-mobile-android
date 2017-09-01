package com.noemptypromises.rahularya.lionel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReloadIntervalAlarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // For our recurring task, we'll just display a message
        //Toast.makeText(context, "Syncing...", Toast.LENGTH_SHORT).show();

        //Log.d("ReloadIntervalAlarm", "auto-syncing");

        UserLoginTask task = new com.noemptypromises.rahularya.lionel.UserLoginTask(context, false, false, false, null, null);
        task.execute((Void) null);
    }
}