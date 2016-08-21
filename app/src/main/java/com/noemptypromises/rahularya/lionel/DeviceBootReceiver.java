package com.noemptypromises.rahularya.lionel;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

public class DeviceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            int interval = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString("sync_frequency", "60"));
            Intent intervalIntent = new Intent(context, ReloadIntervalAlarm.class);
            PendingIntent intervalPending = PendingIntent.getBroadcast(context, 0, intervalIntent, 0);
            manager.cancel(intervalPending);
            if (interval != 0)
            {
                manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 200, (interval*60*1000), intervalPending);
            }
            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("smart_update", true))
            {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, 8);
                calendar.set(Calendar.MINUTE, 30);

                manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 60 * 24, intervalPending);

                calendar.set(Calendar.HOUR_OF_DAY, 4);
                calendar.set(Calendar.MINUTE, 15);

                manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 60 * 24, intervalPending);

                calendar.set(Calendar.HOUR_OF_DAY, 20);
                calendar.set(Calendar.MINUTE, 30);

                manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 60 * 24, intervalPending);
            }

            Intent notifyIntent = new Intent(context, NotifyAlarm.class);
            PendingIntent notifyPending = PendingIntent.getBroadcast(context, 0, notifyIntent, 0);
            manager.cancel(notifyPending);
            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("hw_notifications", true))
            {
                long a = PreferenceManager.getDefaultSharedPreferences(context).getLong("time", 1458216017528L) % (1000*60*60*24) + System.currentTimeMillis() - (System.currentTimeMillis() % (1000*60*60*24));
                if (a < System.currentTimeMillis())
                {
                    a += 1000*60*60*24;
                }
                manager.setRepeating(AlarmManager.RTC_WAKEUP, a, 1000 * 60 * 60 * 24, notifyPending);
            }
        }
    }
}