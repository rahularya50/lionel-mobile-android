package com.noemptypromises.rahularya.lionel;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

public class DeviceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent notifyIntent = new Intent(context, NotifyAlarm.class);
            PendingIntent notifyPending = PendingIntent.getBroadcast(context, 0, notifyIntent, 0);
            manager.cancel(notifyPending);
            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("hw_notifications", true))
            {
                long a = PreferenceManager.getDefaultSharedPreferences(context).getLong("time", 43200000L) % (1000 * 60 * 60 * 24)
                        + System.currentTimeMillis() - (System.currentTimeMillis() % (1000*60*60*24));
                if (a < System.currentTimeMillis())
                {
                    a += 1000*60*60*24;
                }
                manager.setRepeating(AlarmManager.RTC_WAKEUP, a, 1000 * 60 * 60 * 24, notifyPending);
            }
        }
    }
}