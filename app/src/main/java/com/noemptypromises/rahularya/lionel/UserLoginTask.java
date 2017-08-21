package com.noemptypromises.rahularya.lionel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MenuItem;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

    private final String mEmail;
    private final String mPassword;
    private Document ml3;
    private Document ml4;
    private Document ml5;
    private Document mCal;
    private Context activity;
    private Boolean timetable;
    private Boolean hw;
    private Boolean bulletin;

    private MenuItem rootView;

    UserLoginTask(Context a, Boolean tTable, Boolean hLog, Boolean btin, MenuItem rootV) {
        activity = a;
        timetable = tTable;
        bulletin=btin;
        rootView = rootV;
        hw = hLog;

        SharedPreferences login = activity.getSharedPreferences("usercreds", 0);
        mEmail = login.getString("username", "username");

        SecurePW.context = a;

        mPassword = SecurePW.decrypt(login.getString("password", "password"));
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        String TAG = WelcomeScreen.class.getSimpleName();

        try {
            Connection.Response loginForm = Jsoup.connect("https://lionel2.kgv.edu.hk/login/index.php")
                    .method(Connection.Method.GET)
                    .timeout(0)
                    .execute();

            Connection.Response l1 = Jsoup.connect("https://lionel2.kgv.edu.hk/login/index.php")
                    .data("username", mEmail)
                    .data("password", mPassword)
                    .data("rememberusername", "0")
                    .cookies(loginForm.cookies())
                    .method(Connection.Method.POST)
                    .timeout(0)
                    .execute();

            Log.d(TAG, "PROGRAM l1 " + l1.toString());

            Log.d(TAG, "PROGRAM loginForm length " + loginForm.body().length());

            Log.d(TAG, "PROGRAM doc length " + l1.body().length());

            if (l1.body().lastIndexOf("Log out") == -1)
            {
                Log.d(TAG, "PROGRAM fail due to length");
                return false;
            }

            mCal = l1.parse();

            Log.d(TAG, "PROGRAM id " + l1.body().lastIndexOf("http://lionel.kgv.edu.hk/user/view.php?id="));

            int uidStartPos = l1.body().lastIndexOf("<a alt=\"summary\" class=\" \" href=\"https://lionel2.kgv.edu.hk/local/mis/students/summary.php?sid=");

            String uid = l1.body().substring(uidStartPos + 95, uidStartPos + 99);

            //String a = l1.parse().select(".menu").get(0).ownText();

            //String uid = a.replaceAll("[^0-9]", "");

            //String uid = a.substring(a.length() - 20, a.length() - 16);

            //String uid = regexer("Logout<a>\\)<div>[^>]*<div>", l1.parse().html(), 1);

            Log.d(TAG, "PROGRAM uid: " + uid);

            //Log.d(TAG, "PROGRAM doc length " + l2.body().length());

            Connection.Response l3 = Jsoup.connect("https://lionel2.kgv.edu.hk/local/mis/misc/printtimetable.php?sid=" + uid)
                    .cookies(l1.cookies())
                    .method(Connection.Method.GET)
                    .timeout(0)
                    .execute();

            ml3 = l3.parse();

            Connection.Response l4 = Jsoup.connect("https://lionel2.kgv.edu.hk/local/mis/mobile/myhomework.php")
                    .cookies(l1.cookies())
                    .method(Connection.Method.GET)
                    .timeout(0)
                    .execute();

            ml4 = l4.parse();

            Connection.Response l5 = Jsoup.connect("https://lionel2.kgv.edu.hk/local/mis/bulletin/bulletin.php")
                    .cookies(l1.cookies())
                    .method(Connection.Method.GET)
                    .timeout(0)
                    .execute();

            ml5 = l5.parse();
        }
        catch (IOException e) {
            return false;
        }

        if (mPassword.equals("ERROR"))
        {
            return false;
        }

        //Log.d(TAG, "Reload successful!");

        SharedPreferences settings = activity.getSharedPreferences("usercreds", 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("timetable", ml3.html());
        editor.putString("homework", ml4.html());
        editor.putString("bulletin", ml5.html());
        editor.putString("cal", mCal.html());
        editor.commit();



        return true;
    }

    protected void stopOpen () {
        timetable = false;
        hw = false;
        bulletin = false;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        //showProgress(false);
        if (timetable)
        {
            Intent intent = new Intent(activity, Timetable2.class);
            activity.startActivity(intent);
            ((Activity) activity).finish();
        }
        if (hw)
        {
            Intent intent = new Intent(activity, Homework.class);
            activity.startActivity(intent);
            ((Activity) activity).finish();
        }
        if (bulletin)
        {
            Intent intent = new Intent(activity, Bulletin.class);
            activity.startActivity(intent);
            ((Activity) activity).finish();
        }
        return;
    }

    @Override
    protected void onCancelled() {
        return;
    }
}