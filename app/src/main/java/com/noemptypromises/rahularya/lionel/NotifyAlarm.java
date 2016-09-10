package com.noemptypromises.rahularya.lionel;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotifyAlarm extends BroadcastReceiver {

    private static final String TAG = NotifyAlarm.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences check = context.getSharedPreferences("active", 0);

        if (!check.getBoolean("active", false))
        {
            return;
        }

        SharedPreferences login = context.getSharedPreferences("usercreds", 0);

        String username = login.getString("username", "");

        Document timetable = Jsoup.parse(login.getString("timetable", "timetable"));

        Document l3 = Jsoup.parse(login.getString("homework", "homework"));

        Elements elements = l3.select(".container-fluid").get(1).select("#stage > div > div > div");

        Map<String, String> codeMap = new HashMap<String, String>();
        Map<String, String[]> teacherMap = new HashMap<String, String[]>();

        String text = "blank";
        String bodyText = "";
        String body = "";
        List<String> bodyLines = new ArrayList<String>();
        List<String> subjectLines = new ArrayList<String>();

        for (Element element : elements) {
            String code;
            String teacher;
            String dueDate;
            try {
                code = element.select(".span3 > div > div").get(1).text();
            } catch (Exception e) {
                code = "Unknown";
            }
            try {
                teacher = element.select(".span6 > div").get(1).select(" > p").get(0).text();
            } catch (Exception e) {
                teacher = "Unknown";
            }
            try {
                body = element.select(".span6 > div").get(0).html();
                body = br2nl(body);
                body = body.replace("&nbsp;", "");
                body = body.replaceAll("[\r\n ]+[\r\n]+[\r\n]*", "\n\n");
                body = body.replaceAll("[\r\n]+[\r\n]+[\r\n ]*", "\n\n");
                body = body.trim();
            } catch (Exception e) {
                body = "Unknown";
            }
            try {
                dueDate = element.select(".span3 > div > div").get(3).text();
            } catch (Exception e) {
                dueDate = "Unknown";
            }

            if (dueDate.contains("tomorrow")) {
                String subject = codeMap.get(code);

                if (subject == null) {
                    try {
                        subject = teacherMap.get(teacher)[0];
                    } catch (Exception ignored) {
                    }
                }

                if (subject == null) {
                    if (code == null) {
                        subject = "Unknown";
                    } else {
                        subject = code;
                    }
                }

                if (text.equals("blank")) {
                    text = subject + " Homework";
                    bodyText = body;
                    bodyLines.add(body);
                    subjectLines.add(subject);
                } else {
                    bodyLines.add(body);
                    subjectLines.add(subject);
                }
            }
        }

        Uri soundUri = Uri.parse(PreferenceManager.getDefaultSharedPreferences(context).getString("ringtone", ""));

        NotificationCompat.InboxStyle nStyle = new NotificationCompat.InboxStyle();

        for (int i = 0; i < bodyLines.size(); i++) {
            nStyle.addLine(subjectLines.get(i) + "   " + bodyLines.get(i));
        }

        nStyle.setBigContentTitle(bodyLines.size() + " assignments due").setSummaryText(username + "@kgv.hk");

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setContentTitle("Upcoming assignments")
                        .setStyle(nStyle)
                        .setContentText(bodyLines.size() + " assignments due tomorrow.")
                        .setSound(soundUri);

        if (bodyLines.size() == 1) {
            mBuilder = new NotificationCompat.Builder(context)
                    .setContentTitle(text)
                    .setContentText(bodyText)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(bodyText).setSummaryText(username + "@kgv.hk"))
                    .setSound(soundUri);
        }

        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("vibrate", true)) {
            mBuilder.setVibrate(new long[]{1000, 1000});
        }

        mBuilder.setSmallIcon(R.drawable.ic_notifier);
        mBuilder.setColor(context.getResources().getColor(R.color.colorPrimaryDark));

        Intent resultIntent = new Intent(context, Homework.class);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        mBuilder.setAutoCancel(true);

        int mNotificationId = 001;
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotifyMgr.cancelAll();

        if (bodyLines.size() > 0) {
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
        }

        Log.d(TAG, "Notification check complete");
    }

    public String regexer(String regex, String string, int pos) {
        Pattern r = Pattern.compile(regex);
        Matcher m = r.matcher(string);
        for (int i = 0; i < pos; i++) {
            m.find();
        }
        return string.substring(m.start(), m.end());
    }

    public static String br2nl(String html) {
        if (html == null)
            return html;
        Document document = Jsoup.parse(html);
        document.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
        document.select("br").append("\\n");
        document.select("p").prepend("\\n\\n");
        String s = document.html().replaceAll("\\\\n", "\n");
        return Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
    }
}