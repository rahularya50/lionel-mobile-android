package com.noemptypromises.rahularya.lionel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link BulletinCard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BulletinCard extends Fragment {

    private String body;
    private String title;
    private String name;
    private String dates;
    private String preview;

    private Boolean isOpen;

    private static final String TAG = PlaceholderFragment.class.getSimpleName();

    private float height;

    public View rootView;

    public BulletinCard() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static BulletinCard newInstance() {
        BulletinCard fragment = new BulletinCard();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setInfo(String titlex, String namex, String bodyx, String datesx, String previewx, Boolean open)
    {
        title = titlex;
        name = namex;
        body = bodyx;
        dates = datesx;
        preview = previewx;
        isOpen = open;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View a = inflater.inflate(R.layout.fragment_bulletin_card, container, false);

        TextView datesText = (TextView) a.findViewById(R.id.dates);
        TextView teacherText = (TextView) a.findViewById(R.id.teacher);
        TextView titleText = (TextView) a.findViewById(R.id.title);
        TextView previewText = (TextView) a.findViewById(R.id.previewText);
        TextView bodyText = (TextView) a.findViewById(R.id.description);

        titleText.setText(title);
        bodyText.setText(body);
        previewText.setText(preview);
        teacherText.setText(name);
        datesText.setText(dates);

        rootView = a;

        rootView.findViewById(R.id.card_view).setOnClickListener(new View.OnClickListener() {
                                                                     public void onClick(View v) {
                                                                         AnimationSet animation = new AnimationSet(false); //change to false

                                                                         Animation fadeOut;

                                                                         final View view = v;

                                                                         final float scale = getContext().getResources().getDisplayMetrics().density;

                                                                         if (v.getHeight() == height)
                                                                         {
                                                                             fadeOut = new ResizeAnimation(v, v.getWidth(), height, v.getWidth(), Math.round(170*scale + 0.5f));
                                                                             v.findViewById(R.id.dots).animate().setDuration(100).alpha(1).setListener(new AnimatorListenerAdapter() {
                                                                                 @Override
                                                                                 public void onAnimationEnd(Animator animation) {
                                                                                     view.findViewById(R.id.dots).setVisibility(View.VISIBLE);
                                                                                 }
                                                                             });
                                                                             v.findViewById(R.id.dots).animate().setDuration(100).scaleX(1).setListener(new AnimatorListenerAdapter() {
                                                                                 @Override
                                                                                 public void onAnimationEnd(Animator animation) {
                                                                                     view.findViewById(R.id.dots).setScaleX(1);
                                                                                 }
                                                                             });
                                                                         }
                                                                         else {
                                                                             fadeOut = new ResizeAnimation(v, v.getWidth(), v.getHeight(), v.getWidth(), height);
                                                                             v.findViewById(R.id.dots).animate().setDuration(100).alpha(0).setListener(new AnimatorListenerAdapter() {
                                                                                 @Override
                                                                                 public void onAnimationEnd(Animator animation) {
                                                                                     view.findViewById(R.id.dots).setVisibility(View.INVISIBLE);
                                                                                 }
                                                                             });
                                                                             v.findViewById(R.id.dots).animate().setDuration(100).scaleX(0).setListener(new AnimatorListenerAdapter() {
                                                                                 @Override
                                                                                 public void onAnimationEnd(Animator animation) {
                                                                                     view.findViewById(R.id.dots).setScaleX(0);
                                                                                 }
                                                                             });
                                                                         }

                                                                         animation.addAnimation(fadeOut);

                                                                         animation.setAnimationListener(new Animation.AnimationListener() {
                                                                             @Override
                                                                             public void onAnimationStart(Animation animation) {
                                                                             }

                                                                             @Override
                                                                             public void onAnimationEnd(Animation animation) {
                                                                                 if (view.getHeight() < height - 10) {
                                                                                     //rootView.findViewById(R.id.description).setVisibility(View.INVISIBLE);
                                                                                     //rootView.findViewById(R.id.descriptionSingle).setVisibility(View.VISIBLE);
                                                                                 }
                                                                             }

                                                                             @Override
                                                                             public void onAnimationRepeat(Animation animation) {
                                                                             }
                                                                         });

                                                                         v.startAnimation(animation);
                                                                     }
                                                                 }
        );

        return a;
    }

    public void onViewCreated(View view, Bundle saved) {
        super.onViewCreated(view, saved);
        final ViewTreeObserver observer = view.getViewTreeObserver();
        final View v = view;
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                v.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                final float scale = getContext().getResources().getDisplayMetrics().density;

                height = rootView.findViewById(R.id.card_view).getHeight();

                //Log.d(TAG, "PROGRAM " + height);

                if (!isOpen)
                {
                    ViewGroup.LayoutParams b = rootView.findViewById(R.id.card_view).getLayoutParams();
                    b.height = Math.round(170* scale + 0.5f);
                    rootView.findViewById(R.id.card_view).setLayoutParams(b);
                }
                else
                {
                    rootView.findViewById(R.id.dots).setScaleX(0);
                    rootView.findViewById(R.id.dots).setVisibility(View.GONE);
                }
            }
        });
    }
}
