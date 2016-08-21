package com.noemptypromises.rahularya.lionel;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Card#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Card extends Fragment {


    public Card() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Card.
     */

    public View rootView;


    public static Card newInstance(String param1, String param2) {
        Card fragment = new Card();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.card_timetable, container, false);
        return rootView;
    }

    public void setSubject(String subject)
    {
        ((TextView) rootView.findViewById(R.id.info_text)).setText(subject);
    }

    public void setPeriod(String period)
    {
        ((TextView) rootView.findViewById(R.id.info_text2)).setText(period);
    }

}
