package com.example.chen.freeparkingusers.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chen.freeparkingusers.R;
import com.example.chen.freeparkingusers.item.CampaignInfo;
import com.example.chen.freeparkingusers.item.SellerInfo;
import com.example.chen.freeparkingusers.net.Config;
import com.example.chen.freeparkingusers.net.NetPostConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Pants on 2016/7/5.
 */
public class SellerDetailActivity extends Activity {

    private LinearLayout layoutContent;

    private SellerInfo sellerInfo;
    private List<CampaignInfo> campaignInfoList;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x1) {
                AlertDialog dialog = new AlertDialog.Builder(getBaseContext())
                        .setTitle("提示").setMessage("获取商家活动信息失败")
                        .setNeutralButton("回到主页面", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setPositiveButton("重新获取", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                updateCampaignInfo(sellerInfo.getSellerId());
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();
            } else if (msg.what == 0x0) {
                updateCampaignUI();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_detail);

        layoutContent = (LinearLayout) findViewById(R.id.layoutContent);

        sellerInfo = getIntent().getParcelableExtra("seller_info");

        ((TextView) findViewById(R.id.tvSellerName)).setText(sellerInfo.getSellerName());
        ((TextView) findViewById(R.id.tvSellerAddress)).setText(sellerInfo.getSellerAddress());
        ((TextView) findViewById(R.id.tvSellerContact)).setText(sellerInfo.getSellerContact());
//        ImageLoader imageLoader = ImageLoader.build();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (campaignInfoList == null)
            updateCampaignInfo(sellerInfo.getSellerId());
    }

    private void updateCampaignInfo(String sellerId) {
        new NetPostConnection(Config.URL_USER_GET_SELLER_INFO,
                new NetPostConnection.SuccessCallback() {
                    @Override
                    public void onSuccess(String result) throws JSONException {
                        JSONArray jsonArray = new JSONArray(result);
                        int count = jsonArray.length();
                        campaignInfoList = new ArrayList<>(count);
                        for (int i = 0; i < count; i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            CampaignInfo info = new CampaignInfo(
                                    jsonObject.getInt("activity_id"),
                                    jsonObject.getString("activity_img"),
                                    jsonObject.getString("activity_name"),
                                    jsonObject.getString("activity_endtime"),
                                    jsonObject.getString("activity_starttime"),
                                    jsonObject.getString("activity_detail")
                            );
                            campaignInfoList.add(info);
                        }
                        mHandler.obtainMessage(0x0).sendToTarget();
                    }
                },
                new NetPostConnection.FailCallback() {
                    @Override
                    public void onFail() {
                        mHandler.obtainMessage(0x1).sendToTarget();
                    }
                }, "seller_id", sellerId);
    }

    private void updateCampaignUI() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.line_height_larger));
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params2.bottomMargin = 16;

        int size = campaignInfoList.size();
        if (size == 0) {
            View view = View.inflate(this, R.layout.layout_campaign_none_tip, null);
            layoutContent.addView(view, params);
        } else {
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CampaignInfo info = (CampaignInfo) v.getTag(R.id.tag_campaign_info);
                    Intent intent = new Intent(SellerDetailActivity.this, CampaignDetailActivity.class);
                    intent.putExtra("campaign_info", info);
                    startActivity(intent);
                }
            };

            for (int i = 0; i < size; i++) {
                CampaignInfo info = campaignInfoList.get(i);
                View view = View.inflate(this, R.layout.layout_campaign_item, null);
                ((TextView) view.findViewById(R.id.tvCampaignNumber))
                        .setText("活动" + getChineseNumber(i + 1));
                ((TextView) view.findViewById(R.id.tvCampaignName))
                        .setText(info.getCampaignName());
                ((TextView) view.findViewById(R.id.tvCampaignDetail))
                        .setText(info.getCampaignDetail());
                layoutContent.addView(view, params2);

                view.setTag(R.id.tag_campaign_info, campaignInfoList.get(i));
                view.setOnClickListener(onClickListener);
            }
        }
    }

    // assert (i<100);
    private String getChineseNumber(int i) {
        char[] nums = new char[]{
                '一', '二', '三', '四', '五', '六', '七', '八', '九', '十'
        };
        StringBuilder sb = new StringBuilder();
        int a = i / 10;
        int b = i % 10;
        if (a >= 2)
            sb.append(nums[a - 1]);
        if (a >= 1)
            sb.append(nums[9]);
        if (b != 0)
            sb.append(nums[b - 1]);
        return sb.toString();
    }

    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btnPrevious) {
            finish();
        }
    }
}
