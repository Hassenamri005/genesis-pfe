package com.example.pfe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;
import java.util.List;

public class MyRecyclerViewAdapterActPrises extends RecyclerView.Adapter<MyRecyclerViewAdapterActPrises.ViewHolder>  {

    private List<String> mData,data2,data3;
    static String myId;
    static String loadsEmail;
    private LayoutInflater mInflater;
    private MyRecyclerViewAdapterActPrises.ItemClickListener mClickListener;
    static boolean swStatus;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    static int i=-1;

    // data is passed into the constructor
    MyRecyclerViewAdapterActPrises(Context context, List<String> data, List<String> data2, List<String> data3, String loadsEmail) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.data2 =data2;
        this.data3 = data3;
        this.loadsEmail = loadsEmail;
    }


    // inflates the row layout from xml when needed
    @Override
    public MyRecyclerViewAdapterActPrises.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_rowactprises, parent, false);
        return new MyRecyclerViewAdapterActPrises.ViewHolder(view);
    }
    // binds the data to the TextView in each row
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(MyRecyclerViewAdapterActPrises.ViewHolder holder, int position) {
        String animal = mData.get(position);
        holder.myTextView.setText(animal);
        String status = data2.get(position);
        if (data3.get(position).equals("true")){
            holder.sw.setChecked(true);

        }else{
            holder.sw.setChecked(false);

        }
        holder.sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chambre = mData.get(position);
                boolean checked = holder.sw.isChecked();
                String x = loadsEmail;
                loadIdFromEmail(x,position,checked);

            }
        });
    }

    private void loadIdFromEmail(String loadsEmail,int position, boolean checked) {

        myRef = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = myRef.orderByKey();
        query.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                        Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                        while (iterator.hasNext()) {
                            DataSnapshot next = (DataSnapshot) iterator.next();
                            String key = next.getKey();
                            String value = String.valueOf(next.child("email").getValue());
                            if (loadsEmail.equals(value)){
                                myId = key;
                                Log.i("your email ",myId);

                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }

                });
        String y = myId;
        changeValue(position,checked,y);
    }

    private void changeValue(int position, boolean checked,String myId) {

        String path = "users/"+myId+"/Prises";
        myRef = FirebaseDatabase.getInstance().getReference(path);
        Query query = myRef.orderByKey();
        query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                        Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                        while (iterator.hasNext()) {
                            DataSnapshot next = (DataSnapshot) iterator.next();
                            String key = next.getKey();
                            if (key.equals(String.valueOf(position))){
                                if (checked==true){
                                    myRef.child(key).child("access").setValue("true");
                                }else{
                                    myRef.child(key).child("access").setValue("false");
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
    }


    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        Switch sw;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.tvAnimalName);
            sw = itemView.findViewById(R.id.switch2);
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
    void setClickListener(MyRecyclerViewAdapterActPrises.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}