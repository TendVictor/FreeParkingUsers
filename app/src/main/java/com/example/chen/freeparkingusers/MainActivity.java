package com.example.chen.freeparkingusers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.chen.freeparkingusers.Fragments.BaseFragment;
import com.example.chen.freeparkingusers.Fragments.SellerFragment;
import com.example.chen.freeparkingusers.Fragments.ticketFragment;
import com.example.chen.freeparkingusers.activity.LoginActivity;
import com.example.chen.freeparkingusers.activity.SellerSearchActivity;
import com.example.chen.freeparkingusers.activity.UserInfoDetailActivity;
import com.example.chen.freeparkingusers.net.Config;
import com.example.chen.freeparkingusers.view.ViewPagerIndicator;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends FragmentActivity implements View.OnClickListener{

    private EditText searchEdit;
    private ImageView scanImage, persnalImage;

    private FragmentPagerAdapter mAdapter;
    private ViewPager mViewPager;

    private List<BaseFragment> fragments;

    private SellerFragment mSellerFragment;
    private ticketFragment mTicketFragment;

    private List<String> mDatas = Arrays.asList("商家","停车券");

    private ViewPagerIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(savedInstanceState != null)
        savedInstanceState.clear();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initTopView();
        initCenterView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mTicketFragment.RefreshTicketInfo();
    }

    private void initTopView() {
        searchEdit = (EditText) findViewById(R.id.ev_search);
        scanImage = (ImageView) findViewById(R.id.iv_scan);
        persnalImage = (ImageView) findViewById(R.id.iv_person);

        searchEdit.setOnClickListener(this);
        scanImage.setOnClickListener(this);
        persnalImage.setOnClickListener(this);
    }

    private void initCenterView() {

        mIndicator = (ViewPagerIndicator) findViewById(R.id.id_indicator);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        fragments = new ArrayList<>();
        mSellerFragment = new SellerFragment();
        mSellerFragment.setTitle(mDatas.get(0));

        mTicketFragment = new ticketFragment();
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
        mIndicator.setBackgroundColor(Color.parseColor("#ffffff"));
        mViewPager.setAdapter(mAdapter);

        mIndicator.setViewPager(mViewPager, 0);

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume(){
        super.onResume();

        //刷新操作
        mTicketFragment.RefreshTicketInfo();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    //设置main中点击事件处理
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.iv_person:
                intoUserInfo();
                break;
            case R.id.iv_scan:
                ScanQRCode();
                break;
            case R.id.ev_search:
                intoSearch();
                break;
        }
    }

    public void ScanQRCode(){
        //已登录则可以扫描
        if(Config.username != null){
            Intent i = new Intent(MainActivity.this, CaptureActivity.class);
            i.putExtra("user_id",Config.username);
            startActivity(i);
        }//没有登录则提示登录
        else{
            new AlertDialog.Builder(this).setMessage("您还没有登录，请先登录").setPositiveButton("这就登录", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                }
            }).setNegativeButton("暂时不", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }
    }

    public void intoSearch(){
        Intent i = new Intent(MainActivity.this, SellerSearchActivity.class);
        if(mSellerFragment.getLatitude() != 0 && mSellerFragment.getLongtitude() != 0){
            i.putExtra("longtitude",mSellerFragment.getLongtitude());
            i.putExtra("latitude",mSellerFragment.getLatitude());
        }
        startActivity(i);
    }

    public void intoUserInfo(){
        if(Config.username != null){
            Intent i = new Intent(MainActivity.this, UserInfoDetailActivity.class);
            startActivity(i);
        }else{
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        }
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
