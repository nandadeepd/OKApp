package com.bigfatj.okpro.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bigfatj.okpro.fragments.ActiveGroupsFragment;
import com.bigfatj.okpro.fragments.EmergencyFragment;
import com.bigfatj.okpro.fragments.LocateFragment;

/**
 * Created by Aditya on 04-05-2015.
 */
public class TabPagerAdapter extends FragmentPagerAdapter {

    private final String[] TITLES = {"Groups", "Locate"};

    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return ActiveGroupsFragment.newInstance(position);
           // case 1:
              //  return EmergencyFragment.newInstance(position);
            case 1:
                return LocateFragment.newInstance(position);

        }
        return ActiveGroupsFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }
}
