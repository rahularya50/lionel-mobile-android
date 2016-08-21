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

        final Elements links = timetable.select("tr");

        Pattern r = Pattern.compile("<br>[^<]*<br>");
        Matcher m = r.matcher(timetable.html());
        m.find();

        for (int position = 0; position < 10; position++) {
            final Elements dayElement = ((Element) links.toArray()[position + 1]).select("td");
            for (int i = 0; i < 5; i++) {
                String classCode = dayElement.toArray()[i].toString().substring(29, 36);

                //Log.d(TAG, "PROGRAM " + timetable.html());

                m.find();

                //String subjectx = regexer("<br>[^<]*<br>", timetable.html(), i + position * 6 + 2);
                String subjectx = timetable.html().substring(m.start(), m.end());
                subjectx = subjectx.substring(4, subjectx.length() - 4);
                codeMap.put(classCode, subjectx);
            }
            m.find();
        }

        Pattern r2 = Pattern.compile("<br>[^<]*<br>");
        Matcher m2 = r2.matcher(timetable.html());
        m2.find();

        Pattern r3 = Pattern.compile("<br>[^<]* <a");
        Matcher m3 = r3.matcher(timetable.html());
        m3.find();


        for (int position = 0; position < 10; position++) {
            final Elements dayElement = ((Element) links.toArray()[position + 1]).select("td");
            for (int i = 0; i < 5; i++) {
                String classCode = dayElement.toArray()[i].toString().substring(29, 36);

                String subjectx = regexer("<br>[^<]*<br>", timetable.html(), i + position * 6 + 2);
                //String subjectx = timetable.html().substring(m2.start(), m2.end());
                subjectx = subjectx.substring(4, subjectx.length() - 4);
                m2.find();

                //String teacherX = regexer("<br>[^<]* <a", timetable.html(), i + position * 5 + 1);
                String teacherX = timetable.html().substring(m3.start(), m3.end());
                teacherX = teacherX.substring(4, teacherX.length() - 3);
                m3.find();
                //Log.d(TAG, "PROGRAM " + teacherX + subjectx);

                teacherMap.put(teacherX, new String[]{subjectx, classCode});
            }
            m2.find();
            m2.find();
            m2.find();
            m2.find();
            m2.find();
            m2.find();
        }

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

        int mNotificationId = 001;
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotifyMgr.cancelAll();

        if (bodyLines.size() > 0) {
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
        }
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