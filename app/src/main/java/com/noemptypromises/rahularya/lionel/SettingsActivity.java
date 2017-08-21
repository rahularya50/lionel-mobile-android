package com.noemptypromises.rahularya.lionel;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            //Log.d(TAG, "PROGRAM update settingSummary");

            String stringValue = value.toString();

            if (preference.getKey().equals("theme")) {
                //setTheme(R.style.Rowell);
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .edit()
                        .putBoolean("reloadMain", true)
                        .commit();
                recreate();
                //Log.d(TAG, "PROGRAM reloading theme");
                //return true;
            }

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            }
            else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent notifyIntent = new Intent(this, NotifyAlarm.class);
        PendingIntent notifyPending = PendingIntent.getBroadcast(this, 0, notifyIntent, 0);
        manager.cancel(notifyPending);
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("hw_notifications", true)) {
            long a = PreferenceManager.getDefaultSharedPreferences(this).getLong("time", 72000000L) % (1000 * 60 * 60 * 24)
                    + System.currentTimeMillis() - (System.currentTimeMillis() % (1000 * 60 * 60 * 24));
            if (a < System.currentTimeMillis()) {
                a += 1000 * 60 * 60 * 24;
            }
            Log.d(TAG, "PROGRAM starting notifier at time index " + a);
            manager.setRepeating(AlarmManager.RTC_WAKEUP, a, 1000 * 60 * 60 * 24, notifyPending);
        }
        super.onBackPressed();
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        //preference.setOnPreferenceChangeListener(sThemeChangeReloadListener);

        // Trigger the listener immediately with the preference's
        // current value.

        if (!preference.getKey().equals("theme")) {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }
        else
        {
            //Manually set themeprefs

            ListPreference themePreference = (ListPreference) findPreference("theme");

            // Set the summary to reflect the new value.
            themePreference.setSummary(PreferenceManager
                    .getDefaultSharedPreferences(this)
                    .getString("theme", ""));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getResources().getIdentifier(PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString("theme", ""), "style", "com.noemptypromises.rahularya.lionel"));

        setContentView(R.layout.activity_settings);

        addPreferencesFromResource(R.xml.pref_general);
        bindPreferenceSummaryToValue(findPreference("ringtone"));
        bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        bindPreferenceSummaryToValue(findPreference("theme"));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
