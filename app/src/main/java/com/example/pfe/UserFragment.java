package com.example.pfe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pfe.ui.dashboard.DashboardViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import static com.example.pfe.MainActivity.isAdmin;

public class UserFragment extends Fragment implements MyRecyclerViewAdapter2.ItemClickListener{
    private DashboardViewModel dashboardViewModel;
    MyRecyclerViewAdapter2 adapter;
    private FirebaseDatabase database;
    private DatabaseReference myRef,myRef1,myRef2,myRef3,myRef4,myReff1,myReff2,myReff3,myReff4;
    ArrayList<String> usersNames;
    TextView addUser;
    private static String role,r;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String myLoggedEmail;
    private ProgressBar progressBar;
    AlertDialog.Builder builder;
    static int id=0;
    static String password="123456789",name="user",email="@gmail.com";
    String tempemail;
    ArrayList<String> idPrise,idLed,idFan,idWindow;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("/users");
        myReff1 = database.getReference("/users");
        myReff2 = database.getReference("/users");
        myReff3 = database.getReference("/users");
        myReff4 = database.getReference("/users");



        //getReference();
        //Toast.makeText(getContext(),"your role : "+r,Toast.LENGTH_LONG).show();

        if (isAdmin==true){
            getUserInfo();
            // data to populate the RecyclerView with
            usersNames = new ArrayList<>();
            //usersNames.add("Horse");

            // set up the RecyclerView
            RecyclerView recyclerView = root.findViewById(R.id.recview);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new MyRecyclerViewAdapter2(getContext(), usersNames);
            adapter.setClickListener(this::onItemClick);
            recyclerView.setAdapter(adapter);
        }else {
            Toast.makeText(getContext(),"You are not admin to get access on this part !",Toast.LENGTH_LONG).show();
        }
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        myLoggedEmail =currentUser.getEmail().toString();


        addUser = root.findViewById(R.id.addUser);
        if (isAdmin==false){
            addUser.setVisibility(View.INVISIBLE);
        }
        builder = new AlertDialog.Builder(getContext());
        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser();
            }
        });

        idPrise = new ArrayList<>();
        idLed = new ArrayList<>();
        idFan = new ArrayList<>();
        idWindow = new ArrayList<>();


        return root;
    }

    private void addUser() {
        id++;
        tempemail=name+String.valueOf(id)+email;
        //Setting message manually and performing action on button click
        builder.setMessage("Voulez vous ajouter une nouveau utilisateur ?\nEmail : "+tempemail+"\nMot de passe : "+password)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mAuth.createUserWithEmailAndPassword(tempemail,password)
                                .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            addUserToFirebase(tempemail);
                                            addHistoryToFirebase(tempemail);
                                            // Sign in success, update UI with the signed-in user's information
                                            Toast.makeText(getContext(),"Registering successfully.",Toast.LENGTH_SHORT).show();


                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Toast.makeText(getContext(),"Error happened !\n"+ task.getException(),Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });



                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("AlertDialogExample");
        alert.show();
    }

    private void addHistoryToFirebase(String email) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        DatabaseReference newChildRef = myRef.push();
        String key = newChildRef.getKey();

        myRef.child("history").child(key).child("who").setValue(myLoggedEmail);

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String Date= formatter.format(date);
        myRef.child("history").child(key).child("date").setValue(Date);

        Date currentTime = Calendar.getInstance().getTime();
        myRef.child("history").child(key).child("time").setValue(currentTime.getHours()+":"+currentTime.getMinutes());
        currentUser = mAuth.getCurrentUser();

        String action = myLoggedEmail+" created "+email+" account.";
        myRef.child("history").child(key).child("action").setValue(action);
    }

    private void addUserToFirebase(String email) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef22 = database.getReference();
        DatabaseReference newChildRef = myRef22.push();
        String key = newChildRef.getKey();

        getPrises(key);
        getLeds(key);
        getFans(key);
        getWindow(key);
        //myRef.child("users").child(key).child("Prises").child("0").child("access").setValue("false");
  /*      int lp = idPrise.size();
        for (int i=0;i<lp;i++){
            myRef.child("users").child(key).child("Prises").child(idPrise.get(i)).child("access").setValue("false");
            Log.i("addeddddd",""+idPrise.get(i));
        }
        int ll = idLed.size();
        for (int i=0;i<ll;i++){
            myRef.child("users").child(key).child("Leds").child(idLed.get(i)).child("access").setValue("false");
            Log.i("addeddddd",""+idLed.get(i));
        }
        int lf = idFan.size();
        for (int i=0;i<lf;i++){
            myRef.child("users").child(key).child("Ventilateurs").child(idFan.get(i)).child("access").setValue("false");
            Log.i("addeddddd",""+idFan.get(i));
        }
        int lw = idWindow.size();
        for (int i=0;i<lw;i++){
            myRef.child("users").child(key).child("Fenetres").child(idWindow.get(i)).child("access").setValue("false");
            Log.i("addeddddd",""+idWindow.get(i));
        }

   */

        myRef22.child("users").child(key).child("email").setValue(email);
        myRef22.child("users").child(key).child("role").setValue("user");


    }
    public void getPrises(String key){
        myRef1 = FirebaseDatabase.getInstance().getReference("/actionneurs/prise");
        // Read from the database
        Query query = myRef1.orderByKey();
        query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                        Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                        while (iterator.hasNext()) {
                            DataSnapshot next = (DataSnapshot) iterator.next();
                           // String value = String.valueOf(next.child("").child("status").getValue());
                            //String chambre = String.valueOf(next.child("").child("chambre").getValue());
                            String id = String.valueOf(next.child("").getKey());
                            int insertIndex = 0;
                            idPrise.add(insertIndex, id);
                            myReff1.child(key).child("Prises").child(id).child("access").setValue("false");
                            Log.i("ppppp",""+idPrise.get(insertIndex));
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
        //Toast.makeText(getContext(),"Done ",Toast.LENGTH_LONG).show();
    }
    public void getLeds(String key){
        myRef2 = FirebaseDatabase.getInstance().getReference("/actionneurs/leds");
        // Read from the database
        Query query = myRef2.orderByKey();
        query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                        Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                        while (iterator.hasNext()) {
                            DataSnapshot next = (DataSnapshot) iterator.next();
                            // String value = String.valueOf(next.child("").child("status").getValue());
                            //String chambre = String.valueOf(next.child("").child("chambre").getValue());
                            String id = String.valueOf(next.child("").getKey());
                            int insertIndex = 0;
                            idLed.add(insertIndex, id);
                            myReff2.child(key).child("Leds").child(id).child("access").setValue("false");
                            Log.i("lllllllllll",""+idLed.get(insertIndex));
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
        //Toast.makeText(getContext(),"Done ",Toast.LENGTH_LONG).show();
    }
    public void getWindow(String key){
        myRef3 = FirebaseDatabase.getInstance().getReference("/actionneurs/fenetre");
        // Read from the database
        Query query = myRef3.orderByKey();
        query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                        Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                        while (iterator.hasNext()) {
                            DataSnapshot next = (DataSnapshot) iterator.next();
                            // String value = String.valueOf(next.child("").child("status").getValue());
                            //String chambre = String.valueOf(next.child("").child("chambre").getValue());
                            String id = String.valueOf(next.child("").getKey());
                            int insertIndex = 0;
                            idWindow.add(insertIndex, id);
                            myReff3.child(key).child("Fenetres").child(id).child("access").setValue("false");
                            Log.i("wwwwwwww",""+idWindow.get(insertIndex));
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
        //Toast.makeText(getContext(),"Done ",Toast.LENGTH_LONG).show();
    }
    public void getFans(String key){
        myRef4 = FirebaseDatabase.getInstance().getReference("/actionneurs/ventilateur");
        // Read from the database
        Query query = myRef4.orderByKey();
        query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                        Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                        while (iterator.hasNext()) {
                            DataSnapshot next = (DataSnapshot) iterator.next();
                            // String value = String.valueOf(next.child("").child("status").getValue());
                            //String chambre = String.valueOf(next.child("").child("chambre").getValue());
                            String id = String.valueOf(next.child("").getKey());
                            int insertIndex = 0;
                            idFan.add(insertIndex, id);
                            myReff4.child(key).child("Ventilateurs").child(id).child("access").setValue("false");
                            Log.i("ffffffffffff",""+idFan.get(insertIndex));
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
        //Toast.makeText(getContext(),"Done ",Toast.LENGTH_LONG).show();
    }


    public void onItemClick(View view, int position) {
        //Toast.makeText(getContext(), "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        Intent selectedUser = new Intent(getContext(), SelectedUser.class);
        selectedUser.putExtra("position",position);
        TextView emailTXT = view.findViewById(R.id.tvUserName);
        String email = emailTXT.getText().toString();
        if(!email.toLowerCase().equals(myLoggedEmail)){
            selectedUser.putExtra("email",email);
            startActivity(selectedUser);
        }else{
            //Toast.makeText(getContext(), "You're an Admin, You cannot change your account access settings", Toast.LENGTH_SHORT).show();
            Intent selectedMyProfile = new Intent(getContext(), MyProfile.class);
            selectedMyProfile.putExtra("position",position);
            selectedMyProfile.putExtra("email",myLoggedEmail);
            startActivity(selectedMyProfile);

        }

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
                            String value = String.valueOf(next.child("email").getValue());
                            int insertIndex = 0;
                            usersNames.add(insertIndex, value);
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