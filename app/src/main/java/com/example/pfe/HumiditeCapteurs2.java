package com.example.pfe;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;

public class HumiditeCapteurs2 extends AppCompatActivity implements MyRecyclerViewAdapter5.ItemClickListener {

    Timer timer = new Timer();
    MyRecyclerViewAdapter1 adapter;
    ArrayList<String> animalNames,humNames;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    RecyclerView recyclerView;
    private ProgressBar pbTemp, pbHum, pbGas;
    private Handler handler = new Handler();
    private TextView temp, hum;
    int progressStatus = 0, progressStatus2 = 0, progressStatus3 = 0;
    private com.google.firebase.database.DatabaseReference myReff;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_humidite_capteurs2);


        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.myyellow)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Humidité");
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        pbTemp = (ProgressBar) findViewById(R.id.pbTemp);
        temp = (TextView) findViewById(R.id.temp);
        myRef = database.getReference("/actionneurs/dht11");

        // data to populate the RecyclerView with
        animalNames = new ArrayList<>();
        humNames = new ArrayList<>();
        //animalNames.add("Horse");
        getTempFirebaseValue();

        getUserInfo();
        getUserInfo2();

        // set up the RecyclerView
        recyclerView = findViewById(R.id.rvAnimals2);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyRecyclerViewAdapter1(this, animalNames, animalNames, humNames);
        adapter.setClickListener(this::onItemClick);
        recyclerView.setAdapter(adapter);

    }




    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    private void animateProgressBarTemperature(int x) {
        if (progressStatus == 100) {
            progressStatus = 0;
        }
        if (progressStatus<x){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (progressStatus < x+1) {
                        // Update the progress status
                        progressStatus += 1;
                        // Try to sleep the thread for 20 milliseconds
                        try {
                            Thread.sleep(20);  //3 seconds
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // Update the progress bar
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                pbTemp.setProgress(progressStatus);
                            }
                        });
                    }
                }
            }).start(); // Start the operation
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (progressStatus > x) {
                        // Update the progress status
                        progressStatus -= 1;
                        // Try to sleep the thread for 20 milliseconds
                        try {
                            Thread.sleep(20);  //3 seconds
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // Update the progress bar
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                pbTemp.setProgress(progressStatus);
                            }
                        });
                    }
                }
            }).start(); // Start the operation
        }

    }
    private void getTempFirebaseValue() {
        myReff = FirebaseDatabase.getInstance().getReference().child("Temp");
        // User data change listener
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                String tp = dataSnapshot.getValue().toString();
                int x = Integer.parseInt(tp);
                animateProgressBarTemperature(x);
                temp.setText(tp);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        };
        myReff.addValueEventListener(postListener);
    }

    public void getUserInfo2(){
        // Read from the database
        Query query = myRef.orderByKey();
        query.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                        Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                        while (iterator.hasNext()) {
                            DataSnapshot next = (DataSnapshot) iterator.next();
                            String value = String.valueOf(next.child("").child("chambre").getValue());
                            int insertIndex = 0;
                            humNames.add(insertIndex, value);
                            adapter.notifyItemInserted(insertIndex);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
        //Toast.makeText(getContext(),"Done ",Toast.LENGTH_LONG).show();
    }

    public void getUserInfo(){
        // Read from the database
        Query query = myRef.orderByKey();
        query.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                        Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                        while (iterator.hasNext()) {
                            DataSnapshot next = (DataSnapshot) iterator.next();
                            String value = String.valueOf(next.child("").child("status").getValue());
                            int insertIndex = 0;
                            animalNames.add(insertIndex, value);
                            adapter.notifyItemInserted(insertIndex);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
        //Toast.makeText(getContext(),"Done ",Toast.LENGTH_LONG).show();
    }

}