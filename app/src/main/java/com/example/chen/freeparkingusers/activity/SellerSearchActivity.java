package com.example.chen.freeparkingusers.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

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
 * Created by chen on 16/7/6.
 */
public class SellerSearchActivity extends Activity implements View.OnClickListener{

    private int number_limit = 0;

    private EditText searchEdit;
    private ImageView backBtn;


    private SwipeRefreshLayout searchSwipeLayout;
    private RecyclerView searchRecyclerView;

    private List<SellerInfo> searchDatas;
    private SellerAdapter searchSellerAdapter;

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0x0:
                    number_limit+=10;
                    searchSellerAdapter.notifyDataSetChanged();
                    break;
                case 0x1:
                    break;
            }
            searchSwipeLayout.setRefreshing(false);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sellersearch);
        initView();
    }


    //初始化View
    private void initView() {
        searchEdit = (EditText) findViewById(R.id.ev_searchseller);
        backBtn = (ImageView) findViewById(R.id.iv_back);


        backBtn.setOnClickListener(this);
        searchEdit.setOnClickListener(this);

//        searchEdit.setOn

        searchSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_search);
        searchRecyclerView = (RecyclerView) findViewById(R.id.rv_search);

        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchSwipeLayout.setOnRefreshListener(new onRefreshListener());

        searchDatas = new ArrayList<>();
        getDatas();

        searchSellerAdapter = new SellerAdapter(this, (ArrayList<SellerInfo>) searchDatas);
        searchRecyclerView.setAdapter(searchSellerAdapter);
    }

    private void getDatas(){

        for(int i = 0; i < 30 ; i++){
            SellerInfo s = new SellerInfo(i+"","name"+i,"SellerImg","the place:"+i , "the contact:" +i);
            searchDatas.add(s);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.ev_searchseller:
//                startSearchSeller();
                break;
        }
    }


    //接口对接，搜搜
    private void startSearchSeller(String search_word, int number_limit) {
        NetPostConnection connection = new NetPostConnection(Config.URL_SELLER_SEARCH,
                new NetPostConnection.SuccessCallback() {
                    @Override
                    public void onSuccess(String result) throws JSONException {
                        JSONArray jsonArray = new JSONArray(result);
                        searchDatas.clear();
                        for (int i = 0; i < jsonArray.length(); i++){
                            JSONObject object = (JSONObject) jsonArray.getJSONObject(i);
                            SellerInfo info = new SellerInfo(
                                    object.getString("seller_id"),
                                    object.getString("seller_name"),
                                    object.getString("seller_img"),
                                    object.getString("seller_address"),
                                    object.getString("seller_contact"));
                            searchDatas.add(info);
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


    class onRefreshListener implements SwipeRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh() {

        }
    }

}
