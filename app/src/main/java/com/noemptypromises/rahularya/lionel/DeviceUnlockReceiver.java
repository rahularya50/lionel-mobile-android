package com.noemptypromises.rahularya.lionel;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class DeviceUnlockReceiver extends BroadcastReceiver {
    private static final String TAG = DeviceUnlockReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context arg0, Intent intent) {

        /*Sent when the user is present after 
         * device wakes up (e.g when the keyguard is gone)
         * */
        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            //Log.d(TAG, "Device Unlocked");

            long interval = Long.valueOf(PreferenceManager.getDefaultSharedPreferences(arg0).getString("sync_frequency", "60"));

            long prevRefreshTime = Long.valueOf(PreferenceManager.getDefaultSharedPreferences(arg0).getString("last_refresh", "0"));

            //Log.d(TAG, String.valueOf(interval));
            //Log.d(TAG, String.valueOf(System.currentTimeMillis()));
            //Log.d(TAG, String.valueOf(prevRefreshTime));

            if (interval != 0 && System.currentTimeMillis() - prevRefreshTime > interval*60*1000) {

                //Log.d(TAG, "Reloading...");
                UserLoginTask task = new com.noemptypromises.rahularya.lionel.UserLoginTask(arg0, false, false, false, null);
                task.execute((Void) null);

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(arg0).edit();
                editor.putString("last_refresh", String.valueOf(System.currentTimeMillis()));
                editor.commit();
            }
        }
    }
}