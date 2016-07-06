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
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tv_name.setText("name");
        holder.tv_address.setText("address");
        holder.tv_deadline.setText("deadline");
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView iv_activity;
        TextView tv_address;
        TextView tv_name;
        TextView tv_deadline;
        public MyViewHolder(View view)
        {
            super(view);

            iv_activity = $(view,R.id.iv_activity);
            tv_address = $(view,R.id.tv_address);
            tv_name = $(view,R.id.tv_name);
            tv_deadline = $(view,R.id.tv_deadline);
        }
    }
    private <T extends View> T $(View v, int id) {
        return (T) v.findViewById(id);
    }
}
