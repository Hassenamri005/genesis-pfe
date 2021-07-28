package com.example.pfe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
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

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText eml,pwd;
    private Button signup;
    private com.google.firebase.database.DatabaseReference DatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;
    private com.google.firebase.database.DatabaseReference myRef;
    private TextInputLayout email_layout,password_layout,ref_layout;
    private DatabaseReference myRef1,myRef2,myRef3,myRef4,myReff1,myReff2,myReff3,myReff4;
    ArrayList<String> idPrise,idLed,idFan,idWindow;
    FirebaseUser currentUser;
    // creating a variable for our
    // Firebase Database.
    FirebaseDatabase firebaseDatabase;

    // creating a variable for our Database
    // Reference for Firebase.
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Genesis - Register :");
        getSupportActionBar().hide();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        email_layout = (TextInputLayout) findViewById(R.id.email_layout);
        password_layout = (TextInputLayout) findViewById(R.id.email_layout);

        idPrise = new ArrayList<>();
        idLed = new ArrayList<>();
        idFan = new ArrayList<>();
        idWindow = new ArrayList<>();
        myReff1 = mFirebaseDatabase.getReference("/users");
        myReff2 = mFirebaseDatabase.getReference("/users");
        myReff3 = mFirebaseDatabase.getReference("/users");
        myReff4 = mFirebaseDatabase.getReference("/users");


        signup = (Button)findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupUser();
            }
        });


    }


    public void goToLogin(View v) {
        Intent LoginActivity = new Intent(getApplicationContext(), LoginActivity2.class);
        startActivity(LoginActivity);
    }

    private void signupUser() {
        String email = email_layout.getEditText().getText().toString().trim();
        String password = password_layout.getEditText().getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            addUserToFirebase(email);
                            addHistoryToFirebase(email);
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(SignupActivity.this,"Registering successfully.",Toast.LENGTH_SHORT).show();
                            Intent dashboardActivity = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(dashboardActivity);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignupActivity.this,"Error happened !\n"+ task.getException(),Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }


    private void addHistoryToFirebase2(String email) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        DatabaseReference newChildRef = myRef.push();
        String key = newChildRef.getKey();

        myRef.child("history").child(key).child("who").setValue(email);

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String Date= formatter.format(date);
        myRef.child("history").child(key).child("date").setValue(Date);

        Date currentTime = Calendar.getInstance().getTime();
        myRef.child("history").child(key).child("time").setValue(currentTime.getHours()+":"+currentTime.getMinutes());
        currentUser = mAuth.getCurrentUser();
        String emCreator =currentUser.getEmail().toString();
        String action = emCreator+" created "+email+" account.";
        myRef.child("history").child(key).child("action").setValue(action);
    }

    private void addHistoryToFirebase(String email) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        DatabaseReference newChildRef = myRef.push();
        String key = newChildRef.getKey();

        myRef.child("history").child(key).child("who").setValue(email);

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String Date= formatter.format(date);
        myRef.child("history").child(key).child("date").setValue(Date);

        Date currentTime = Calendar.getInstance().getTime();
        myRef.child("history").child(key).child("time").setValue(currentTime.getHours()+":"+currentTime.getMinutes());
        String action = email+" signed up.";
        myRef.child("history").child(key).child("action").setValue(action);
    }

   /* private void addUserToFirebase(String email) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        DatabaseReference newChildRef = myRef.push();
        String key = newChildRef.getKey();
        myRef.child("users").child(key).child("email").setValue(email);
        myRef.child("users").child(key).child("role").setValue("user");


    }
*/
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
    private void addUserToFirebase(String email) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        DatabaseReference newChildRef = myRef.push();
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

        myRef.child("users").child(key).child("email").setValue(email);
        myRef.child("users").child(key).child("role").setValue("user");


    }




    @Override
    protected void onStart() {
        super.onStart();
        checkUserState();
    }

    private void checkUserState() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is signed in
            Intent dashboardActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(dashboardActivity);
            finish();
        } else {
            // User is signed out
        }
    }


}