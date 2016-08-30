package com.noemptypromises.rahularya.lionel;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(getResources().getIdentifier(PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString("theme", ""), "style", "com.noemptypromises.rahularya.lionel"));
        Log.d(TAG, "PROGRAM inMain");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AnalyticsApplication application = (com.noemptypromises.rahularya.lionel.AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        checkFirstRun();

        SecurePW.context = this;

        String x = SecurePW.encrypt("test");
        Log.d(TAG, x);
        Log.d(TAG, SecurePW.decrypt(x));
    }

    public void checkFirstRun() {
        PackageManager manager = this.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            int isFirstRun = getSharedPreferences("PREFERENCE", 0).getInt("prevVersion", 0);
            if (isFirstRun != info.versionCode){
                // Place your dialog code here to display the dialog

                new AlertDialog.Builder(this).setTitle("Changelog").setMessage("Themes have been added! Go to settings to change the app colors. Send an email to 16luong1@kgv.hk to suggest more themes!").setNeutralButton("OK", null).show();

                getSharedPreferences("PREFERENCE", 0)
                        .edit()
                        .putInt("prevVersion", info.versionCode)
                        .apply();
            }
        }

        catch (PackageManager.NameNotFoundException e) {
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (PreferenceManager
                .getDefaultSharedPreferences(this)
                .getBoolean("reloadMain", false))
        {
            PreferenceManager
                    .getDefaultSharedPreferences(this)
                    .edit()
                    .putBoolean("reloadMain", false)
                    .commit();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
        UserLoginTask mAuthTask = new UserLoginTask();
        mAuthTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public class UserLoginTask extends AsyncTask<Void, Void, String[]> {

            UserLoginTask() {
            }

            @Override
            protected String[] doInBackground(Void... params) {
                SharedPreferences login = getSharedPreferences("usercreds", 0);
                if (login.getString("cal", "test").equals("test"))
                {
                    return new String[] {"Unknown", "Unknown"};
                }

                Document timetable = Jsoup.parse(login.getString("timetable", "timetable"));

                int period;

                Calendar calendar = Calendar.getInstance();

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int min = calendar.get(Calendar.MINUTE);

                if (hour < 8 || (hour < 9 && min < 30))
                {
                    period = 1;
                }
                else if (hour == 8 || (hour == 9 && min < 45))
                {
                    period = 2;
                }
                else if (hour == 9 || hour == 10 || (hour == 11 && min < 30))
                {
                    period = 3;
                }
                else if (hour == 11 || (hour == 12 && min < 40))
                {
                    period = 4;
                }
                else if (hour == 12 || hour == 13 || hour == 14)
                {
                    period = 5;
                }
                else
                {
                    period = 1; //New day!
                }

                Document l1 = Jsoup.parse(login.getString("cal", "test"));
                String a = l1.select(".smallcal > div").get(0).html();

                int currentDay = 0;
                int currentWeek = Integer.parseInt(a.substring(a.length() - 1));
                int day = calendar.get(Calendar.DAY_OF_WEEK);

                if (day == 1)
                {
                    currentDay = currentWeek * 5 - 5;
                    period = 1;
                }
                if (day >= 2 && day <= 5)
                {
                    if (hour >= 15)
                    {
                        currentDay = currentWeek * 5 - 5 + day - 1;
                    }
                    else
                    {
                        currentDay = currentWeek * 5 - 5 + day - 2;
                    }
                }
                if (day == 6)
                {
                    if (hour >= 15)
                    {
                        currentDay = currentWeek * 5;
                    }
                    else
                    {
                        currentDay = currentWeek * 5 - 5 + day - 2;
                    }
                }
                if (day == 7)
                {
                    currentDay = currentWeek * 5 - 5;
                    period = 1;
                }

                Log.d(TAG, "PROGRAM currentDay " + currentDay + " " + (period + currentDay * 6 + 1));

                currentDay = currentDay % 10;

                final Elements links = timetable.select("tr");
                final Elements dayE = ((Element) links.toArray()[currentDay + 1]).select("td");
                final Element subjecty = (Element) dayE.toArray()[period - 1];

                String subjectF;

                try {
                    subjectF = regexer("<br>[^<]*<br>", subjecty.html(), 1);
                    subjectF = subjectF.substring(4, subjectF.length() - 4);
                    subjectF = subjectF.replace("&amp;", "&");
                }
                catch (Exception e)
                {
                    subjectF = "Free Period";
                }
                Document l3 = Jsoup.parse(login.getString("homework", "homework"));

                Elements elements = l3.select(".container-fluid").get(1).select("#stage > div > div > div");

                Map<String, String> codeMap = new HashMap<String, String>();
                Map<String, String[]> teacherMap = new HashMap<String, String[]>();

                for (int position = 0; position < 10; position++) {
                    for (int i = 0; i < 5; i++) {
                        try {
                            Elements dayE2 = ((Element) links.toArray()[position + 1]).select("td");
                            final Element subjectx = (Element) dayE2.toArray()[i];

                            String classCode = dayE2.toArray()[i].toString().substring(29, 36);

                            String subject = regexer("<br>[^<]*<br>", subjectx.html(), 1);
                            subject = subject.substring(4, subject.length() - 4);
                            subject = subject.replace("&amp;", "&");

                            String teacher = regexer("<br>[^<]* <a", subjectx.html(), 1);
                            teacher = teacher.substring(4, teacher.length() - 3);

                            codeMap.put(classCode, subject);
                            teacherMap.put(teacher, new String[]{subject, classCode});
                            Log.d(TAG, "PROGRAM " + teacher + " " + subject + " " + classCode);
                        }
                        catch (Exception e)
                        {

                        }
                        }
                }

                String text = "";

                for (Element element : elements) {
                    String code;
                    String teacher;
                    try {
                        code = element.select(".span3 > div > div").get(1).text();
                    }
                    catch (Exception e) {
                        code = "Unknown";
                    }
                    try {
                        teacher = element.select(".span6 > div").get(1).select(" > p").get(0).text();
                    }
                    catch (Exception e)
                    {
                        teacher = "Unknown";
                    }

                    String subject = codeMap.get(code);

                    if (subject == null)
                    {
                        try {
                            subject = teacherMap.get(teacher)[0];
                        }
                        catch (Exception ignored)
                        {
                        }
                    }

                    if (subject == null) {
                        if (code == null) {
                            subject = "Unknown";
                        }
                        else
                        {
                            subject = code;
                        }
                    }
                    text = text + subject + ", ";
                }

                if (text.length() != 0)
                {
                    text = text.substring(0,text.length()-2);
                }
                else
                {
                    text = "No homework";
                }

                return new String[] {subjectF, text};
            }

            @Override
            protected void onPostExecute(final String[] x) {
                ((TextView) findViewById(R.id.nextPeriod)).setText("Next: " + x[0] + ".");
                ((TextView) findViewById(R.id.nextHW)).setText(x[1] + ".");
            }
    }

    public String regexer(String regex, String string, int pos)
    {
        Pattern r =  Pattern.compile(regex);
        Matcher m = r.matcher(string);
        for (int i = 0; i < pos; i++) {
            m.find();
        }
        return string.substring(m.start(), m.end());
    }

    public void timetableClick(View view) throws IOException {
        Intent intent = new Intent(this, Timetable2.class);
        mTracker.setScreenName("Timetable");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        startActivity(intent);
    }

    public void homeworkClick(View view) throws IOException {
        Intent intent = new Intent(this, Homework.class);
        mTracker.setScreenName("Homework");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        startActivity(intent);
    }

    public void bulletinClick(View view) throws IOException {
        Intent intent = new Intent(this, Bulletin.class);
        mTracker.setScreenName("Bulletin");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        startActivity(intent);
    }

    public void settingsClick(View view) throws IOException {
        Intent intent = new Intent(this, SettingsActivity.class);
        mTracker.setScreenName("Settings");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        startActivity(intent);
    }

    public void logoutClick(View view) throws IOException {
        DialogFragment newFragment = new SignOutDialogFragment();
        mTracker.setScreenName("Logout");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        newFragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}