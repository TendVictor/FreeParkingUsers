package com.example.chen.freeparkingusers.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.chen.freeparkingusers.R;
import com.example.chen.freeparkingusers.activity.SellerDetailActivity;
import com.example.chen.freeparkingusers.adapter.SellerAdapter;
import com.example.chen.freeparkingusers.item.SellerInfo;
import com.example.chen.freeparkingusers.net.Config;
import com.example.chen.freeparkingusers.net.NetPostConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen on 16/7/4.
 */
public class SellerFragment extends BaseFragment{

    private LocationClient mLocationClient = null;
    private BDLocationListener mdListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location

            latitude = location.getLatitude();
            longtitude = location.getLongitude();
            Log.d("latitude",latitude+"         " +location.getLocType());
            Log.d("longtitude", longtitude + "");

            if(!checkInfoIsNull(latitude,longtitude)){
                if(!isLoading)
                    mDatas.clear();
                getSellerInfo(search_word,number_limit,latitude,longtitude);
            }
            mLocationClient.stop();
        }

    };

    private double latitude = 0;
    private double longtitude = 0;

    private SellerAdapter.FootViewHolder footHolder;
    private boolean isLoading = false;
    private boolean loadingAble = true;

    private int last = 0;
    private int totalItemCount = 0;

    private String search_word = "";
    private int number_limit = 0;

    private RelativeLayout nodataLinear;
    private TextView tv_showNodata;

    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mRecyclerView;

    private List<SellerInfo> mDatas;
    private SellerAdapter mSellerAdapter;

    private LinearLayoutManager mLayoutManager;

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0x0://加载更多跟刷新数据
                    number_limit+=10;
                    totalItemCount = mDatas.size();
                    break;
                case 0x1:
                    Toast.makeText(getActivity(),"更新失败，请检查网络",Toast.LENGTH_SHORT).show();
                    break;
                case 0x2:
                    getDatas();
                    totalItemCount = mSellerAdapter.getItemCount();
                    isLoading = false;
                    Toast.makeText(getActivity(),"更新失败,没有数据",Toast.LENGTH_SHORT).show();
                    break;
                case 0x3://没有更多数据了
                    nomoreData();
                    isLoading = false;
                    break;
            }
            if(footHolder == null)
                footHolder = (SellerAdapter.FootViewHolder)
                        mRecyclerView.findViewHolderForAdapterPosition(totalItemCount);
            adjustIfhasData();
            mSellerAdapter.notifyDataSetChanged();
            mSwipeLayout.setRefreshing(false);

            tv_showNodata.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getSellerInfoAfterLocation("",0);
                }
            });
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container , Bundle SavedInstanceState){
        View view = inflater.inflate(R.layout.fragment_campaign,null,false);
        initLocation();
        initView(view);


        return view;
    }

    //初始化位置信息
    private void initLocation() {
        mLocationClient = new LocationClient(getActivity().getApplicationContext());
        mLocationClient.registerLocationListener(mdListener);

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(1000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }


    //初始化view
    private void initView(View view) {
        nodataLinear = (RelativeLayout) view.findViewById(R.id.container_campaign_nodata);
        tv_showNodata = (TextView) view.findViewById(R.id.tv_littledata);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_seller);
        mSwipeLayout.setColorSchemeResources(R.color.colorAppTheme);
        mSwipeLayout.setOnRefreshListener(new onRefreshListener());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_seller);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newstate) {
                super.onScrollStateChanged(recyclerView, newstate);
                Log.d("loadmoreBefore", isLoading + "");
                if (last >= totalItemCount && !isLoading && loadingAble) {
                    isLoading = true;
                    loadmore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                last = mLayoutManager.findLastCompletelyVisibleItemPosition();
                Log.d("second1", mLayoutManager.findLastVisibleItemPosition() + "  " + mLayoutManager.getChildCount());
                if (mRecyclerView.getChildAt(mLayoutManager.getChildCount()) != null) {
                    Log.d("second", "1");
                    if (mSellerAdapter.getItemViewType(mLayoutManager.findLastVisibleItemPosition()) == 2) {
                        Log.d("height", mRecyclerView.getChildAt(mLayoutManager.findLastVisibleItemPosition()).getHeight() + "");
                    }
                }
            }
        });



        mDatas = new ArrayList<>();
        number_limit = 0;
        getSellerInfoAfterLocation(search_word,number_limit);
        adjustIfhasData();

        mSellerAdapter = new SellerAdapter(getActivity(), (ArrayList<SellerInfo>) mDatas);
        mSellerAdapter.setOnItemClickListener(new SellerAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SellerInfo seller_info = mDatas.get(position);
                Intent intent = new Intent(getActivity(), SellerDetailActivity.class);
                intent.putExtra("seller_info", seller_info);
                startActivity(intent);
            }
        });

        mRecyclerView.setAdapter(mSellerAdapter);
    }


    //判断界面是否有数据
    public void adjustIfhasData(){
        if(mDatas.isEmpty()){
            //没有数据
            mSwipeLayout.setVisibility(View.GONE);
            nodataLinear.setVisibility(View.VISIBLE);
        }else{
            //有数据
            nodataLinear.setVisibility(View.GONE);
            mSwipeLayout.setVisibility(View.VISIBLE);
        }

    }


    //滑动底层加载更多
    public void loadmore(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("loadmore", "loadmore    "+search_word +"    "+ number_limit);
                getSellerInfoAfterLocation(search_word,number_limit);
            }
        }, 3000);
    }

    //没有数据的展示
    public void nomoreData(){
        if(footHolder != null)
            footHolder.setIsHaveData(true);
        if(mRecyclerView.findViewHolderForAdapterPosition(totalItemCount) != null){
            footHolder =
                    (SellerAdapter.FootViewHolder) mRecyclerView.findViewHolderForAdapterPosition(totalItemCount);
            footHolder.setIsHaveData(true);
        }
        loadingAble = false;
    }

    //恢复刷新之前的状态
    public void resetRecycleStateAndRefresh(){
        Log.d("resetBefore", totalItemCount + "");
        if(footHolder != null){
            footHolder.setIsHaveData(false);
        }
        search_word = "";
        number_limit = 0;
        isLoading = false;
        loadingAble = true;
        getSellerInfoAfterLocation(search_word,number_limit);
    }

    //测试数据
    private void getDatas(){

        for(int i = 0; i < 10 ; i++){
            SellerInfo s = new SellerInfo(
                    i+"",
                    "name"+i,
                    "SellerImg",
                    "the place:"+i ,
                    "the contact:" +i,
                    "the distance:"+ i);
            mDatas.add(s);
        }
    }

    class onRefreshListener implements SwipeRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh() {
            resetRecycleStateAndRefresh();
        }
    }

    private void getSellerInfoAfterLocation(String search_word , final int number_limit){
        this.search_word = search_word;
        this.number_limit = number_limit;
        mLocationClient.start();
    }


    private void getSellerInfo(String search_word, final int number_limit,double latitude,double longtitude){

        NetPostConnection connection = new NetPostConnection(Config.URL_SELLER_SEARCH,
                new NetPostConnection.SuccessCallback() {
                    @Override
                    public void onSuccess(String result) throws JSONException {
                        if(result.contains("\"state\":0")){
                            handler.obtainMessage(0x3).sendToTarget();
                            return;
                        }
                        JSONArray jsonArray = new JSONArray(result);
                        for (int i = 0; i < jsonArray.length(); i++){
                            JSONObject object = (JSONObject) jsonArray.getJSONObject(i);
                            String [] address = object.getString("seller_address").split("%");

                            SellerInfo info = new SellerInfo(
                                    object.getString("seller_id"),
                                    object.getString("seller_name"),
                                    object.getString("seller_img"),
                                    address[0],
                                    object.getString("seller_contact"),
                                    object.getString("distance")
                            );
                            mDatas.add(info);
                        }
                        handler.obtainMessage(0x0).sendToTarget();
                    }
                }, new NetPostConnection.FailCallback(){

            @Override
            public void onFail() {
                handler.obtainMessage(0x1).sendToTarget();
            }
        }, "search_word",search_word,"number_limit",number_limit,"location_j",longtitude,"location_w",latitude);

    }

    //判断参数是否都存在
    private boolean checkInfoIsNull(Object... keys) {
        boolean someisNull = false;
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] == null){
                someisNull = true;
                Log.d("someisNull", keys[i].toString()+"  is null");
                break;
            }
        }
        return someisNull;
    }

    /**

     */
    public double getLatitude(){
        return latitude;
    }

    public double getLongtitude(){
        return longtitude;
    }

}
