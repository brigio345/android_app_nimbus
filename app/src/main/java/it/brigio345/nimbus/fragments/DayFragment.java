package it.brigio345.nimbus.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import it.brigio345.nimbus.adapters.DayListAdapter;

public class DayFragment extends Fragment {
    private static final String CIELO = "cielo";
    private static final String PRECIPITAZIONI = "precipitazioni";
    private static final String VENTI = "venti";
    private static final String TEMPERATURE = "temperature";

    private String[] content;

    public DayFragment() {
        // Required empty public constructor
    }

    public static DayFragment newInstance(String content) {
        DayFragment fragment = new DayFragment();

        String[] spl;
        Bundle args = new Bundle();

        spl = content.split("Cielo:");
        spl = spl[1].split("Precipitazioni:");
        args.putString(CIELO, spl[0]);
        spl = spl[1].split("Venti:");
        args.putString(PRECIPITAZIONI, spl[0]);
        spl = spl[1].split("Temperature:");
        args.putString(VENTI, spl[0]);
        args.putString(TEMPERATURE, spl[1]);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        if (args != null) {
            content = new String[4];
            content[0] = args.getString(PRECIPITAZIONI);
            content[1] = args.getString(CIELO);
            content[2] = args.getString(TEMPERATURE);
            content[3] = args.getString(VENTI);
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
        listView.setAdapter(new DayListAdapter(getContext(), content));

        return listView;
    }
}