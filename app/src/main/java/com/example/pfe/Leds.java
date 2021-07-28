package com.example.pfe;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import static com.example.pfe.MainActivity.loggedemail;

public class Leds extends AppCompatActivity implements MyRecyclerViewAdapter4.ItemClickListener  {
    MyRecyclerViewAdapter4 adapter;
    ArrayList<String> ledsNames,statusLeds,myprisesaccess,myprisesaccessid;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    public static boolean status = false;
    static int i=-1;
    private FirebaseDatabase mFirebaseDatabase;
    private com.google.firebase.database.DatabaseReference myReff;
    private com.google.firebase.database.DatabaseReference DatabaseReference;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leds);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.myyellow)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Led");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("/actionneurs/leds");

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
        adapter = new MyRecyclerViewAdapter4(this, ledsNames,statusLeds);
        adapter.setClickListener((MyRecyclerViewAdapter4.ItemClickListener) this);
        recyclerView.setAdapter(adapter);

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
                            String value = String.valueOf(next.child("").child("chambre").getValue());
                            String status = String.valueOf(next.child("").child("status").getValue());
                            String id = String.valueOf(next.child("").child("").getKey());

                            String acces = String.valueOf(next.child("").child("access").child("").getValue());
                            //Log.i("myyyyyy 222222",acces);

                            //if (acces.contains(loggedemail)){
                            if (myprisesaccess.contains(id)){
                                int insertIndex = 0;
                                ledsNames.add(insertIndex, value);
                                statusLeds.add(insertIndex, status);
                                adapter.notifyItemInserted(insertIndex);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
        //Toast.makeText(getContext(),"Done ",Toast.LENGTH_LONG).show();
    }



    @Override
    public void onItemClick(View view, int position) {
        //Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        changeValue(position);

    }

    private void changeValue(int position) {
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
                            if (i==position){
                                if (status==true){
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference myRef = database.getReference("/actionneurs/leds");
                                    myRef.child(key).child("status").setValue("off");
                                    status=false;
                                }else{
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference myRef = database.getReference("/actionneurs/leds");
                                    myRef.child(key).child("status").setValue("on");
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
        myReff = mFirebaseDatabase.getReference("/users");
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
                            String value = String.valueOf(next.child("").child("email").getValue());
                            String value2 = String.valueOf(next.getKey());
                            Log.i("my pathhhhhhhh ",value);
                            if (loggedemail.toLowerCase().equals(value.toLowerCase())){
                                String path = "/users/"+value2+"/Leds";

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
                            if (value.equals("true")){
                                int insertIndex2 = myprisesaccessid.size();
                                myprisesaccessid.add(insertIndex2, value);
                                Log.i("id",key);
                                int insertIndex = myprisesaccess.size();
                                myprisesaccess.add(insertIndex, key);
                                Log.i("value",myprisesaccessid.get(insertIndex));
                                Log.i("value 22222222222",myprisesaccess.get(insertIndex));
                            }



                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
        getUserInfo();
    }

}