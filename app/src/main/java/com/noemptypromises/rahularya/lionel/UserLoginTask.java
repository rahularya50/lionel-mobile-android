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

            Connection.Response loginForm = Jsoup.connect("https://lionel.kgv.edu.hk/login/index.php")
                    .method(Connection.Method.GET)
                    .timeout(0)
                    .execute();

            //Log.d(TAG, "PROGRAM loginForm " + loginForm.toString());

            Connection.Response l1 = Jsoup.connect("https://lionel.kgv.edu.hk/login/index.php")
                    .data("username", mEmail)
                    .data("password", mPassword)
                    .cookies(loginForm.cookies())
                    .method(Connection.Method.POST)
                    .timeout(0)
                    .execute();

            //Log.d(TAG, "PROGRAM l1 " + l1.toString());

            //Log.d(TAG, "PROGRAM doc length " + l1.body().length());

            if (l1.body().length() < 20000)
            {
                return false;
            }

            Connection.Response cal = Jsoup.connect("http://lionel.kgv.edu.hk/kgv-additions/Calendar/master.php?style=small")
                    .cookies(l1.cookies())
                    .cookies(loginForm.cookies())
                    .method(Connection.Method.GET)
                    .timeout(0)
                    .execute();

            mCal = cal.parse();

            Connection.Response l2 = Jsoup.connect("http://lionel.kgv.edu.hk/auth/mnet/jump.php?hostid=10")
                    .cookies(l1.cookies())
                    .cookies(loginForm.cookies())
                    .method(Connection.Method.GET)
                    .timeout(0)
                    .execute();

            //Log.d(TAG, "PROGRAM l2 " + l2.toString());

            int uidStartPos = l1.body().lastIndexOf("http://lionel.kgv.edu.hk/user/view.php?id=");

            String uid = l1.body().substring(uidStartPos + 42, uidStartPos + 46);

            //Log.d(TAG, "PROGRAM uid: " + uid);

            String b = l1.parse().select(".smallcal").text();

            //Log.d(TAG," PROGRAM" + b);

            //String uid = regexer("Logout<a>\\)<div>[^>]*<div>", l1.parse().html(), 1);

            //Log.d(TAG, "PROGRAM uid: " + uid);

            //Log.d(TAG, "PROGRAM doc length " + l2.body().length());

            Connection.Response l3 = Jsoup.connect("https://lionel2.kgv.edu.hk/local/mis/misc/printtimetable.php?sid=" + uid)
                    .cookies(l1.cookies())
                    .cookies(l2.cookies())
                    .method(Connection.Method.GET)
                    .timeout(0)
                    .execute();

            ml3 = l3.parse();

            Connection.Response l4 = Jsoup.connect("https://lionel2.kgv.edu.hk/local/mis/mobile/myhomework.php")
                    .cookies(l1.cookies())
                    .cookies(l2.cookies())
                    .method(Connection.Method.GET)
                    .timeout(0)
                    .execute();

            ml4 = l4.parse();

            Connection.Response l5 = Jsoup.connect("https://lionel2.kgv.edu.hk/local/mis/bulletin/bulletin.php")
                    .cookies(l1.cookies())
                    .cookies(l2.cookies())
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