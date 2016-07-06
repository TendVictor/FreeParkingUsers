package com.example.chen.freeparkingusers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.example.chen.freeparkingusers.Fragments.BaseFragment;
import com.example.chen.freeparkingusers.Fragments.SellerFragment;
import com.example.chen.freeparkingusers.Fragments.ticketFragment;
import com.example.chen.freeparkingusers.view.ViewPagerIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends FragmentActivity {

    private FragmentPagerAdapter mAdapter;
    private ViewPager mViewPager;

    private List<BaseFragment> fragments;

    private SellerFragment mSellerFragment;
    private ticketFragment mTicketFragment;

    private List<String> mDatas = Arrays.asList("商家","停车券1");

    private ViewPagerIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTopView();
        initCenterView();
    }

    private void initTopView() {


    }

    private void initCenterView() {

        mIndicator = (ViewPagerIndicator) findViewById(R.id.id_indicator);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        fragments = new ArrayList<>();
        mSellerFragment = new SellerFragment();
        mSellerFragment.setTitle(mDatas.get(0));

        mTicketFragment = new ticketFragment();
//        ticketFragment.setTitle(mDatas.get(1));
        fragments.add(mSellerFragment);
        fragments.add(mTicketFragment);

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };


        mIndicator.setTabItemTitles(mDatas);
        mViewPager.setAdapter(mAdapter);

        mIndicator.setViewPager(mViewPager,0);

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }


    public class OnPageChangeListener implements ViewPager.OnPageChangeListener{



        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

}
