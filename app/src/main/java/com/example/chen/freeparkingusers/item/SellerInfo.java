package com.example.chen.freeparkingusers.item;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Pants on 2016/7/6.
 */
public class SellerInfo implements Parcelable {
    private String sellerId;
    private String sellerName;
    private String sellerImg;
    private String sellerAddress;
    private String sellerContact;
    private String sellerdistance;

    public String getSellerdistance() {
        return sellerdistance;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getSellerImage() {
        return sellerImg;
    }

    public String getSellerAddress() {
        return sellerAddress;
    }

    public String getSellerContact() {
        return sellerContact;
    }


    public SellerInfo(String sellerId, String sellerName,
                      String sellerImg, String sellerAddress, String sellerContact,String sellerDistance) {
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.sellerImg = sellerImg;
        this.sellerAddress = sellerAddress;
        this.sellerContact = sellerContact;
        this.sellerdistance = sellerDistance;
    }

    protected SellerInfo(Parcel in) {
        sellerId = in.readString();
        sellerName = in.readString();
        sellerImg = in.readString();
        sellerAddress = in.readString();
        sellerContact = in.readString();
        sellerdistance = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sellerId);
        dest.writeString(sellerName);
        dest.writeString(sellerImg);
        dest.writeString(sellerAddress);
        dest.writeString(sellerContact);
    }

    public static final Creator<SellerInfo> CREATOR = new Creator<SellerInfo>() {
        @Override
        public SellerInfo createFromParcel(Parcel in) {
            return new SellerInfo(in);
        }

        @Override
        public SellerInfo[] newArray(int size) {
            return new SellerInfo[size];
        }
    };
}
