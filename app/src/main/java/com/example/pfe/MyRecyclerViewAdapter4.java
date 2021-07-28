package com.example.pfe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;
import java.util.List;

import static com.example.pfe.Leds.status;
import static com.example.pfe.MainActivity.actionneursLedsAcces;
import static com.example.pfe.MainActivity.actionneursPrisesAcces;
import static java.security.AccessController.getContext;

public class MyRecyclerViewAdapter4 extends RecyclerView.Adapter<MyRecyclerViewAdapter4.ViewHolder> {

    private List<String> mData,data2;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    static boolean swStatus;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    static int i=-1;

    // data is passed into the constructor
    MyRecyclerViewAdapter4(Context context, List<String> data, List<String> data2) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.data2 =data2;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_leds, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String animal = mData.get(position);
        holder.myTextView.setText(animal);
        String status = data2.get(position);
        if (status.contains("on")){
            holder.sw.setChecked(true);

        }else{
            holder.sw.setChecked(false);

        }
        holder.sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chambre = mData.get(position);
                boolean checked = holder.sw.isChecked();
                changeValue(position,chambre,checked);
            }
        });
    }

    private void changeValue(int position, String chambre, boolean checked) {
        myRef = FirebaseDatabase.getInstance().getReference().child("actionneurs/leds");
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
                            i++;
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("/actionneurs/leds");
                            myRef.child(key).child("status").setValue("on");
                            String chmbr = String.valueOf(next.child("chambre").getValue());

                            if (chmbr.toLowerCase().equals(chambre.toLowerCase())){
                                myRef.child(key).child("status").setValue("on");

                                if (actionneursLedsAcces.get(position).equals("true")){
                                    if (checked==true){
                                        myRef.child(key).child("status").setValue("on");
                                        Log.i("chambreeee","onnnnnnnnn");
                                    }else{
                                        myRef.child(key).child("status").setValue("off");
                                        Log.i("chambreeee","offffffff");
                                    }
                                }else{
                                    Log.i("warning","you don't have access");
                                }

                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
        i=-1;
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
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}