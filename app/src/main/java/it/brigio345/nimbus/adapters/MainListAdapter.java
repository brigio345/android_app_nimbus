package it.brigio345.nimbus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import it.brigio345.nimbus.R;

public class MainListAdapter extends BaseAdapter {
    private final Context context;
    private final String content;

    public MainListAdapter(Context context, String content) {
        this.context = context;
        this.content = content;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_main, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.textview_mainitem);
        textView.setText(content);

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return content;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getCount() {
        return 1;
    }
}