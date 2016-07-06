package com.example.chen.freeparkingusers.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chen.freeparkingusers.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 2016/7/6 0006.
 */
public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<HashMap<String,String>> dataSet;

    public TicketAdapter(Context context, ArrayList<HashMap<String, String>> dataSet) {
        this.context = context;
        this.dataSet = dataSet;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_ticket_item,parent,false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

         //设置显示数据集


         //添加时间监听事件
        if(onItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(v,pos);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemLongClick(v,pos);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        View itemView;
        ImageView iv_activity;
        TextView tv_address;
        TextView tv_name;
        TextView tv_deadline;
        public MyViewHolder(View view)
        {
            super(view);
            itemView = view;
            iv_activity = $(view,R.id.iv_activity);
            tv_address = $(view,R.id.tv_address);
            tv_name = $(view,R.id.tv_name);
            tv_deadline = $(view,R.id.tv_deadline);
        }
    }

    private onItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(TicketAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface onItemClickListener{
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
    }

    private <T extends View> T $(View v, int id) {
        return (T) v.findViewById(id);
    }
}
