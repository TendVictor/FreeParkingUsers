package com.example.chen.freeparkingusers.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chen.freeparkingusers.R;
import com.example.chen.freeparkingusers.item.SellerInfo;
import com.example.chen.freeparkingusers.net.ImageLoader;

import java.util.ArrayList;

/**
 * Created by chen on 16/7/11.
 */
public class SellerSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private onItemClickListener mOnItemClickListener = null;

    private Context context;
    private ArrayList<SellerInfo> mDatas;

    public SellerSearchAdapter(Context context, ArrayList<SellerInfo> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        myViewHolder holder = new myViewHolder(
                LayoutInflater.from(context).inflate(R.layout.lv_sellercampaign, parent, false));

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((myViewHolder) holder).name.setText(mDatas.get(position).getSellerName());
        ((myViewHolder) holder).place.setText(mDatas.get(position).getSellerAddress());
        ((myViewHolder) holder).contact.setText(mDatas.get(position).getSellerContact());
        ImageLoader.getInstance(context).bindBitmap
                (mDatas.get(position).getSellerImage(),
                        R.drawable.default_img,
                        ((myViewHolder) holder).image);

        ((myViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = position;
                mOnItemClickListener.onItemClick(v, pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    //暴露在外的接口
    public void setOnItemClickListener(SellerSearchAdapter.onItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }


    public interface onItemClickListener {
        void onItemClick(View view, int position);
    }


    class myViewHolder extends RecyclerView.ViewHolder {

        TextView name, place, contact;
        ImageView image;

        public myViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_seller_name);
            place = (TextView) itemView.findViewById(R.id.tv_seller_address);
            contact = (TextView) itemView.findViewById(R.id.tv_seller_contact);
            image = (ImageView) itemView.findViewById(R.id.iv_seller);
        }
    }


}
