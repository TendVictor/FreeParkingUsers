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
import android.widget.TextView;
import android.widget.Toast;

import com.example.chen.freeparkingusers.R;
import com.example.chen.freeparkingusers.activity.LoginActivity;
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
 * Created by ran on 16/7/4.
 */

public class ticketFragment extends BaseFragment {

    public static final String TAG = "TicFrg";

    private SwipeRefreshLayout swipeRefreshLayout = null;
    private RecyclerView recyclerView = null;

    private ArrayList<HashMap<String, String>> dataSet = null;
    private TicketAdapter ticketAdapter = null;

    private LinearLayout llContainer = null;

    //要求登录与没有数据相关页面
    private Button btnRefresh = null;
    private TextView tvNodata = null;

    private MyScrollListener mScrollListener;

    private View rootView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle SavedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_ticket, null, false);
        initData();
        initVariables();
        initViewAndEvents(rootView);
        FetchTicketData();
        return rootView;
    }

    private void initData() {
        dataSet = new ArrayList<HashMap<String, String>>();
    }

    private void initVariables() {
        ticketAdapter = new TicketAdapter(getActivity(), dataSet);
        ticketAdapter.setOnItemClickListener(new TicketAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //传递数据
                Intent i = new Intent(getActivity(), QRCodeActivity.class);
                i.putExtra("ticket_id", dataSet.get(position).get("ticket_id"));
                i.putExtra("seller_name", dataSet.get(position).get("seller_name"));
                i.putExtra("ticket_deadline", dataSet.get(position).get("ticket_deadline"));
                startActivity(i);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Log.e(TAG, "Long click from TicFrg");
            }
        });
    }

    private void initViewAndEvents(View view) {

        btnRefresh = $(view, R.id.btn_refresh);
        tvNodata = $(view, R.id.tv_NoData);

        if (Config.username == null) {
            btnRefresh.setText("点我登录");
            tvNodata.setText("登录后才可以查看停车券哦");
        }

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Config.username == null) {
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    startActivity(i);
                } else {
                    FetchTicketData();
                }
            }
        });

        llContainer = $(view, R.id.ll_nodata_container);

        recyclerView = $(view, R.id.rv_ticket);
        swipeRefreshLayout = $(view, R.id.sl_ticket);
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#2CBEC5"));

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(ticketAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FetchTicketData();
            }
        });

        mScrollListener = new MyScrollListener();
        recyclerView.addOnScrollListener(mScrollListener);
    }

    public static final int LOADMORE = 0x120;

    public void RefreshTicketInfo() {

        FetchTicketData();

    }

    private void FetchTicketData() {
        if (Config.username == null) {
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
                ChangeViewState(View.VISIBLE);
            }
            //对界面做处理 改成要去登陆的界面
        } else {
            new NetPostConnection(Config.URL_GET_ALLTICKETS, new NetPostConnection.SuccessCallback() {
                @Override
                public void onSuccess(String result) throws JSONException {
                    Message msg = new Message();
                    msg.obj = result;
                    msg.what = NET_SUCCESS;
                    handler.sendMessage(msg);
                }
            }, new NetPostConnection.FailCallback() {
                @Override
                public void onFail() {
                    handler.sendEmptyMessage(NET_FAILURE);
                }
            }, new Object[]{
                    "user_id", Config.username
            });
        }

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NET_SUCCESS:

                    if (swipeRefreshLayout != null)
                        swipeRefreshLayout.setRefreshing(false);

                    String result = (String) msg.obj;

                    if (parseJsonInfo(result)) {

                        ticketAdapter.notifyDataSetChanged();

                        mScrollListener.setHasMore(true);

                        LinearLayoutManager llLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                        if (llLayoutManager.findFirstVisibleItemPosition() <= 0
                                && llLayoutManager.findLastCompletelyVisibleItemPosition() >= ticketAdapter.getItemCount() - 1) {
                            mScrollListener.setHasMore(false);
                            mScrollListener.resetFootView();
                        }
                    }

                    break;
                case NET_FAILURE:

                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), "网络错误,请检查", Toast.LENGTH_SHORT).show();

                    break;
                case LOADMORE:

                    mScrollListener.setHasMore(false);

                    if (mScrollListener.isLoadingMore()) {
                        mScrollListener.resetFootView();
                        mScrollListener.setLoadingMore(false);
                    }

                    break;
            }
        }
    };

    private void ChangeViewState(int state) {

        if (state == View.GONE) {
            llContainer.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);

        } else {
            swipeRefreshLayout.setVisibility(View.GONE);
            llContainer.setVisibility(View.VISIBLE);

            if (Config.username == null) {

                btnRefresh.setVisibility(View.VISIBLE);
                btnRefresh.setText("点我登录");
                tvNodata.setText("登录后才可以查看停车券哦");

            } else {
                btnRefresh.setVisibility(View.GONE);
                tvNodata.setText("暂时没有停车券,快去参加活动吧");
            }

        }
    }

    private boolean parseJsonInfo(String result) {
        if (result.contains("\"state\":")) {
            if (result.contains("0")) {
                //这里是没有数据的展示
                ChangeViewState(View.VISIBLE);
            } else if (result.contains("2")) {
                Toast.makeText(getActivity(), "服务器异常", Toast.LENGTH_SHORT).show();
            }
            return false;
        } else {
            try {

                ChangeViewState(View.GONE);

                dataSet.clear();

                JSONArray array = new JSONArray(result);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject tmpObj = (JSONObject) array.get(i);
                    HashMap<String, String> tmp = new HashMap<String, String>();

                    tmp.put("ticket_id", (String) tmpObj.get("ticket_id"));
                    tmp.put("seller_name", (String) tmpObj.get("seller_name"));
                    tmp.put("activity_name", (String) tmpObj.get("activity_name"));
                    tmp.put("ticket_deadline", (String) tmpObj.get("ticket_deadline"));
                    tmp.put("activity_img", (String) tmpObj.get("activity_img"));

                    dataSet.add(tmp);
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static final int NET_SUCCESS = 0x123;
    private static final int NET_FAILURE = 0x110;

    private <T extends View> T $(View v, int id) {
        return (T) v.findViewById(id);
    }


    class MyScrollListener extends RecyclerView.OnScrollListener {

        int lastVisibleItemPosition;
        boolean isLoadingMore = false;
        boolean hasMore = true;


        public boolean isLoadingMore() {
            return isLoadingMore;
        }

        public void setLoadingMore(boolean loadingMore) {
            isLoadingMore = loadingMore;
        }

        public void setHasMore(boolean hasMore) {
            this.hasMore = hasMore;
        }

        public void resetFootView() {
            if (hasMore) {
                TicketAdapter.FootViewHolder fvh = (TicketAdapter.FootViewHolder) recyclerView.findViewHolderForLayoutPosition(ticketAdapter.getItemCount() - 1);
                //界面展示
                fvh.tv.setText("上拉加载");
                fvh.pb.setVisibility(View.GONE);
            } else {
                TicketAdapter.FootViewHolder fvh = (TicketAdapter.FootViewHolder) recyclerView.findViewHolderForLayoutPosition(ticketAdapter.getItemCount() - 1);
                //界面展示
                fvh.tv.setText("没有更多数据了");
                fvh.pb.setVisibility(View.GONE);
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

//            resetFootView();

            if (hasMore && newState == RecyclerView.SCROLL_STATE_IDLE && ticketAdapter.getItemCount() != 1 &&
                    lastVisibleItemPosition == ticketAdapter.getItemCount() - 1) {
                if (!isLoadingMore) {

                    isLoadingMore = true;
                    TicketAdapter.FootViewHolder fvh = (TicketAdapter.FootViewHolder) recyclerView.findViewHolderForLayoutPosition(lastVisibleItemPosition);
                    //界面展示
                    fvh.tv.setText("加载更多中=。=");
                    fvh.pb.setVisibility(View.VISIBLE);

                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                sleep(3000);
                                handler.sendEmptyMessage(LOADMORE);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
            if (lastVisibleItemPosition >= ticketAdapter.getItemCount() - 1)
                resetFootView();
            else {
                isLoadingMore = false;
            }
        }
    }
}
