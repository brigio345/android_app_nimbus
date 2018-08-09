package it.brigio345.nimbus.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import it.brigio345.nimbus.R;

public class DayListAdapter extends BaseAdapter {
    private final Context context;
    private final String[] content;

    public DayListAdapter(Context context, String[] content) {
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
        textView.setText(content[position]);

        int color;
        String title;

        switch (position) {
            case 0:
                color = ContextCompat.getColor(context, R.color.colorPrecipitazioni);
                title = context.getString(R.string.precipitazioni);

                break;

            case 1:
                color = ContextCompat.getColor(context, R.color.colorCielo);
                title = context.getString(R.string.cielo);

                break;

            case 2:
                color = ContextCompat.getColor(context, R.color.colorTemperature);
                title = context.getString(R.string.temperature);

                break;

            case 3:
                color = ContextCompat.getColor(context, R.color.colorVenti);
                title = context.getString(R.string.venti);

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
    public Object getItem(int position) {
        return content[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return 4;
    }
}