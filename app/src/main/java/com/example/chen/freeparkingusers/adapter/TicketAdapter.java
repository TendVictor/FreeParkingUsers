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
import com.example.chen.freeparkingusers.net.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 2016/7/6 0006.
 */
public class TicketAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOT = 1;

    private Context context;
    private ArrayList<HashMap<String,String>> dataSet;

    public TicketAdapter(Context context, ArrayList<HashMap<String, String>> dataSet) {
        this.context = context;
        this.dataSet = dataSet;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_ITEM){
            View view = LayoutInflater.from(context).inflate(R.layout.layout_ticket_item,parent,false);
            ItemViewHolder vh = new ItemViewHolder(view);
            return vh;
        }else if(viewType == TYPE_FOOT){
            View view = LayoutInflater.from(context).inflate(R.layout.layout_foot_view,parent,false);
            FootViewHolder fvh = new FootViewHolder(view);
            return fvh;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
         if(getItemViewType(position) == TYPE_ITEM && holder instanceof ItemViewHolder){
             //设置显示数据集
             HashMap<String,String> tmp = dataSet.get(position);
             ((ItemViewHolder)holder).tv_name.setText(tmp.get("activity_name"));
             ((ItemViewHolder)holder).tv_address.setText(tmp.get("seller_name"));
             ((ItemViewHolder)holder).tv_deadline.setText("使用日期 "+tmp.get("ticket_deadline"));
             String img_url = tmp.get("activity_img");
             ImageLoader.getInstance(context).bindBitmap(img_url,R.drawable.default_img,((ItemViewHolder)holder).iv_activity);

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

    }

    @Override
    public int getItemViewType(int position) {
        if(position == getItemCount()-1) return TYPE_FOOT;
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return dataSet.size()+1;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder
    {
        View itemView;
        ImageView iv_activity;
        TextView tv_address;
        TextView tv_name;
        TextView tv_deadline;
        public ItemViewHolder(View view)
        {
            super(view);
            itemView = view;
            iv_activity = $(view,R.id.iv_activity);
            tv_address = $(view,R.id.tv_address);
            tv_name = $(view,R.id.tv_name);
            tv_deadline = $(view,R.id.tv_deadline);
        }
    }
    public class FootViewHolder extends RecyclerView.ViewHolder{

        View itemView = null;
        public TextView tv;
        public ProgressBar pb;
        public FootViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.tv = $(itemView,R.id.tv);
            this.pb = $(itemView,R.id.pb);
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
