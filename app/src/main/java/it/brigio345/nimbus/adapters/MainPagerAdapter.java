package it.brigio345.nimbus.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.LinkedList;
import java.util.List;

import it.brigio345.nimbus.fragments.DayFragment;
import it.brigio345.nimbus.fragments.MainFragment;

public class MainPagerAdapter extends FragmentStateAdapter {
    private final List<Fragment> fragments = new LinkedList<>();
    private final List<String> tabTitles = new LinkedList<>();
    private final List<String> tabContents = new LinkedList<>();

    public MainPagerAdapter(FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

    public void addPage(String title, String content, boolean isMain) {
        tabTitles.add(title);
        tabContents.add(content);

        if (!isMain)
            fragments.add(DayFragment.newInstance(content));
        else
            fragments.add(MainFragment.newInstance(content));
    }

    public CharSequence getPageTitle(int position) {
        return tabTitles.get(position);
    }

    public CharSequence[] getPageTitles() {
        return tabTitles.toArray(new CharSequence[0]);
    }

    public String getPageContent(int position) {
        return tabContents.get(position);
    }

    public void clear() {
        if (fragments.isEmpty() && tabTitles.isEmpty() && tabContents.isEmpty()) {
            return;
        }
        fragments.clear();
        tabTitles.clear();
        tabContents.clear();
        notifyDataSetChanged();
    }
}
