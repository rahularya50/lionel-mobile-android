package com.noemptypromises.rahularya.lionel;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class TimetableExpand extends AppCompatActivity {

    private static final String TAG = PlaceholderFragment.class.getSimpleName();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(getResources().getIdentifier(PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString("theme", ""), "style", "com.noemptypromises.rahularya.lionel"));

        super.onCreate(savedInstanceState);

        //setContentView(R.layout.app_bar_main);

        setContentView(R.layout.activity_timetable_expand);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);
        listView.setFocusable(false);

        // Defined Array values to show in ListView
        String[] values = new String[]{"Location",
                "Class Code",
                "Teacher",
                "Email",
        };

        ArrayList<User> arrayOfUsers = new ArrayList<User>();
        UsersAdapter adapter = new UsersAdapter(this, arrayOfUsers);

        Intent intent = getIntent();

        ((TextView) findViewById(R.id.subject)).setText(intent.getStringExtra("subject"));
        //((TextView) findViewById(R.id.realsubject)).setText(intent.getStringExtra("subject"));


        adapter.add(new User("Period", intent.getStringExtra("period")));
        adapter.add(new User("Location", intent.getStringExtra("location")));
        adapter.add(new User("Class Code", intent.getStringExtra("classCode")));
        adapter.add(new User("Teacher", intent.getStringExtra("teacher")));
        adapter.add(new User("Email", intent.getStringExtra("email")));

        listView.setAdapter(adapter);

        Animation a = AnimationUtils.loadAnimation(this, R.anim.scale);

        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //findViewById(R.id.subject).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.subject).setAlpha(0);
                findViewById(R.id.subject2).setVisibility(View.VISIBLE);
                //((ScrollView) findViewById(R.id.scroll)).fullScroll(ScrollView.FOCUS_UP);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });


        //findViewById(R.id.subject).setVisibility(View.INVISIBLE);
        //findViewById(R.id.realsubject).setVisibility(View.VISIBLE);
        //((ScrollView) findViewById(R.id.scroll)).fullScroll(ScrollView.FOCUS_UP);

        TextView tv = (TextView) findViewById(R.id.subject);
        //tv.startAnimation(a);

        SharedPreferences login = getSharedPreferences("usercreds", 0);

        Document l3 = Jsoup.parse(login.getString("homework", "homework"));

        Elements elements = l3.select(".container-fluid").get(1).select("#stage > div > div > div");

        for (Element element : elements) {
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
                body = element.select(".span6 > div").get(0).html();
            }
            catch (Exception e) {
                body = "Unknown";
            }
            try {
                teacher = element.select(".span6 > div").get(1).select(" > p").get(0).text();
            }
            catch (Exception e)
            {
                teacher = "Unknown";
            }

            body = br2nl(body);
            body = body.replace("&nbsp;","");
            body = body.replaceAll("[\r\n ]+[\r\n]+[\r\n]*", "\n\n");
            body = body.replaceAll("[\r\n]+[\r\n]+[\r\n ]*", "\n\n");
            body = body.trim();
            HW_Card card = new HW_Card();

            if (code.equals(intent.getStringExtra("classCode")))
            {
                getSupportFragmentManager().beginTransaction().add(R.id.main, card).commit();
                card.setInfo(intent.getStringExtra("subject"), code, body, Character.toUpperCase(dueDate.charAt(0)) + dueDate.substring(1), teacher, time, true);
            }
        }
        Utility.setListViewHeightBasedOnChildren(listView);
    }

    public static String br2nl(String html) {
        if(html==null)
            return html;
        Document document = Jsoup.parse(html);
        document.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
        document.select("br").append("\\n");
        document.select("p").prepend("\\n\\n");
        String s = document.html().replaceAll("\\\\n", "\n");
        return Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
    }

    public class User {
        public String name;
        public String value;

        public User(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return(super.onOptionsItemSelected(item));
    }

    public void onBackPressed() {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new LinearInterpolator()); //and this
        fadeOut.setStartOffset(120);
        fadeOut.setDuration(120);

        AnimationSet animation = new AnimationSet(false); //change to false
        animation.addAnimation(fadeOut);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.list).setAlpha(0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        Animation fadeOut2 = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new LinearInterpolator()); //and this
        fadeOut.setStartOffset(300);
        fadeOut.setDuration(300);

        AnimationSet animation2 = new AnimationSet(false); //change to false
        animation.addAnimation(fadeOut);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.subject).setAlpha(0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        //findViewById(R.id.list).startAnimation(animation);

        Animation a = AnimationUtils.loadAnimation(this, R.anim.descale);

        animation2.addAnimation(fadeOut2);
        animation2.addAnimation(a);

        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //findViewById(R.id.realsubject).setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.subject).setScaleX(1);
                findViewById(R.id.subject).setScaleY(1);
                //findViewById(R.id.realsubject).setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        TextView tv = (TextView) findViewById(R.id.subject);
        //tv.startAnimation(animation2);

        super.onBackPressed();
    }

    public class UsersAdapter extends ArrayAdapter<User> {
        public UsersAdapter(Context context, ArrayList<User> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            User user = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.timetablelistitem, parent, false);
            }
            // Lookup view for data population
            TextView tvName = (TextView) convertView.findViewById(R.id.name);
            TextView tvHome = (TextView) convertView.findViewById(R.id.value);
            // Populate the data into the template view using the data object
            tvName.setText(user.name);
            tvHome.setText(user.value);
            // Return the completed view to render on screen
            return convertView;
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