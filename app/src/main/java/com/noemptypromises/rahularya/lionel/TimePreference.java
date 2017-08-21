package com.noemptypromises.rahularya.lionel;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimePreference extends DialogPreference implements Preference.OnPreferenceChangeListener {
    private TimePicker picker = null;
    public final static long DEFAULT_VALUE = 72000000L;

    private static final String TAG = TimePreference.class.getSimpleName();

    public TimePreference(final Context context, final AttributeSet attrs) {
        super(context, attrs, 0);

        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");

        setDialogTitle(null);

        this.setOnPreferenceChangeListener(this);
    }

    protected void setTime(final long time) {
        persistLong(time);
        notifyDependencyChange(shouldDisableDependents());
        notifyChanged();
    }

    protected void updateSummary(final long time) {
        final DateFormat dateFormat = android.text.format.DateFormat.getTimeFormat(getContext());
        final Date date = new Date(time);
        setSummary(dateFormat.format(date.getTime()));
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());
        picker.setIs24HourView(android.text.format.DateFormat.is24HourFormat(getContext()));
        return picker;
    }

    protected Calendar getPersistedTime() {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(getPersistedLong(DEFAULT_VALUE));

        return c;
    }

    @Override
    protected void onBindDialogView(final View v) {
        super.onBindDialogView(v);
        final Calendar c = getPersistedTime();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            picker.setHour(c.get(Calendar.HOUR_OF_DAY));
            picker.setMinute(c.get(Calendar.MINUTE));
        }
    }

    @Override
    protected void onDialogClosed(final boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            final Calendar c = Calendar.getInstance();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                c.set(Calendar.MINUTE, picker.getMinute());
                c.set(Calendar.HOUR_OF_DAY, picker.getHour());
            }


            if (!callChangeListener(c.getTimeInMillis())) {
                return;
            }

            //Log.d(TAG, "PROGRAM " + c.getTimeInMillis());

            setTime(c.getTimeInMillis());
        }
    }

    @Override
    protected Object onGetDefaultValue(final TypedArray a, final int index) {
        return a.getInteger(index, 0);
    }

    @Override
    protected void onSetInitialValue(final boolean restorePersistedValue, final Object defaultValue) {
        long time;
        if (defaultValue == null) {
            time = restorePersistedValue ? getPersistedLong(DEFAULT_VALUE) : DEFAULT_VALUE;
        } else if (defaultValue instanceof Long) {
            time = restorePersistedValue ? getPersistedLong((Long) defaultValue) : (Long) defaultValue;
        } else if (defaultValue instanceof Calendar) {
            time = restorePersistedValue ? getPersistedLong(((Calendar)defaultValue).getTimeInMillis()) : ((Calendar)defaultValue).getTimeInMillis();
        } else {
            time = restorePersistedValue ? getPersistedLong(DEFAULT_VALUE) : DEFAULT_VALUE;
        }

        setTime(time);
        updateSummary(time);
    }

    @Override
    public boolean onPreferenceChange(final Preference preference, final Object newValue) {
        ((TimePreference) preference).updateSummary((Long)newValue);
        return true;
    }
}