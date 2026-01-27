package it.brigio345.nimbus.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import it.brigio345.nimbus.R;
import it.brigio345.nimbus.adapters.MainPagerAdapter;
import it.brigio345.nimbus.utils.DateConverter;

public class MainActivity extends AppCompatActivity {
    String url;
    Element overviewContent;
    Elements daysContent;
    Elements days;
    MainPagerAdapter pagerAdapter;
    ViewPager2 viewPager;

    private class DownloadData implements Runnable {
        @Override
        public void run() {
            url = getString(R.string.piemonte_url);

            try {
                InputStream inStream = new URL(url).openStream();
                Document document = Jsoup.parse(inStream, "ISO-8859-1", url);
                daysContent = document.getElementsByAttributeValue("class", "MsoNormal");
                overviewContent = daysContent.get(0);
                daysContent.remove(0);
                days = document.getElementsByTag("table");

                Vector<GregorianCalendar> dayCalendars = new Vector<>();
                // Remove the elements that are not actually days.
                days.removeIf(day -> {
                    try {
                        dayCalendars.add(DateConverter.convertDate(day.text()));
                        return false;
                    } catch (DateConverter.InvalidStringDateException e) {
                        return true;
                    }
                });

                pagerAdapter.clear();

                String overviewContentStr = overviewContent.wholeText().trim()
                        .replaceAll("(?m)(^[ \t]*\\R){2,}", "\n");
                runOnUiThread(() -> {
                    pagerAdapter.addPage(getString(R.string.situazione_meteorologica),
                            overviewContentStr, true);

                    String day;
                    Calendar today = Calendar.getInstance();
                    Calendar tomorrow = Calendar.getInstance();
                    tomorrow.add(Calendar.DAY_OF_YEAR, 1);

                    int size = daysContent.size();

                    for (int i = 0; i < size; i++) {
                        day = days.get(i).text();

                        GregorianCalendar dayCalendar = dayCalendars.get(i);

                        // if the date is older than "today", don't add to tabs
                        if (dayCalendar.compareTo(today) < 0)
                            continue;

                        if (dayCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                                dayCalendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                            day = getString(R.string.oggi);
                        } else if (dayCalendar.get(Calendar.YEAR) == tomorrow.get(Calendar.YEAR) &&
                                dayCalendar.get(Calendar.DAY_OF_YEAR) == tomorrow.get(Calendar.DAY_OF_YEAR)) {
                            day = getString(R.string.domani);
                        } else {
                            String dayOfWeek = dayCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                            int dayOfMonth = dayCalendar.get(Calendar.DAY_OF_MONTH);
                            day = dayOfWeek + " " + dayOfMonth;
                            day = day.substring(0, 1).toUpperCase(Locale.getDefault()) + day.substring(1);
                        }

                        pagerAdapter.addPage(day, daysContent.get(i).wholeText(), false);
                    }

                    pagerAdapter.notifyDataSetChanged();
                    viewPager.setCurrentItem(0);

                    findViewById(R.id.progressbar_main).setVisibility(View.INVISIBLE);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        Network nw = connectivityManager.getActiveNetwork();
        if (nw == null) {
            return false;
        }
        NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
        return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Context context = this;

        final Thread download = new Thread(new DownloadData());

        if (isNetworkAvailable()) {
            download.start();
        } else {
            final AlertDialog alert = new MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.no_connection)
                    .setMessage(R.string.no_connection_message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.try_again, null)
                    .create();

            alert.setOnShowListener(dialogInterface -> {
                Button button = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(view -> {
                    if (isNetworkAvailable()) {
                        findViewById(R.id.progressbar_main).setVisibility(View.VISIBLE);
                        download.start();
                        alert.dismiss();
                    }
                });
            });

            // without this, pressing back button would have no effect
            alert.setOnKeyListener((arg0, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK)
                    finish();

                return false;
            });

            alert.show();
        }

        pagerAdapter = new MainPagerAdapter(this);
        viewPager = findViewById(R.id.viewpager_main);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tablayout_main);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(pagerAdapter.getPageTitle(position))
        ).attach();
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

        if (id == R.id.item_openinbrowser) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
            return true;
        }

        if (id == R.id.item_share) {
            AlertDialog alert = new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.share_forecast)
                    .setMultiChoiceItems(pagerAdapter.getPageTitles(), null,
                            (dialog, which, isChecked) -> {
                                if (isChecked)
                                    mSelectedItems.add(which);

                                else if (mSelectedItems.contains(which))
                                    mSelectedItems.remove(Integer.valueOf(which));
                            })
                    .setPositiveButton(R.string.ok, (dialog, which) -> {
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
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .create();

            alert.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
