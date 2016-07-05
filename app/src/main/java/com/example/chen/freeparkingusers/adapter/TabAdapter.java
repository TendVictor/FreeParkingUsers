package com.example.chen.freeparkingusers.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.chen.freeparkingusers.Fragments.BaseFragment;

import java.util.List;

/**
 * Created by chen on 16/7/5.
 */
public class TabAdapter extends FragmentPagerAdapter{

    private List<BaseFragment> fragments;
    private List<String> list_strings;

    public TabAdapter(FragmentManager fm, List<BaseFragment> fragments, List<String> list_strings){
        super(fm);
        this.fragments = fragments;
        this.list_strings = list_strings;
    }


    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public CharSequence getPagerTitle(int position){
        return list_strings.get(position % list_strings.size());
    }
}
