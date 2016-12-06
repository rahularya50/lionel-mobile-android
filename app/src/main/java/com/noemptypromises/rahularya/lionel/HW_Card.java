package com.noemptypromises.rahularya.lionel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
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
 * Use the {@link HW_Card#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HW_Card extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private String body;
    private String code;
    private String teacher;
    private String date;
    private String duration;
    private String subject;

    private Boolean isOpen;

    private static final String TAG = PlaceholderFragment.class.getSimpleName();

    private float height;

    public View rootView;


    //private OnFragmentInteractionListener mListener;

    public HW_Card() {
        // Required empty public constructor
    }

    public static HW_Card newInstance() {
        HW_Card fragment = new HW_Card();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setInfo(String subjectx, String codex, String bodyx, String datex, String teacherx, String durationx, Boolean open)
    {
        subject = subjectx;
        isOpen = open;
        code = codex;
        body = bodyx;
        date = datex;
        teacher = teacherx;
        duration = durationx;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View a = inflater.inflate(R.layout.fragment_hw__card, container, false);

        TextView bodyText = (TextView) a.findViewById(R.id.description);
        TextView previewText = (TextView) a.findViewById(R.id.descriptionSingle);
        TextView dueDateText = (TextView) a.findViewById(R.id.due_date);
        TextView classCodeText = (TextView) a.findViewById(R.id.subject);
        TextView teacherText = (TextView) a.findViewById(R.id.teacher);
        TextView durationText = (TextView) a.findViewById(R.id.duration);
        TextView codeText = (TextView) a.findViewById(R.id.classCode);

        classCodeText.setText(subject);
        bodyText.setText(body);
        previewText.setText(body);
        dueDateText.setText(date.substring(7));
        teacherText.setText(teacher);
        durationText.setText(duration);
        codeText.setText(code);

        rootView = a;

        rootView.findViewById(R.id.card_view).setOnClickListener(new View.OnClickListener() {
                                                                     public void onClick(View v) {
                                                                         AnimationSet animation = new AnimationSet(false); //change to false

                                                                         Animation fadeOut;

                                                                         final View view = v;

                                                                         final float scale = getContext().getResources().getDisplayMetrics().density;

                                                                         if (v.getHeight() == height)
                                                                         {
                                                                             fadeOut = new ResizeAnimation(v, v.getWidth(), height, v.getWidth(), Math.round(160*scale + 0.5f));
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

    // TODO: Rename method, update argument and hook method into UI event
    /*public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    */

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        */
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
                    b.height = Math.round(160* scale + 0.5f);
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

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
     */
}
