package com.noemptypromises.rahularya.lionel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Homework extends AppCompatActivity {

    private static final String TAG = Timetable2.class.getSimpleName();

    private View mProgressView;

    private ParseHomework mAuthTask;

    private com.noemptypromises.rahularya.lionel.UserLoginTask task;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(getResources().getIdentifier(PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString("theme", ""), "style", "com.noemptypromises.rahularya.lionel"));
        setContentView(R.layout.activity_homework);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProgressView = findViewById(R.id.login_progress);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showProgress(true);

        mAuthTask = new ParseHomework();
        mAuthTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public class ParseHomework extends AsyncTask<Void, Void, Boolean> {

        ParseHomework() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            SharedPreferences login = getSharedPreferences("usercreds", 0);

            try {
                Answers.getInstance().logContentView(new ContentViewEvent()
                        .putContentType("Homework")
                        .putContentId(login.getString("username", "unknown")));
            }
            catch (Exception e) {}

            Document l3 = Jsoup.parse(login.getString("homework", "homework"));

            Elements elements = l3.select(".container-fluid").get(1).select("#stage > div > div > div");

            Document timetable = Jsoup.parse(login.getString("timetable", "timetable"));

            Map<String, String> codeMap = new HashMap<String, String>();
            Map<String, String[]> teacherMap = new HashMap<String, String[]>();

            final Elements links = timetable.select("tr");

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
                        //Log.d(TAG, "PROGRAM " + teacher + " " + subject + " " + classCode);
                    }
                    catch (Exception e)
                    {

                    }
                }
            }

            for (Element element : elements) {
                //Log.d(TAG, element.html());
                String code;
                String time;
                String dueDate;
                String issued;
                String body;
                String teacher;
                try {
                    code = element.select(".span3 > div > div").get(1).text();
                }
                catch (Exception e) {
                    code = "Unknown";
                }
                try {
                    time = element.select(".span3 > div > div").get(2).text();
                }
                catch (Exception e) {
                    time = "Unknown";
                }
                try {
                    dueDate = element.select(".span3 > div > div").get(3).text();
                }
                catch (Exception e) {
                    dueDate = "Unknown";
                }
                try {
                    issued = element.select(".span6 > div").get(1).select(" > p").get(1).text();
                }
                catch (Exception e) {
                    issued = "Unknown";
                }
                try {
                    body = element.select(".span6 > div").get(1).html();
                    Log.d(TAG, body);
                }
                catch (Exception e) {
                    body = "Unknown";
                }
                try {
                    teacher = element.select(".span6 > div").get(2).select(" > p").get(0).text();
                }
                catch (Exception e)
                {
                    teacher = "Unknown";
                }
                String subject = "Unknown";

                subject = codeMap.get(code);

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

                body = br2nl(body);
                body = body.replace("&nbsp;","");
                body = body.replaceAll("[\r\n ]+[\r\n]+[\r\n]*", "\n\n");
                body = body.replaceAll("[\r\n]+[\r\n]+[\r\n ]*", "\n\n");
                body = body.trim();

            /*for (Element e : x) {
                body = body + e.text() + "\n";
            }*/
                HW_Card card = new HW_Card();
                getSupportFragmentManager().beginTransaction().add(R.id.homework_content, card).commit();
                card.setInfo(subject, code, body, Character.toUpperCase(dueDate.charAt(0)) + dueDate.substring(1), teacher, time, false);
            }

            return true;
        }

        @Override
        public void onPostExecute(Boolean b)
        {
            showProgress(false);
        }
    }

    public static String br2nl(String html) {
        if(html==null)
            return html;
        Document document = Jsoup.parse(html);
        document.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
        document.select("br").append("\\n");
        document.select("p").prepend("\\n\\n");
        String s = document.html().replaceAll("\\\\n", "\n");
        //Log.d(TAG, "PROGRAM bodyOutput " + s);
        return Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
    }

    public String getTextFields(Element e, Integer depth)
    {
        String body = "";
        for (Element et : e.children()) {
            //Log.d(TAG, "Recursing");
            String a = getTextFields(et, depth + 1);
            body = body + "\n";
            body = body + a;
        }

        if (!body.equals(""))
        {
            body = body + "\n";
        }
        body = body + e.ownText();
        //Log.d(TAG, depth + "x  " + e.ownText());

        //Log.d(TAG, depth + "   " + body);
        return body;
    }

    public String regexer(String regex, String string, int pos)
    {
        Pattern r =  Pattern.compile(regex);
        Matcher m = r.matcher(string);
        for (int i = 0; i < pos; i++) {
            //Log.d(TAG, "PROGRAM " + Integer.valueOf(i).toString());
            Boolean a = m.find();
        }
        return string.substring(m.start(), m.end());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_homework, menu);
        //Log.d(TAG, "PROGRAM " + menu.getItem(R.id.action_refresh).getActionView());
        return true;
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
        if (mAuthTask != null)
        {
            mAuthTask.cancel(true);
        }
        super.onBackPressed();
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

        //Log.d(TAG, "PROGRAM start");
        task = new com.noemptypromises.rahularya.lionel.UserLoginTask(this, false, true, false, m);
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

    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            /*mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    //mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            }); */

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            //mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void openHomework(View view) {
        try {
            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://lionel2.kgv.edu.hk/local/mis/mobile/myhomework.php"));
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request."
                    + " Please install a web browser", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
