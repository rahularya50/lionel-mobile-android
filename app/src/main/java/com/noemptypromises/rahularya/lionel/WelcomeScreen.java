package com.noemptypromises.rahularya.lionel;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.content.Intent;

import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;

import org.jsoup.Connection;

import java.io.IOException;


public class WelcomeScreen extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.rahularya.lionel.MESSAGE";
    private static final String TAG = WelcomeScreen.class.getSimpleName();
    public static final String AUTO = "auto";

    private TextView textView;

    private Connection.Response doc;
    private Connection.Response loginForm;
    private Connection.Response l2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(getResources().getIdentifier(PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString("theme", ""), "style", "com.noemptypromises.rahularya.lionel"));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-5618482356089007~8022190378");

        textView = (TextView) findViewById(R.id.Test);

        Intent i = getIntent();

        SharedPreferences login = getSharedPreferences("usercreds", 0);

        PackageManager manager = this.getPackageManager();

        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);

            if (!login.getString("username", "fail").equals("fail") && !login.getString("timetable", "fail").equals("fail") && !login.getString("homework", "fail").equals("fail") && i.getBooleanExtra("redirect", true) && getSharedPreferences("PREFERENCE", 0).getInt("prevVersion", 0) > 10) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("auto", true);
                //Log.d(TAG, "PROGRAM startLogin");
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
            else {
                //Log.d(TAG, "PROGRAM fullFail");
                //Intent intent = new Intent(this, LoginActivity.class);
                //intent.putExtra("auto", false);
                //startActivity(intent);
                //overridePendingTransition(0, 0);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    public boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
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

    public void myClickHandler(View view) throws IOException {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("auto", false);
        startActivity(intent);
        //overridePendingTransition(R.anim.slide_in_up, R.anim.hold);
    }
}
