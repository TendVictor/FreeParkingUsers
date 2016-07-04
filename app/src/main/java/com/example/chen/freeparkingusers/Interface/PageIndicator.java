package com.example.chen.freeparkingusers.Interface;

import android.support.v4.view.ViewPager;

/**
 * Created by chen on 16/7/4.
 */
public interface PageIndicator extends ViewPager.OnPageChangeListener{

    void setViewPager(ViewPager view);
    void setViewPager(ViewPager view, int initialPosition);
    void setCurrentItem(int item);
    void setOnPageChangeListener(ViewPager.OnPageChangeListener listener);
    void notifyDataSetChanged();
}
