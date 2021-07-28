package com.example.pfe;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyRecyclerViewAdapter1 extends RecyclerView.Adapter<MyRecyclerViewAdapter1.ViewHolder> {

    private List<String> mData;
    private List<String> mData2;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    int progressStatus = 0, progressStatus2 = 0, progressStatus3 = 0;
    private Handler handler = new Handler();

    // data is passed into the constructor
    MyRecyclerViewAdapter1(Context context, ArrayList<String> animalNames, List<String> data, List<String> data2) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mData2 = data2;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row001, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String animal = mData.get(position);
        holder.myTextView.setText(animal);
        String chambre = mData2.get(position);
        holder.chambre.setText(chambre);
        int x = Integer.parseInt(animal);
        holder.pbTemp.setProgress(x);
    }


    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }




    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView,chambre;
        ProgressBar pbTemp;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.tvAnimalName);
            chambre = itemView.findViewById(R.id.chambre);
            pbTemp = itemView.findViewById(R.id.pbTemp);
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