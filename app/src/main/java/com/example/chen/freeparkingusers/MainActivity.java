package com.example.chen.freeparkingusers;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.example.chen.freeparkingusers.Fragments.BaseFragment;
import com.example.chen.freeparkingusers.Fragments.campaignFragment;
import com.example.chen.freeparkingusers.Fragments.mineFragment;
import com.example.chen.freeparkingusers.Fragments.ticketFragment;
import com.example.chen.freeparkingusers.Interface.IconPagerAdapter;
import com.example.chen.freeparkingusers.LinearLayout.IconTabPageIndicator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

    private ViewPager mViewPager;
    private IconTabPageIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.main_viewPager);
        mIndicator = (IconTabPageIndicator) findViewById(R.id.indicator);

        List<BaseFragment> fragments = initFragments();

        FragmentAdapter adapter = new FragmentAdapter(fragments,getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mIndicator.setViewPager(mViewPager);

        Log.d("fragments'size",fragments.size()+"");
    }

    private List<BaseFragment> initFragments(){
        List<BaseFragment> fragments =
                new ArrayList<BaseFragment>();

        campaignFragment mCampaign = new campaignFragment();
        mCampaign.setTitle("活动");
        fragments.add(mCampaign);

        ticketFragment mTicketFragment = new ticketFragment();
        mCampaign.setTitle("停车场");
        fragments.add(mTicketFragment);

        mineFragment  mMineFragment= new mineFragment();
        mCampaign.setTitle("自己");
        fragments.add(mMineFragment);

        return fragments;
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }


    class FragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
        private List<BaseFragment> mFragments;

        public FragmentAdapter(List<BaseFragment> fragments, FragmentManager fm) {
            super(fm);
            mFragments = fragments;
        }

        @Override
        public BaseFragment getItem(int i) {
            return mFragments.get(i);
        }

        @Override
        public int getIconResId(int index) {
            return 0;
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragments.get(position).getTitle();
        }
    }
}
