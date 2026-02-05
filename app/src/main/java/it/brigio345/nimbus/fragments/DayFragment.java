package it.brigio345.nimbus.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Arrays;

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
        DayFragment fragment = new DayFragment();
        Bundle args = new Bundle();

        if (content != null) {
            String[] spl = content.split(CIELO_KEY);
            if (spl.length > 1) {
                String[] spl2 = spl[1].split(PRECIPITAZIONI_KEY);
                args.putString(CIELO, spl2[0]);
                if (spl2.length > 1) {
                    String[] spl3 = spl2[1].split(VENTI_KEY);
                    args.putString(PRECIPITAZIONI, spl3[0]);
                    if (spl3.length > 1) {
                        String[] spl4 = spl3[1].split(TEMPERATURE_KEY);
                        args.putString(VENTI, spl4[0]);
                        if (spl4.length > 1) {
                            args.putString(TEMPERATURE, spl4[1]);
                        }
                    }
                }
            }
        }
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
