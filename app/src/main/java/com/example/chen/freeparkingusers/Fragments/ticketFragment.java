package com.example.chen.freeparkingusers.Fragments;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.chen.freeparkingusers.R;
import com.example.chen.freeparkingusers.activity.QRCodeActivity;
import com.example.chen.freeparkingusers.adapter.TicketAdapter;
import com.example.chen.freeparkingusers.net.Config;
import com.example.chen.freeparkingusers.net.NetPostConnection;
import com.example.chen.freeparkingusers.view.DividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by chen on 16/7/4.
 */
public class ticketFragment extends BaseFragment {

    public static final String TAG = "TicFrg";

    private SwipeRefreshLayout swipeRefreshLayout = null;
    private RecyclerView recyclerView = null;

    private ArrayList<HashMap<String, String>> dataSet = null;
    private TicketAdapter ticketAdapter = null;

    private LinearLayout llContainer = null;

    //4 test
    private Button btnTest =  null;
    private String username = "sb";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle SavedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket, null, false);
        initViewAndEvents(view);
        initData();
        initVariables();
        return view;
    }

    private void initData() {
         dataSet = new ArrayList<HashMap<String, String>>();
         swipeRefreshLayout.setRefreshing(true);
         FetchTicketData();
    }

    private void initVariables() {
        ticketAdapter = new TicketAdapter(getActivity(),dataSet);
        ticketAdapter.setOnItemClickListener(new TicketAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //传递数据
                Intent i = new Intent(getActivity(), QRCodeActivity.class);
                i.putExtra("ticket_id",dataSet.get(position).get("ticket_id"));
                startActivity(i);
            }
            @Override
            public void onItemLongClick(View view, int position) {
                Log.e(TAG,"Long click from TicFrg");
            }
        });
    }

    private void initViewAndEvents(View view) {

        //test
        btnTest = $(view,R.id.btn_refresh);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = "user";
                FetchTicketData();
            }
        });

        llContainer = $(view,R.id.ll_nodata_container);
        recyclerView = $(view, R.id.rv_ticket);
        swipeRefreshLayout = $(view, R.id.sl_ticket);
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#2CBEC5"));

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FetchTicketData();
            }
        });
    }

    public void FetchTicketData(){
        new NetPostConnection(Config.URL_GET_ALLTICKETS, new NetPostConnection.SuccessCallback() {
            @Override
            public void onSuccess(String result) throws JSONException {
                Message msg = new Message();
                msg.obj = result;
                msg.what= NET_SUCCESS;
                handler.sendMessage(msg);
            }
        }, new NetPostConnection.FailCallback() {
            @Override
            public void onFail() {
                handler.sendEmptyMessage(NET_FAILURE);
            }
        },new Object[]{
             "user_id",username
        });
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case NET_SUCCESS:
                    swipeRefreshLayout.setRefreshing(false);
                    String result = (String) msg.obj;
                    Log.e("TAG",result);
                    parseJsonInfo(result);

                    recyclerView.setAdapter(ticketAdapter);
                    break;
                case NET_FAILURE:
                    break;
            }
        }
    };

    private void SetViewVisible(int state){
        if(state == View.GONE){
            llContainer.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
        }else{
            swipeRefreshLayout.setVisibility(View.GONE);
            llContainer.setVisibility(View.VISIBLE);
        }
    }
    private void parseJsonInfo(String result) {
        if(result.contains("\"state\":")){
             if(result.contains("0")){
                 //这里是没有数据的展示
                 SetViewVisible(View.VISIBLE);
             }
             else if(result.contains("2")){
                 Toast.makeText(getActivity(),"服务器异常",Toast.LENGTH_SHORT).show();
             }
        }else{
            try {
                SetViewVisible(View.GONE);
                dataSet.clear();
                JSONArray array = new JSONArray(result);
                for (int i=0;i<array.length();i++){
                    JSONObject tmpObj = (JSONObject) array.get(i);
                    HashMap<String,String> tmp = new HashMap<String, String>();

                    tmp.put("ticket_id", (String) tmpObj.get("ticket_id"));
                    tmp.put("seller_name", (String) tmpObj.get("seller_name"));
                    tmp.put("activity_name", (String) tmpObj.get("activity_name"));
                    tmp.put("ticket_deadline", (String) tmpObj.get("ticket_deadline"));
                    tmp.put("activity_img", (String) tmpObj.get("activity_img"));

                    dataSet.add(tmp);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private static final int NET_SUCCESS = 0x123;
    private static final int NET_FAILURE = 0x110;
    private <T extends View> T $(View v, int id) {
        return (T) v.findViewById(id);
    }

}
