package it.brigio345.nimbus;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class DayFragment extends Fragment {
    private class ListAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final ArrayList<String> content;

        ListAdapter(Context context, ArrayList<String> content) {
            super(context, 0, content);
            this.context = context;
            this.content = content;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context)
                        .inflate(R.layout.item_day, parent, false);
            }

            TextView textView = convertView.findViewById(R.id.textview_dayitem_content);
            textView.setText(content.get(position));

            Resources resources = getResources();

            int color;
            String title;

            switch (position) {
                case 0:
                    color = resources.getColor(R.color.colorPrecipitazioni);
                    title = getString(R.string.precipitazioni);

                    break;

                case 1:
                    color = resources.getColor(R.color.colorCielo);
                    title = getString(R.string.cielo);

                    break;

                case 2:
                    color = resources.getColor(R.color.colorTemperature);
                    title = getString(R.string.temperature);

                    break;

                case 3:
                    color = resources.getColor(R.color.colorVenti);
                    title = getString(R.string.venti);

                    break;

                default:
                    color = -1;
                    title = null;
            }

            textView = convertView.findViewById(R.id.textview_dayitem_title);
            textView.setText(title);

            ((CardView) convertView.findViewById(R.id.cardview_dayitem))
                    .setCardBackgroundColor(color);

            return convertView;
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    private static final String CIELO = "cielo";
    private static final String PRECIPITAZIONI = "precipitazioni";
    private static final String VENTI = "venti";
    private static final String TEMPERATURE = "temperature";

    private ArrayList<String> content;

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
            content = new ArrayList<>(4);
            content.add(args.getString(PRECIPITAZIONI));
            content.add(args.getString(CIELO));
            content.add(args.getString(TEMPERATURE));
            content.add(args.getString(VENTI));
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
        listView.setAdapter(new ListAdapter(getContext(), content));

        return listView;
    }
}
