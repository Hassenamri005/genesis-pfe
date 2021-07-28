package com.example.pfe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyRecyclerViewAdapter3 extends RecyclerView.Adapter<MyRecyclerViewAdapter3.ViewHolder> {

    private List<String> mData;
    private List<String> mDataAction;
    private List<String> mDataDate;
    private List<String> mDataTime;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    MyRecyclerViewAdapter3(Context context, List<String> data, List<String> dataAction, List<String> dataDate, List<String> dataTime) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mDataAction = dataAction;
        this.mDataDate = dataDate;
        this.mDataTime = dataTime;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row3, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String who = mData.get(position);
        String str = who;
        int index = str.indexOf("@");
        String who2 = str.substring(0,index);
        holder.myTextView.setText(who2);
        String action = mDataAction.get(position);
        holder.hostory_content.setText(action);
        String date = mDataDate.get(position);
        holder.history_date.setText(date);
        String time = mDataTime.get(position);
        holder.history_time.setText(time);

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView,hostory_content,history_date,history_time;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.who);
            hostory_content = itemView.findViewById(R.id.hostory_content);
            history_date = itemView.findViewById(R.id.history_date);
            history_time = itemView.findViewById(R.id.history_time);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}