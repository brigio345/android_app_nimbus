package it.brigio345.nimbus.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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

import it.brigio345.nimbus.adapters.MainPagerAdapter;
import it.brigio345.nimbus.utils.DateConverter;
import it.brigio345.nimbus.R;

public class MainActivity extends AppCompatActivity {
    private String url;
    private Elements content;
    private Elements days;
    private MainPagerAdapter pagerAdapter;
    private ViewPager viewPager;

    private class DownloadData implements Runnable {
        private final int id;

        DownloadData(int id) {
            this.id = id;
        }

        @Override
        public void run() {

            switch (id) {
                case 1:
                    url = getString(R.string.lombardia_url);
                    break;

                default:
                    url = getString(R.string.piemonte_url);
            }

            try {
                InputStream inStream = new URL(url).openStream();
                Document document = Jsoup.parse(inStream, "ISO-8859-1", url);
                content = document.getElementsByAttributeValue("class", "MsoNormal");
                days = document.getElementsByTag("table");

                pagerAdapter.clear();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pagerAdapter.addPage(getString(R.string.situazione_meteorologica),
                                content.get(0).text(), true);

                        String day;
                        Calendar today = Calendar.getInstance();

                        int size = content.size();
                        boolean todaySet = false;
                        boolean tomorrowSet = false;

                        for (int i = 1, j = 6; i < size; i++, j++) {
                            day = days.get(j).text();

                            // if the date is older than "today", don't add to tabs
                            if (DateConverter.convertDate(day).compareTo(today) < 0)
                                continue;

                            if (tomorrowSet) {
                                day = day.replaceFirst("\\s[\\d]{4}", ""); // removes the year
                            } else if (todaySet) {
                                day = getString(R.string.domani);
                                tomorrowSet = true;
                            } else {
                                day = getString(R.string.oggi);
                                todaySet = true;
                            }

                            pagerAdapter.addPage(day, content.get(i).text(), false);
                        }

                        pagerAdapter.notifyDataSetChanged();
                        viewPager.setCurrentItem(0);

                        findViewById(R.id.progressbar_main).setVisibility(View.INVISIBLE);
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
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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

                if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                    findViewById(R.id.progressbar_main).setVisibility(View.VISIBLE);
                    download.start();
                } else {
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

                    // without this, pressing back button would have no effect
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

                editor.putInt("DEFAULT_REGION", position);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.viewpager_main);
        viewPager.setAdapter(pagerAdapter);

        Intent intent = getIntent();

        boolean region_selected = false;

        if (intent != null && intent.getAction().equals(Intent.ACTION_VIEW)) {
            String path = intent.getData().getPath();

            if (path != null) {
                if (path.equals(getString(R.string.piemonte_url_path))) {
                    spinner.setSelection(0);
                    region_selected = true;
                } else if (path.equals(getString(R.string.lombardia_url_path))) {
                    spinner.setSelection(1);
                    region_selected = true;
                }
            }
        }

        if (!region_selected)
            spinner.setSelection(sharedPref.getInt("DEFAULT_REGION", Context.MODE_PRIVATE));
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
                                    public void onClick(DialogInterface dialog, int which,
                                                        boolean isChecked) {
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

                                    builder.append(url);

                                    builder.append("\n\n");

                                    for (int it : mSelectedItems) {
                                        builder.append(pagerAdapter.getPageTitle(it));
                                        builder.append("\n\n");
                                        builder.append(pagerAdapter.getPageContent(it));
                                        builder.append("\n\n\n");
                                    }

                                    builder.append(getString(R.string.share_closing));

                                    sendIntent.putExtra(Intent.EXTRA_TEXT, builder.toString());
                                    sendIntent.setType("text/plain");
                                    startActivity(Intent.createChooser(sendIntent,
                                            getString(R.string.share_forecast)));
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