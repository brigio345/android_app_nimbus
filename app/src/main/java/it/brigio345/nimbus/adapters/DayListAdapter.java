package it.brigio345.nimbus.adapters;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

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
        textView.setText(content[position].trim());

        String title;

        switch (position) {
            case 0:
                title = context.getString(R.string.precipitazioni);
                break;
            case 1:
                title = context.getString(R.string.cielo);
                break;
            case 2:
                title = context.getString(R.string.temperature);
                break;
            case 3:
                title = context.getString(R.string.venti);
                break;
            default:
                title = null;
        }

        textView = convertView.findViewById(R.id.textview_dayitem_title);
        textView.setText(title);

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

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @ColorInt
    private int getColorFromAttr(Context context, @AttrRes int attr) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }
}
