package com.example.chen.freeparkingusers.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chen.freeparkingusers.R;
import com.example.chen.freeparkingusers.activity.QRCodeActivity;
import com.example.chen.freeparkingusers.adapter.TicketAdapter;
import com.example.chen.freeparkingusers.view.DividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by chen on 16/7/4.
 */
public class ticketFragment extends BaseFragment {

    public static final String TAG = "TicFrg";
    private View rootView = null;

    private SwipeRefreshLayout swipeRefreshLayout = null;
    private RecyclerView recyclerView = null;

    private ArrayList<HashMap<String, String>> dataSet = null;
    private TicketAdapter ticketAdapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle SavedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket, null, false);
        initData();
        initVariables();
        initViewAndEvents(view);
        return view;
    }

    private void initData() {
         dataSet = new ArrayList<HashMap<String, String>>();
         for (int i=0;i<30;i++){
             HashMap<String,String> tmp = new HashMap<String, String>();
             tmp.put("aaa","bbb");
             dataSet.add(tmp);
         }
    }

    private void initVariables() {
        ticketAdapter = new TicketAdapter(getActivity(),dataSet);
        ticketAdapter.setOnItemClickListener(new TicketAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //传递数据
                Intent i = new Intent(getActivity(), QRCodeActivity.class);
                i.putExtra("ticket_id","helloworld");
                startActivity(i);
            }
            @Override
            public void onItemLongClick(View view, int position) {
                Log.e(TAG,"Long click from TicFrg");
            }
        });
    }

    private void initViewAndEvents(View view) {
        recyclerView = $(view, R.id.rv_ticket);
        swipeRefreshLayout = $(view, R.id.sl_ticket);
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#2CBEC5"));

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(ticketAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST));
    }

    private <T extends View> T $(View v, int id) {
        return (T) v.findViewById(id);
    }
}
