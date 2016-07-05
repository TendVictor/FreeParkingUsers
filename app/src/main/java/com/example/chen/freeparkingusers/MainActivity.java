package com.example.chen.freeparkingusers;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.chen.freeparkingusers.Fragments.BaseFragment;
import com.example.chen.freeparkingusers.Fragments.campaignFragment;
import com.example.chen.freeparkingusers.Fragments.mineFragment;
import com.example.chen.freeparkingusers.Fragments.ticketFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    private void initView() {

    }

    private List<BaseFragment> initFragments() {
        List<BaseFragment> fragments =
                new ArrayList<BaseFragment>();

        campaignFragment mCampaign = new campaignFragment();
        mCampaign.setTitle("活动");
        mCampaign.setIconId(R.drawable.car);
        fragments.add(mCampaign);

        ticketFragment mTicketFragment = new ticketFragment();
        mTicketFragment.setTitle("停车场");
        mTicketFragment.setIconId(R.drawable.ticket);
        fragments.add(mTicketFragment);

        mineFragment mMineFragment = new mineFragment();
        mMineFragment.setIconId(R.drawable.info);
        mMineFragment.setTitle("自己");
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


}
