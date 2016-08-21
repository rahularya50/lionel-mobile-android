package com.noemptypromises.rahularya.lionel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PlaceholderFragment extends Fragment {

    private View mLoginFormView;
    private View mProgressView;

    private static final String TAG = PlaceholderFragment.class.getSimpleName();

    private static final String ARG_SECTION_NUMBER = "section_number";

    View rootView;

    public int position;

    CheckTimetableLoad mCallback;

    public PlaceholderFragment() {
    }

    public interface CheckTimetableLoad {
        public Boolean checkLoad();
        public Document getTimetable();
    }

    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public void drawTimetable(Document timetable)
    {
        //mLoginFormView.setText(timetable.html());
        Elements rows = timetable.select("tr"); // a with href
        for (Element i:rows)
        {
            Log.d(TAG, "PROGRAM" + i.text());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_timetable2, container, false);
        View menu =
        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        mLoginFormView = (RelativeLayout) rootView.findViewById(R.id.wrapper);
        mProgressView = rootView.findViewById(R.id.login_progress);
        try {
            mCallback = (CheckTimetableLoad) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnHeadlineSelectedListener");
        }
        showProgress(mCallback.checkLoad());
        if (!mCallback.checkLoad()) {
            for (int i = 0; i<5; i++) {
                //Fragment fragment = getChildFragmentManager().findFragmentById(getResources().getIdentifier("article_fragment" + (Integer.valueOf(i + 1)).toString(), "id", "com.example.rahularya.lionel"));

                position = getArguments().getInt(ARG_SECTION_NUMBER);
                //Log.d(TAG, "PROGRAM " + position);

                Document timetable = mCallback.getTimetable();

                final Elements links = timetable.select("tr");
                final Elements day = ((Element) links.toArray()[position+1]).select("td");
                final Element subjectx = (Element) day.toArray()[i];

                try {
                    String classCode = day.toArray()[i].toString().substring(29, 36);

                    Log.d(TAG, "PROGRAM " + subjectx.html());

                    TextView period = (TextView) rootView.findViewById(getResources().getIdentifier("info_text" + (Integer.valueOf(i + 1)).toString() + "a", "id", "com.noemptypromises.rahularya.lionel"));
                    period.setText("Period " + (Integer.valueOf(i + 1)).toString());

                    String subject = regexer("<br>[^<]*<br>", subjectx.html(), 1);
                    subject = subject.substring(4, subject.length() - 4);
                    subject = subject.replace("&amp;", "&");

                    TextView subjectText = (TextView) rootView.findViewById(getResources().getIdentifier("info_text" + (Integer.valueOf(i + 1)).toString() + "b", "id", "com.noemptypromises.rahularya.lionel"));
                    subjectText.setText(subject);

                    View card = rootView.findViewById(getResources().getIdentifier("card_view" + (Integer.valueOf(i + 1)).toString(), "id", "com.noemptypromises.rahularya.lionel"));

                    card.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            try {
                                Intent intent = new Intent(getActivity(), TimetableExpand.class);
                                String name = v.getResources().getResourceName(v.getId());
                                name = name.substring(name.length() - 1);
                                int period = Integer.valueOf(name) - 1;

                                String classCode = day.toArray()[period].toString().substring(29, 36);

                                String subject = regexer("<br>[^<]*<br>", subjectx.html(), 1);
                                subject = subject.substring(4, subject.length() - 4);
                                subject = subject.replace("&amp;", "&");

                                String location = regexer("@[^<\"]*<br>", subjectx.html(), 1);
                                location = location.substring(1, location.length() - 4);

                                String email = regexer("href=\"[^\"]*\"", subjectx.html(), 1);
                                email = email.substring(13, email.length() - 1);

                                String teacher = regexer("<br>[^<]* <a", subjectx.html(), 1);
                                teacher = teacher.substring(4, teacher.length() - 3);

                                Log.d(TAG, "PROGRAM " + name);

                                //View mainText = rootView.findViewById(getResources().getIdentifier("info_text" + name + "b", "id", "com.example.rahularya.lionel"));
                                //2mainText.setVisibility(View.INVISIBLE);

                                intent.putExtra("period", "Period " + name);
                                intent.putExtra("location", location);
                                intent.putExtra("classCode", classCode);
                                intent.putExtra("teacher", teacher);
                                intent.putExtra("email", email);
                                intent.putExtra("subject", subject);

                                ActivityOptions options = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP && false) {
                                    options = ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                                            Pair.create(v, "card"),
                                            Pair.create(rootView.findViewById(getResources().getIdentifier("info_text" + name + "b", "id", "com.noemptypromises.rahularya.lionel")), "subject")
                                    );
                                    ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
                                } else {
                                    startActivity(intent);
                                }
                            } catch (Exception e) {
                            }
                            //                    }
                        }
                    });
                }
                catch (Exception e)
                {

                }

                //Log.d(TAG, "PROGRAM " + subject + " " + location + " " + email + " " + classCode);
            }
        }
        return rootView;
    }

    public String regexer(String regex, String string, int pos)
    {
        Pattern r =  Pattern.compile(regex);
        Matcher m = r.matcher(string);
        for (int i = 0; i < pos; i++) {
            //Log.d(TAG, "PROGRAM " + Integer.valueOf(i).toString());
            Boolean a = m.find();
            if (!a)
            {
                //Log.d(TAG, "PROGRAM FAIL" + Integer.valueOf(i).toString());
            }
        }
        return string.substring(m.start(), m.end());
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position % 10 + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 999;
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

    public void openCard(View view)
    {

    }
}