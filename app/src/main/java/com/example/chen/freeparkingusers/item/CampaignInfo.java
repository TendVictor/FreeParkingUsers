package com.example.chen.freeparkingusers.item;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Pants on 2016/7/6.
 */
public class CampaignInfo implements Parcelable {
    private int campaignId;
    private String campaignImage;
    private String campaignName;
    private String campaignStartTime;
    private String campaignEndTime;
    private String campaignDetail;

    public int getCampaignId() {
        return campaignId;
    }

    public String getCampaignImage() {
        return campaignImage;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public String getCampaignStartTime() {
        return campaignStartTime;
    }

    public String getCampaignEndTime() {
        return campaignEndTime;
    }

    public String getCampaignDetail() {
        return campaignDetail;
    }

    public CampaignInfo(int campaignId, String campaignImage, String campaignName, String campaignStartTime, String campaignEndTime, String campaignDetail) {
        this.campaignId = campaignId;
        this.campaignImage = campaignImage;
        this.campaignName = campaignName;
        this.campaignStartTime = campaignStartTime;
        this.campaignEndTime = campaignEndTime;
        this.campaignDetail = campaignDetail;
    }


    protected CampaignInfo(Parcel in) {
        campaignId = in.readInt();
        campaignImage = in.readString();
        campaignName = in.readString();
        campaignStartTime = in.readString();
        campaignEndTime = in.readString();
        campaignDetail = in.readString();
    }

    public static final Creator<CampaignInfo> CREATOR = new Creator<CampaignInfo>() {
        @Override
        public CampaignInfo createFromParcel(Parcel in) {
            return new CampaignInfo(in);
        }

        @Override
        public CampaignInfo[] newArray(int size) {
            return new CampaignInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(campaignId);
        dest.writeString(campaignImage);
        dest.writeString(campaignName);
        dest.writeString(campaignStartTime);
        dest.writeString(campaignEndTime);
        dest.writeString(campaignDetail);
    }
}
