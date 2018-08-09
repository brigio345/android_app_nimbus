package it.brigio345.nimbus.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import it.brigio345.nimbus.adapters.MainListAdapter;


public class MainFragment extends Fragment {
    private static final String CONTENT = "content";

    private String content;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance(String content) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            content = getArguments().getString(CONTENT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ListView listView = new ListView(getContext());
        listView.setNestedScrollingEnabled(true);
        listView.setDivider(null);
        listView.setVerticalScrollBarEnabled(false);
        listView.setHorizontalScrollBarEnabled(false);
        listView.setAdapter(new MainListAdapter(getContext(), content));

        return listView;
    }
}