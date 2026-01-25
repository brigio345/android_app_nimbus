package it.brigio345.nimbus.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import it.brigio345.nimbus.R;
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
        View view = inflater.inflate(R.layout.fragment_day, container, false);
        ((ListView) view.findViewById(R.id.listview_day))
                .setAdapter(new DayListAdapter(getContext(), content));

        return view;
    }
}