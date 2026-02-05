package it.brigio345.nimbus.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.brigio345.nimbus.R;
import it.brigio345.nimbus.adapters.DayListAdapter;

public class DayFragment extends Fragment {
    private static final String CIELO = "cielo";
    private static final String PRECIPITAZIONI = "precipitazioni";
    private static final String VENTI = "venti";
    private static final String TEMPERATURE = "temperature";

    private static final String CIELO_KEY = "Cielo:";
    private static final String PRECIPITAZIONI_KEY = "Precipitazioni:";
    private static final String VENTI_KEY = "Venti:";
    private static final String TEMPERATURE_KEY = "Temperature:";

    private String[] content;

    public DayFragment() {
        // Required empty public constructor
    }

    public static DayFragment newInstance(String content) {
        if (content == null)
            return null;

        String regex = CIELO_KEY + "(.*?)" + PRECIPITAZIONI_KEY + "(.*?)" + VENTI_KEY + "(.*?)" + TEMPERATURE_KEY + "(.*)";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content.trim());

        if (!matcher.matches())
            return null;

        DayFragment fragment = new DayFragment();
        Bundle args = new Bundle();
        args.putString(CIELO, matcher.group(1).trim());
        args.putString(PRECIPITAZIONI, matcher.group(2).trim());
        args.putString(VENTI, matcher.group(3).trim());
        args.putString(TEMPERATURE, matcher.group(4).trim());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        content = new String[4];
        if (args != null) {
            content[0] = args.getString(PRECIPITAZIONI, "");
            content[1] = args.getString(CIELO, "");
            content[2] = args.getString(TEMPERATURE, "");
            content[3] = args.getString(VENTI, "");
        } else {
            Arrays.fill(content, "");
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
