package com.example.chen.freeparkingusers.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chen.freeparkingusers.R;
import com.example.chen.freeparkingusers.item.SellerInfo;
import com.example.chen.freeparkingusers.net.ImageLoader;

import java.util.ArrayList;

/**
 * Created by chen on 16/7/6.
 */
public class SellerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private onItemClickListener mOnItemClickListener = null;

    private final int BODY_TYPE = 1;
    private final int FOOT_TYPE = 2;

    private Context context;
    private ArrayList<SellerInfo> mDatas;

    private boolean ableTouch = true;

    public SellerAdapter(Context context, ArrayList<SellerInfo> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case BODY_TYPE:
                holder = new myViewHolder(LayoutInflater
                        .from(context).inflate(R.layout.lv_sellercampaign, parent, false));
                break;
            case FOOT_TYPE:
                holder = new FootViewHolder(
                        LayoutInflater.from(context).inflate(R.layout.foot_load, parent, false));
                break;
        }
        return holder;
    }


    @Override
    public final int getItemCount() {
        return getFootCount() + getItemViewCount();
    }

    public final int getFootCount() {
        return 1;
    }

    public final int getItemViewCount() {
        return mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= getItemCount() - 1)
            return FOOT_TYPE;
        else
            return BODY_TYPE;

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case BODY_TYPE:
                ((myViewHolder) holder).name.setText(mDatas.get(position).getSellerName());
                ((myViewHolder) holder).place.setText(mDatas.get(position).getSellerAddress());
                ((myViewHolder) holder).contact.setText(mDatas.get(position).getSellerContact());
                ImageLoader.getInstance(context).bindBitmap
                        (mDatas.get(position).getSellerImage(),
                                R.drawable.default_img,
                                ((myViewHolder) holder).image);
                ((myViewHolder) holder).distance.setText(changeTypeDistance(mDatas.get(position).getSellerdistance()));

                ((myViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = position;
                        mOnItemClickListener.onItemClick(v, pos);
                    }
                });


                break;
            case FOOT_TYPE:
                if (((FootViewHolder) holder).isNoData) {
                    ((FootViewHolder) holder).tv_foot.setText("没有其他数据");
                    ((FootViewHolder) holder).pb_foot.setVisibility(View.GONE);
                } else {
                    ((FootViewHolder) holder).tv_foot.setText("加载更多");
                    ((FootViewHolder) holder).pb_foot.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private String changeTypeDistance(String distance) {
        String standardDistance = distance.substring(0, 3);
        standardDistance += "km";

        return standardDistance;
    }



    //暴露在外的接口
    public void setOnItemClickListener(SellerAdapter.onItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }


    public interface onItemClickListener {
        void onItemClick(View view, int position);
    }

    class myViewHolder extends RecyclerView.ViewHolder {

        TextView name, place, contact, distance;
        ImageView image;

        public myViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_seller_name);
            place = (TextView) itemView.findViewById(R.id.tv_seller_address);
            contact = (TextView) itemView.findViewById(R.id.tv_seller_contact);
            image = (ImageView) itemView.findViewById(R.id.iv_seller);
            distance = (TextView) itemView.findViewById(R.id.tv_distance);
        }
    }

    public class FootViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_foot;
        public ProgressBar pb_foot;
        public boolean isNoData = false;

        public void setIsHaveData(boolean isNoData) {
            this.isNoData = isNoData;
        }

        public FootViewHolder(View itemView) {
            super(itemView);
            tv_foot = (TextView) itemView.findViewById(R.id.tv_loadmore);
            pb_foot = (ProgressBar) itemView.findViewById(R.id.pb_foot_loadmore);

        }
    }
}
