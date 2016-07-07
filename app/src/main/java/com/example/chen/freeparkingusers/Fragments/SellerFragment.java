package com.example.chen.freeparkingusers.Fragments;

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
import android.widget.Toast;

import com.example.chen.freeparkingusers.R;
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

    private SellerAdapter.FootViewHolder footHolder;
    private boolean isLoading = false;

    private int last = 0;
    private int totalItemCount = 0;

    private final String search_word = "";
    private int number_limit = 0;

    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mRecyclerView;

    private List<SellerInfo> mDatas;
    private SellerAdapter mSellerAdapter;

    private LinearLayoutManager mLayoutManager;

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0x0:
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
            mSellerAdapter.notifyDataSetChanged();
            mSwipeLayout.setRefreshing(false);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container , Bundle SavedInstanceState){
        View view = inflater.inflate(R.layout.fragment_campaign,null,false);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_seller);
        mSwipeLayout.setColorSchemeResources(R.color.colorAppTheme);
        mSwipeLayout.setOnRefreshListener(new onRefreshListener());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_seller);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView,int newstate){
                super.onScrollStateChanged(recyclerView,newstate);
                Log.d("loadmoreBefore",isLoading+"");
                if (last >= totalItemCount-1&& !isLoading) {
                    isLoading = true;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadmore();
                            Log.d("loadmore","loadmore");
                        }
                    },3000);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                last = mLayoutManager.findLastCompletelyVisibleItemPosition();
            }
        });



        mDatas = new ArrayList<>();
        number_limit = 0;
        getSellerInfo(search_word,number_limit);

        mSellerAdapter = new SellerAdapter(getActivity(), (ArrayList<SellerInfo>) mDatas);
        mRecyclerView.setAdapter(mSellerAdapter);
        return view;
    }

    //滑动底层加载更多
    public void loadmore(){
        getSellerInfo(search_word, number_limit);
    }

    public void nomoreData(){
        if(footHolder != null)
            footHolder.setIsHaveData(true);
        if(mRecyclerView.findViewHolderForAdapterPosition(totalItemCount - 1) != null){
            footHolder =
                    (SellerAdapter.FootViewHolder) mRecyclerView.findViewHolderForAdapterPosition(totalItemCount-1);
            footHolder.setIsHaveData(true);
        }
    }

    //恢复刷新之前的状态
    public void resetRecycleStateAndRefresh(){
        Log.d("resetBefore", totalItemCount + "");
        if(footHolder != null){
            footHolder.setIsHaveData(false);
        }
        mDatas.clear();
        number_limit = 0;
        getSellerInfo(search_word,number_limit);
    }

    //测试数据
    private void getDatas(){

        for(int i = 0; i < 10 ; i++){
            SellerInfo s = new SellerInfo(
                    i+"",
                    "name"+i,
                    "SellerImg",
                    "the place:"+i ,
                    "the contact:" +i);
            mDatas.add(s);
        }
    }

    class onRefreshListener implements SwipeRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh() {
            resetRecycleStateAndRefresh();
        }
    }


    private void getSellerInfo(String search_word, final int number_limit){

        NetPostConnection connection = new NetPostConnection(Config.URL_SELLER_SEARCH,
                new NetPostConnection.SuccessCallback() {
                    @Override
                    public void onSuccess(String result) throws JSONException {
                        if(result.equalsIgnoreCase("0")){
                            handler.obtainMessage(0x3).sendToTarget();
                            return;
                        }
                        JSONArray jsonArray = new JSONArray(result);
                        for (int i = 0; i < jsonArray.length(); i++){
                            JSONObject object = (JSONObject) jsonArray.getJSONObject(i);
                            SellerInfo info = new SellerInfo(
                                    object.getString("seller_id"),
                                    object.getString("seller_name"),
                                    object.getString("seller_img"),
                                    object.getString("seller_address"),
                                    object.getString("seller_contact"));
                            mDatas.add(info);
                        }
                        handler.obtainMessage(0x0).sendToTarget();
                    }
                }, new NetPostConnection.FailCallback(){

            @Override
            public void onFail() {
                handler.obtainMessage(0x1).sendToTarget();
            }
        }, "search_word",search_word,"number_limit",number_limit);

    }

}
