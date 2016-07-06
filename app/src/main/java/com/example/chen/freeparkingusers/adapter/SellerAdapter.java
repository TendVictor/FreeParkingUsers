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

import java.util.ArrayList;

/**
 * Created by chen on 16/7/6.
 */
public class SellerAdapter extends RecyclerView.Adapter<SellerAdapter.myViewHolder>{

    private Context context;
    private ArrayList<SellerInfo> mDatas;

    public SellerAdapter(Context context, ArrayList<SellerInfo> mDatas){
        this.context = context;
        this.mDatas = mDatas;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        myViewHolder holder = new myViewHolder(LayoutInflater
                .from(context).inflate(R.layout.lv_sellercampaign,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        holder.name.setText(mDatas.get(position).getSellerName());
        holder.place.setText(mDatas.get(position).getSellerAddress());
        holder.contact.setText(mDatas.get(position).getSellerContact());
        holder.image.setImageResource(R.drawable.iv_seller_default);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder{

        TextView name,place,contact;
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
