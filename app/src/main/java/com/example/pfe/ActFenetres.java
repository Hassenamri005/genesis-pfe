package com.example.pfe;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class ActFenetres extends AppCompatActivity implements MyRecyclerViewAdapterActFenetre.ItemClickListener {
    MyRecyclerViewAdapterActFenetre adapter;
        ArrayList<String> ledsNames,statusLeds,myprisesaccess,myprisesaccessid,myIdList;
        private FirebaseAuth mAuth;
        private FirebaseDatabase database;
        private com.google.firebase.database.DatabaseReference myRef;
        public static boolean status = false;
        static int i=-1;
        static String loadsEmail;
        private FirebaseDatabase mFirebaseDatabase;
        private com.google.firebase.database.DatabaseReference myReff;
        private com.google.firebase.database.DatabaseReference myRefff;
        private com.google.firebase.database.DatabaseReference myReffff;
        private com.google.firebase.database.DatabaseReference DatabaseReference;
        static String myId;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_fenetres);


        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.myyellow)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Window");
        Intent intent=getIntent();
        loadsEmail = intent.getStringExtra("loadsEmail");
        loadIdFromEmail(loadsEmail);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("/actionneurs/fenetre");

        // data to populate the RecyclerView with
        ledsNames = new ArrayList<>();
        statusLeds = new ArrayList<>();
        myprisesaccess = new ArrayList<>();
        myprisesaccessid = new ArrayList<>();


        //getValue();
        //animalNames.add("Horse");

        checkAccess();

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvAnimals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapterActFenetre(this, ledsNames,statusLeds,myprisesaccess,loadsEmail);
        adapter.setClickListener((MyRecyclerViewAdapterActFenetre.ItemClickListener) this);
        recyclerView.setAdapter(adapter);

    }


    private void loadIdFromEmail(String loadsEmail) {

        myRef = FirebaseDatabase.getInstance().getReference().child("/users");
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
        //getAccesValues();
    }





    @Override
    public void onItemClick(View view, int position) {
        //Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        changeValue(position);

    }

    private void changeValue(int position) {
        myRef = FirebaseDatabase.getInstance().getReference().child("/actionneurs/fenetre");
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
                            if (i==position){
                                if (status==true){
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    com.google.firebase.database.DatabaseReference myRef = database.getReference("/actionneurs/fenetre");
                                    myRef.child(key).child("status").setValue("close");
                                    status=false;
                                }else{
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference myRef = database.getReference("/actionneurs/fenetre");
                                    myRef.child(key).child("status").setValue("open");
                                    status=true;
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

    private void checkAccess() {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("/users");
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
                            String value = String.valueOf(next.child("").child("email").getValue());
                            String value2 = String.valueOf(next.getKey());
                            if (value.toLowerCase().equals(loadsEmail.toLowerCase())){
                                String path = "/users/"+value2+"/Fenetres";
                                getPrises(path);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
    }

    private void getPrises(String emailID) {

        myReff = mFirebaseDatabase.getReference(emailID);
        // Read from the database
        Query query = myReff.orderByKey();
        query.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                        Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                        while (iterator.hasNext()) {
                            DataSnapshot next = (DataSnapshot) iterator.next();
                            String key = String.valueOf(next.getKey());
                            String value = String.valueOf(next.child("").child("access").getValue());

                            int insertIndex = myprisesaccess.size();
                            myprisesaccess.add(insertIndex, value);
                            Log.i("value 2",myprisesaccess.get(insertIndex));




                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
        getUserInfo();
    }

    public void getUserInfo(){
        myRefff = mFirebaseDatabase.getReference("/actionneurs/fenetre");
        // Read from the database
        Query query = myRefff.orderByKey();
        query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                        Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

                        while (iterator.hasNext()) {
                            DataSnapshot next = (DataSnapshot) iterator.next();
                            String value = String.valueOf(next.child("").child("chambre").getValue());
                            String status = String.valueOf(next.child("").child("status").getValue());
                            Log.i("leeeeeed : ",value);
                            Log.i("statuuuusss : ",status);
                            int insertIndex = ledsNames.size();
                            ledsNames.add(insertIndex, value);
                            statusLeds.add(insertIndex, status);
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