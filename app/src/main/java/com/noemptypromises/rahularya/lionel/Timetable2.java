package com.noemptypromises.rahularya.lionel;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Calendar;

public class Timetable2 extends AppCompatActivity implements PlaceholderFragment.CheckTimetableLoad {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    private static final String TAG = Timetable2.class.getSimpleName();

    int PAGE_COUNT = 2;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private UserLoginTask getTimetable;

    private Document l1;
    private Document timetable;

    public Menu appMenu;

    public Boolean ready = false;

    public UserLoginTask task;

    public int currentDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getResources().getIdentifier(PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString("theme", ""), "style", "com.noemptypromises.rahularya.lionel"));
        setContentView(R.layout.activity_timetable2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setCurrentItem(50);

        SharedPreferences login = getSharedPreferences("usercreds", 0);
        timetable = Jsoup.parse(login.getString("timetable", "timetable"));
        l1 = Jsoup.parse(login.getString("cal", "test"));
        ready = true;

        try {
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentType("Timetable")
                    .putContentId(login.getString("username", "unknown")));
        }
        catch (Exception e) {}
        //getTimetable = new ParseHomework(login.getString("username", "username"), login.getString("password", "password"));
        //getTimetable.execute((Void) null);

        //TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        //tabLayout.setupWithViewPager(mViewPager)

        String a = l1.select(".greeting > div").get(0).html();

        boolean isNext = (a.charAt(0) != 'T');
        int currentWeek = Character.getNumericValue(a.charAt(a.indexOf("Week ") + 5)) - 1;

        Log.d(TAG, a);
        Log.d(TAG, String.valueOf(currentWeek));
        Log.d(TAG, String.valueOf(a.indexOf("Week ")));
        Log.d(TAG, a.substring(a.indexOf("Week ") + 5));

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (day == Calendar.SATURDAY || day == Calendar.SUNDAY)
        {
            currentDay = currentWeek * 5;
            if (!isNext)
            {
                currentDay += 5;
            }
        }
        if (day >= Calendar.MONDAY && day <= Calendar.THURSDAY)
        {
            if (hour >= 15)
            {
                currentDay = currentWeek * 5 + day - 1;
            }
            else
            {
                currentDay = currentWeek * 5 + day - 2;
            }
        }
        if (day == Calendar.FRIDAY)
        {
            if (hour >= 15)
            {
                currentDay = (currentWeek) * 5;
                if (!isNext)
                {
                    currentDay += 5;
                }
            }
            else
            {
                currentDay = currentWeek * 5 + day - 2;
                if (isNext)
                {
                    currentDay += 5;
                }
            }
        }

        mViewPager.setCurrentItem(50 + currentDay);
    }

    public Boolean checkLoad() {
        return !ready;
    }

    public Document getTimetable() {
        return timetable;
    }

    public void goto_cday(MenuItem m)
    {
        mViewPager.setCurrentItem(50 + currentDay);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timetable2, menu);
        appMenu = menu;
        //Log.d(TAG, "PROGRAM " + menu.getItem(R.id.action_refresh).getActionView());
        return true;
    }

    public void reload(MenuItem m)
    {
        //Log.d(TAG, "PROGRAM spin!");
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView iv = (ImageView)inflater.inflate(R.layout.button_reload, null);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        rotation.setRepeatCount(Animation.INFINITE);
        iv.startAnimation(rotation);
        m.setActionView(iv);

        SharedPreferences login = getSharedPreferences("usercreds", 0);
        //Log.d(TAG, "PROGRAM start");
        task = new UserLoginTask(this, true, false, false, m);
        task.execute((Void) null);
        //Log.d(TAG, "PROGRAM enter");
    }

    public void reload(View view) {
        //Log.d(TAG, "PROGRAM spin?");
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        if (view.getAnimation() == null)
        {
            view.startAnimation(rotation);
        }
        else {
            view.clearAnimation();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        if (task != null)
        {
            task.stopOpen();
        }
        super.onBackPressed();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position % 10);
        }

        @Override
        public int getCount() {
            // Show 3 total pages. Yeah right.
            return 100;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String day = "";
            String week;
            switch (position % 5) {
                case 0:
                    day = "Monday";
                    break;
                case 1:
                    day = "Tuesday";
                    break;
                case 2:
                    day = "Wednesday";
                    break;
                case 3:
                    day = "Thursday";
                    break;
                case 4:
                    day = "Friday";
                    break;
            }

            if ((position % 10) >= 5) {
                week = "2";
            }
            else
            {
                week = "1";
            }

            return day + " (Week " + week + ")";
        }
    }
}
