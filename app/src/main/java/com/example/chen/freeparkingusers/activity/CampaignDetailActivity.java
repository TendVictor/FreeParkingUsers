package com.example.chen.freeparkingusers.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chen.freeparkingusers.R;
import com.example.chen.freeparkingusers.item.CampaignInfo;
import com.example.chen.freeparkingusers.net.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Pants on 2016/7/5.
 */
public class CampaignDetailActivity extends Activity {

    private CampaignInfo campaignInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_detail);

        campaignInfo = getIntent().getParcelableExtra("campaign_info");

        ((TextView) findViewById(R.id.tvCampaignName)).setText(campaignInfo.getCampaignName());
        ((TextView) findViewById(R.id.tvCampaignTime))
                .setText(getInterval(campaignInfo.getCampaignStartTime(), campaignInfo.getCampaignEndTime()));
        ((TextView) findViewById(R.id.tvCampaignDetail)).setText(campaignInfo.getCampaignDetail());
        ImageLoader.getInstance(this).bindBitmap(campaignInfo.getCampaignImage(),
                R.drawable.default_img, (ImageView) findViewById(R.id.ivCampaignImage));
    }

    private String getInterval(String campaignStartTime, String campaignEndTime) {
        SimpleDateFormat formatIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatOut = new SimpleDateFormat("yyyy.MM.dd");

        try {
            Date startDatetime = formatIn.parse(campaignStartTime);
            Date endDatetime = formatIn.parse(campaignEndTime);
            String startDate = formatOut.format(startDatetime);
            String endDate = formatOut.format(endDatetime);
            if (startDate.substring(0, 3).contentEquals(endDate.substring(0, 3)))
                return startDate.concat("-").concat(endDate.substring(5));
            else
                return startDate.concat("-").concat(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btnPrevious) {
            finish();
        }
    }
}
