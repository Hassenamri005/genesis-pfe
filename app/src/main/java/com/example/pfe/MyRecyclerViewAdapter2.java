package com.example.pfe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MyRecyclerViewAdapter2 extends RecyclerView.Adapter<MyRecyclerViewAdapter2.ViewHolder> {
    FirebaseDatabase database;
    DatabaseReference myRef;
    Switch swAdmin;
    private List<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    MyRecyclerViewAdapter2(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row2, parent, false);

        return new ViewHolder(view);
    }

    private void switchAdminValueInFirebase(boolean b) {
        myRef = database.getReference("/users");
        if (b==true){
            //myRef.child("role").setValue("admin");
            swAdmin.setText("YES");
        }else {
           // myRef.child("role").setValue("user");
            swAdmin.setText("NO");
        }
    }



    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String user = mData.get(position);
        holder.myTextView.setText(user);
        String str = user;
        if (user.isEmpty()==false){
            int index = str.indexOf("@");
            String who2 = str.substring(0,index);
            holder.myTextView2.setText(who2);
        }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView,myTextView2;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.tvUserName);
            myTextView2 = itemView.findViewById(R.id.textView5);
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