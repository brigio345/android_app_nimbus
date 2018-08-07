package it.brigio345.nimbus;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String PIEMONTE_URL = "http://www.nimbus.it/italiameteo/previpiemonte.htm";
    private static final String LOMBARDIA_URL = "http://www.nimbus.it/italiameteo/previlombardia.htm";
    private String url;
    private Elements content;
    private Elements days;
    private CustomPagerAdapter pagerAdapter;

    private class CustomPagerAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> fragments;
        private List<String> tabTitles;
        private List<String> tabContents;

        CustomPagerAdapter(FragmentManager fm) {
            super(fm);
            initializeAdapter();
        }

        @Override
        public Fragment getItem (int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        void addMainPage(String title, String content) {
            tabTitles.add(title);
            tabContents.add(content);

            fragments.add(MainFragment.newInstance(content));
        }

        void addPage(String title, String content) {
            tabTitles.add(title);
            tabContents.add(content);

            fragments.add(DayFragment.newInstance(content));
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles.get(position);
        }

        CharSequence[] getPageTitles() {
            return tabTitles.toArray(new CharSequence[tabTitles.size()]);
        }

        String getPageContent(int position) {
            return tabContents.get(position);
        }

        void initializeAdapter() {
            fragments = new LinkedList<>();
            tabTitles = new LinkedList<>();
            tabContents = new LinkedList<>();
        }
    }

    private class DownloadData implements Runnable {
        private final int id;

        DownloadData(int id) {
            this.id = id;
        }

        @Override
        public void run() {

            switch (id) {
                case 0:
                    url = PIEMONTE_URL;
                    break;

                case 1:
                    url = LOMBARDIA_URL;
                    break;

                default:
                    url = PIEMONTE_URL;
            }

            try {
                InputStream inStream = new URL(url).openStream();
                Document document = Jsoup.parse(inStream, "ISO-8859-1", url);
                content = document.getElementsByAttributeValue("class", "MsoNormal");
                days = document.getElementsByTag("table");

                pagerAdapter.initializeAdapter();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pagerAdapter.addMainPage(getString(R.string.situazione_meteorologica),
                                content.get(0).text());

                        String day;
                        Calendar current = Calendar.getInstance();

                        int size = content.size();
                        int cmp;
                        boolean TODAY_PRINTED = false;
                        boolean TOMORROW_PRINTED = false;

                        for (int i = 1, j = 6; i < size; i++, j++) {
                            day = days.get(j).text();
                            cmp = DateConverter.convertDate(day).compareTo(current);

                            if (cmp < 0)
                                continue;

                            if (!TODAY_PRINTED) {
                                day = getString(R.string.oggi);
                                TODAY_PRINTED = true;
                            } else if (!TOMORROW_PRINTED) {
                                day = getString(R.string.domani);
                                TOMORROW_PRINTED = true;
                            } else {
                                day = day.replaceFirst("\\s[\\d]{4}", ""); // removes the year
                            }

                            pagerAdapter.addPage(day, content.get(i).text());
                        }

                        pagerAdapter.notifyDataSetChanged();

                        ((ConstraintLayout) findViewById(R.id.constraint_layout_main))
                                .removeView(findViewById(R.id.progressbar_main));
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        final Context context = this;

        Spinner spinner = findViewById(R.id.spinner_main_regions);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final ConnectivityManager cm = (ConnectivityManager) getSystemService(
                        Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                final Thread download = new Thread(new DownloadData(position));

                if (activeNetwork == null || !activeNetwork.isConnectedOrConnecting()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final AlertDialog alert = new AlertDialog.Builder(context)
                                    .setTitle(R.string.no_connection)
                                    .setMessage(R.string.no_connection_message)
                                    .setCancelable(false)
                                    .setPositiveButton(R.string.try_again, null)
                                    .create();

                            alert.setOnShowListener(new DialogInterface.OnShowListener() {

                                @Override
                                public void onShow(DialogInterface dialogInterface) {
                                    Button button = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                                    button.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View view) {
                                            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                                            if (activeNetwork != null &&
                                                    activeNetwork.isConnectedOrConnecting()) {
                                                download.start();
                                                alert.dismiss();
                                            }
                                        }
                                    });
                                }
                            });

                            alert.setOnKeyListener(new Dialog.OnKeyListener() {
                                @Override
                                public boolean onKey(DialogInterface arg0, int keyCode,
                                                     KeyEvent event) {
                                    if (keyCode == KeyEvent.KEYCODE_BACK)
                                        finish();

                                    return false;
                                }
                            });

                            alert.show();
                        }
                    });
                } else {
                    download.start();
                }

                editor.putInt("DEFAULT_REGION", position);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        pagerAdapter = new CustomPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.viewpager_main);
        viewPager.setAdapter(pagerAdapter);

        int default_region = sharedPref.getInt("DEFAULT_REGION", Context.MODE_PRIVATE);
        spinner.setSelection(default_region);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_appbar_actions, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        final List<Integer> mSelectedItems = new LinkedList<>();

        switch (id) {
            case R.id.item_openinbrowser:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
                return true;

            case R.id.item_share:
                AlertDialog alert = new AlertDialog.Builder(this)
                        .setTitle(R.string.share_forecast)
                        .setMultiChoiceItems(pagerAdapter.getPageTitles(), null,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                        if (isChecked)
                                            mSelectedItems.add(which);

                                        else if (mSelectedItems.contains(which))
                                            mSelectedItems.remove(Integer.valueOf(which));
                                    }
                                })
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mSelectedItems.size() != 0) {
                                    Intent sendIntent = new Intent();
                                    sendIntent.setAction(Intent.ACTION_SEND);
                                    StringBuilder builder = new StringBuilder();

                                    builder.append(getString(R.string.share_intro));
                                    builder.append(" (");

                                    if (url.equals(PIEMONTE_URL))
                                        builder.append(getString(R.string.piemonte));
                                    else
                                        builder.append(getString(R.string.lombardia));

                                    builder.append(")\n\n");

                                    for (int it : mSelectedItems) {
                                        builder.append(pagerAdapter.getPageTitle(it));
                                        builder.append("\n\n");
                                        builder.append(pagerAdapter.getPageContent(it));
                                        builder.append("\n\n\n");
                                    }

                                    builder.append(getString(R.string.share_closing));

                                    sendIntent.putExtra(Intent.EXTRA_TEXT, builder.toString());
                                    sendIntent.setType("text/plain");
                                    startActivity(sendIntent);
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create();

                alert.show();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
