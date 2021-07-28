package com.example.pfe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import static com.example.pfe.MainActivity.isAdmin;

public class SelectedUser extends AppCompatActivity {
    private String loadsEmail,myLoggedEmail;
    private TextView txt,txt1;
    private Switch swAdmin,swLed,swTv;
    private Button disble,delete;
    private com.google.firebase.database.DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private Button lampesbtn,fenbtn,prisesbtn,mdpbtn,ventbtn, deleteBTN;
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_user);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Account Details");
        Intent intent=getIntent();
        loadsEmail = intent.getStringExtra("email");

        txt1 = (TextView)findViewById(R.id.textView2);
        txt = findViewById(R.id.textView);
        txt.setText(""+loadsEmail);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        myLoggedEmail =currentUser.getEmail().toString();
        //set defult values from firebase
        loadValues();

        swAdmin = findViewById(R.id.swAdmin);
        swAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!loadsEmail.equals(myLoggedEmail)){
                    if (isChecked==true){
                        switchAdminValueInFirebase(true);
                    }else{
                        switchAdminValueInFirebase(false);
                    }
                }else{
                    Toast.makeText(SelectedUser.this, "You cannot doing any action in your account", Toast.LENGTH_SHORT).show();
                }

            }

        });

        deleteBTN = findViewById(R.id.deleteBTN);
        deleteBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser(loadsEmail);
            }
        });
        fenbtn = findViewById(R.id.fenbtn);
        fenbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ActLampes = new Intent(getApplicationContext(), ActFenetres.class);
                ActLampes.putExtra("loadsEmail",loadsEmail);
                startActivity(ActLampes);
            }
        });
        prisesbtn = findViewById(R.id.prisesbtn);
        prisesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ActLampes = new Intent(getApplicationContext(), ActPrises.class);
                ActLampes.putExtra("loadsEmail",loadsEmail);
                startActivity(ActLampes);
            }
        });
        lampesbtn = findViewById(R.id.lampesbtn);
        lampesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ActLampes = new Intent(getApplicationContext(), ActLampes.class);
                ActLampes.putExtra("loadsEmail",loadsEmail);
                startActivity(ActLampes);
            }
        });
        ventbtn = findViewById(R.id.ventbtn);
        ventbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ActLampes = new Intent(getApplicationContext(), ActVentilateurs.class);
                ActLampes.putExtra("loadsEmail",loadsEmail);
                startActivity(ActLampes);
            }
        });
        mdpbtn = findViewById(R.id.mdpbtn);
        mdpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser(loadsEmail);
            }
        });

    }

    private void deleteUser(String loadsEmail) {



    }

    private void loadValues() {
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
                            String valueAdmin = String.valueOf(next.child("role").getValue());
                            String valueLED = String.valueOf(next.child("LED").getValue());
                            String valueTV = String.valueOf(next.child("TV").getValue());
                            if ((loadsEmail.equals(value))&&(!loadsEmail.equals(myLoggedEmail))){
                                if (valueAdmin.toLowerCase().equals("admin")){
                                    txt1.setText("Admin");
                                    swAdmin.setChecked(true);
                                }else{
                                    txt1.setText("User");
                                    swAdmin.setChecked(false);
                                }

                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private void switchAdminValueInFirebase(boolean b) {

        if (b==true){
            setAdminValue(true);
            //Toast.makeText(SelectedUser.this,"Prise On",Toast.LENGTH_SHORT).show();
        }else {
            setAdminValue(false);
            //Toast.makeText(SelectedUser.this, "Prise OFF", Toast.LENGTH_SHORT).show();
        }
    }
    private void setAdminValue(boolean b){
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
                                if (b==true){
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference myRef = database.getReference("/users");
                                    myRef.child(key).child("role").setValue("Admin");
                                }else{
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference myRef = database.getReference("/users");
                                    myRef.child(key).child("role").setValue("User");
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



}