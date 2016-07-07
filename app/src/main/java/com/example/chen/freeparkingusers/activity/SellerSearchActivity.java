package com.example.chen.freeparkingusers.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.chen.freeparkingusers.R;
import com.example.chen.freeparkingusers.adapter.SellerAdapter;
import com.example.chen.freeparkingusers.item.SellerInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen on 16/7/6.
 */
public class SellerSearchActivity extends Activity implements View.OnClickListener{

    private EditText searchEdit;
    private ImageView backBtn;


    private SwipeRefreshLayout searchSwipeLayout;
    private RecyclerView searchRecyclerView;

    private List<SellerInfo> searchDatas;
    private SellerAdapter searchSellerAdapter;


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
                startSearchSeller();
                break;
        }
    }


    //接口对接，搜搜
    private void startSearchSeller() {

    }


    class onRefreshListener implements SwipeRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh() {

        }
    }

}
