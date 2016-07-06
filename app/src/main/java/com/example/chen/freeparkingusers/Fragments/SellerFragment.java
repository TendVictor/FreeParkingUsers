package com.example.chen.freeparkingusers.Fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chen.freeparkingusers.R;
import com.example.chen.freeparkingusers.adapter.SellerAdapter;
import com.example.chen.freeparkingusers.item.SellerInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen on 16/7/4.
 */
public class SellerFragment extends BaseFragment{

    private final String url = "";

    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mRecyclerView;

    private List<SellerInfo> mDatas;
    private SellerAdapter mSellerAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container , Bundle SavedInstanceState){
        View view = inflater.inflate(R.layout.fragment_campaign,null,false);
        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_seller);
        mSwipeLayout.setOnRefreshListener(new onRefreshListener());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_seller);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDatas = new ArrayList<>();
        getDatas();

        mSellerAdapter = new SellerAdapter(getActivity(), (ArrayList<SellerInfo>) mDatas);
        mRecyclerView.setAdapter(mSellerAdapter);
        return view;
    }

    private void getDatas(){

        for(int i = 0; i < 30 ; i++){
            SellerInfo s = new SellerInfo(i+"","name"+i,"SellerImg","the place:"+i , "the contact:" +i);
            mDatas.add(s);
        }
    }

    class onRefreshListener implements SwipeRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh() {

        }
    }



}
