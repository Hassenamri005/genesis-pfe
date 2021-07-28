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

import static com.example.pfe.MainActivity.loggedemail;

public class Ventilateurs extends AppCompatActivity implements MyRecyclerViewAdapterVentilateurs.ItemClickListener{
    MyRecyclerViewAdapterVentilateurs adapter;
    ArrayList<String> chambres,statuss,statusPrise,myprisesaccess,myprisesaccessid;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    RecyclerView recyclerView;
    public static boolean statusP ;
    static int i=-1;
    private com.google.firebase.database.DatabaseReference myReff;

    @RequiresApi(api = Build.VERSION_CODES.M)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventilateurs);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.myblue)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Fan");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("/actionneurs/ventilateur");

        // data to populate the RecyclerView with
        chambres = new ArrayList<>();
        statuss = new ArrayList<>();
        statusPrise = new ArrayList<>();
        myprisesaccess = new ArrayList<>();
        myprisesaccessid = new ArrayList<>();

        checkAccess();

        // set up the RecyclerView
        recyclerView = findViewById(R.id.rvAnimals);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MyRecyclerViewAdapterVentilateurs(this, chambres, statuss);
        adapter.setClickListener((MyRecyclerViewAdapterVentilateurs.ItemClickListener) this);
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

                            //if (acces.contains(loggedemail)){
                            if (myprisesaccess.contains(id)) {
                                int insertIndex = 0;
                                chambres.add(insertIndex, chambre);
                                statuss.add(insertIndex, value);
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
                                String path = "/users/"+value2+"/Ventilateurs";
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
    }




}