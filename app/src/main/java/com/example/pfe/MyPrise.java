package com.example.pfe;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.pfe.MyRecyclerViewAdapter;
import com.example.pfe.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.example.pfe.MainActivity.actionneursPrisesAcces;
import static com.example.pfe.MainActivity.loggedemail;

public class MyPrise extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {
    MyRecyclerViewAdapter adapter;
    ArrayList<String> chambres,statuss,idPrise,myprisesaccess,myprisesaccessid;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private com.google.firebase.database.DatabaseReference DatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;
    private com.google.firebase.database.DatabaseReference myRef;
    RecyclerView recyclerView;
    public static boolean statusP ;
    static int i=-1;
    private com.google.firebase.database.DatabaseReference myReff;


    @RequiresApi(api = Build.VERSION_CODES.M)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_prise);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.myblue)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Plug");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("/actionneurs/prise");

        // data to populate the RecyclerView with
        chambres = new ArrayList<>();
        statuss = new ArrayList<>();
        idPrise = new ArrayList<>();
        myprisesaccess = new ArrayList<>();
        myprisesaccessid = new ArrayList<>();



        checkAccess();


        //checkAccess2();

        // set up the RecyclerView
        recyclerView = findViewById(R.id.rvAnimals);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyRecyclerViewAdapter(this, chambres, statuss);
        adapter.setClickListener((MyRecyclerViewAdapter.ItemClickListener) this);
        recyclerView.setAdapter(adapter);



    }



    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_LONG).show();

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
                            String chambre = String.valueOf(next.child("").child("chambre").getValue());
                            String id = String.valueOf(next.child("").child("").getKey());

                            String acces = String.valueOf(next.child("").child("access").child("").getValue());
                            //Log.i("myyyyyy 222222",acces);
                            Log.i("emaiiiiil",String.valueOf(acces.contains(loggedemail)));
                            Log.i("my id",id);
                            Log.i("my chambre",chambre);
                            Log.i("my status",value);
                            //if (acces.contains(loggedemail)){
                            if (myprisesaccess.contains(id)){
                                Log.i("emaiiiiil",String.valueOf(acces.contains(loggedemail)));
                                Log.i("my id",id);
                                Log.i("my chambre",chambre);
                                Log.i("my status",value);
                                int insertIndex = 0;
                                chambres.add(insertIndex, chambre);
                                statuss.add(insertIndex, value);
                                idPrise.add(insertIndex, id);
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

    /*public void getUserInfo(){
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
                            String chambre = String.valueOf(next.child("").child("chambre").getValue());
                            String id = String.valueOf(next.child("").getKey());
                            Log.i("my id",id);
                            Log.i("my chambre",chambre);
                            Log.i("my status",value);

                            int insertIndex = 0;
                            chambres.add(insertIndex, chambre);
                            statuss.add(insertIndex, value);
                            idPrise.add(insertIndex, id);
                            adapter.notifyItemInserted(insertIndex);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
        //Toast.makeText(getContext(),"Done ",Toast.LENGTH_LONG).show();
    }*/

    private void checkAccess2() {

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
                            String key = String.valueOf(next.getKey());
                            String value = String.valueOf(next.child("").child("access").child("").getValue());




                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
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
                            if (loggedemail.toLowerCase().equals(value.toLowerCase())){
                                String path = "/users/"+value2+"/Prises";
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
                                Log.i("value 2",myprisesaccess.get(insertIndex));
                            }



                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
        getUserInfo();
        getUserInfo2();
    }

    private void getUserInfo2() {
        if (myprisesaccess.contains("0")){
            Log.i("hoooooo","hooooo");
        }
    }


}