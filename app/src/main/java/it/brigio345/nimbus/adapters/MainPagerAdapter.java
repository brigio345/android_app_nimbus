package it.brigio345.nimbus.adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.LinkedList;
import java.util.List;

import it.brigio345.nimbus.fragments.DayFragment;
import it.brigio345.nimbus.fragments.MainFragment;

public class MainPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragments;
    private List<String> tabTitles;
    private List<String> tabContents;

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);

        fragments = new LinkedList<>();
        tabTitles = new LinkedList<>();
        tabContents = new LinkedList<>();
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

    public void addMainPage(String title, String content) {
        tabTitles.add(title);
        tabContents.add(content);

        fragments.add(MainFragment.newInstance(content));
    }

    public void addPage(String title, String content) {
        tabTitles.add(title);
        tabContents.add(content);

        fragments.add(DayFragment.newInstance(content));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles.get(position);
    }

    public CharSequence[] getPageTitles() {
        return tabTitles.toArray(new CharSequence[tabTitles.size()]);
    }

    public String getPageContent(int position) {
        return tabContents.get(position);
    }

    public void clear() {
        fragments.clear();
        tabTitles.clear();
        tabContents.clear();
    }
}