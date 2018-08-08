package it.brigio345.nimbus;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class MainFragment extends Fragment {
    private class ListAdapter extends BaseAdapter {
        private final Context context;
        private final String content;

        ListAdapter(Context context, String content) {
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
        listView.setAdapter(new ListAdapter(getContext(), content));

        return listView;
    }
}
