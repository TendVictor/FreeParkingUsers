package com.example.chen.freeparkingusers.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    private final String search_word = "";
    private int number_limit = 0;

    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mRecyclerView;

    private List<SellerInfo> mDatas;
    private SellerAdapter mSellerAdapter;

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0x0:
                    number_limit+=10;
                    mSellerAdapter.notifyDataSetChanged();
                    break;
                case 0x1:
                    Toast.makeText(getActivity(),"更新失败，请检查网络",Toast.LENGTH_SHORT).show();
                    break;
            }
            mSwipeLayout.setRefreshing(false);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container , Bundle SavedInstanceState){
        View view = inflater.inflate(R.layout.fragment_campaign,null,false);
        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_seller);
        mSwipeLayout.setColorSchemeResources(R.color.colorAppTheme);
        mSwipeLayout.setOnRefreshListener(new onRefreshListener());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_seller);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDatas = new ArrayList<>();
        number_limit = 0;
        getDatas();

        mSellerAdapter = new SellerAdapter(getActivity(), (ArrayList<SellerInfo>) mDatas);
        mRecyclerView.setAdapter(mSellerAdapter);
        return view;
    }

    private void getDatas(){

        for(int i = 0; i < 30 ; i++){
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
            getSellerInfo(search_word,number_limit);

        }
    }


    private void getSellerInfo(String search_word, final int number_limit){

        NetPostConnection connection = new NetPostConnection(Config.URL_SELLER_SEARCH,
                new NetPostConnection.SuccessCallback() {
                    @Override
                    public void onSuccess(String result) throws JSONException {
                        JSONArray jsonArray = new JSONArray(result);
                        mDatas.clear();
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
