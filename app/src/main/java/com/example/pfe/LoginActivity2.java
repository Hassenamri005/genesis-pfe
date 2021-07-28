package com.example.pfe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LoginActivity2 extends AppCompatActivity {
    private TextInputLayout email_layout,password_layout;
    private FirebaseAuth mAuth;
    private Button login;
    TextView textView6;
    static String language;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Genesis - Login :");
        getSupportActionBar().hide();
        email_layout = (TextInputLayout) findViewById(R.id.email_layout);

        password_layout = (TextInputLayout) findViewById(R.id.email_layout);

        //pwd = (TextInputLayout)findViewById(R.id.password);
        //String e = eml.getText().toString().trim();
        //String p = pwd.getText().toString().trim();
        login = (Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUserAccount();
            }
        });

        //static String language;
        language = Locale.getDefault().getLanguage();
        textView6 = findViewById(R.id.textView6);
        if (language.equals(new Locale("ar").getLanguage())){
            textView6.setText("تسجيل الدخول :");
            login.setText("دخول");
        }

    }

    public void goToSignup(View v) {
        Intent SignupActivity = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(SignupActivity);
    }


    private void loginUserAccount() {
        // show the visibility of progress bar to show loading


        // Take the value of two edit texts in Strings
        String email, password;
        email = email_layout.getEditText().getText().toString().trim();
        password = password_layout.getEditText().getText().toString().trim();

        // validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(),
                    "Please enter email!!",
                    Toast.LENGTH_LONG)
                    .show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),
                    "Please enter password!!",
                    Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // signin existing user
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(
                                    @NonNull Task<AuthResult> task)
                            {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(),
                                            "Login successful!!",
                                            Toast.LENGTH_LONG)
                                            .show();

                                    // hide the progress bar

                                    // if sign-in is successful
                                    // intent to home activity
                                    Intent intent
                                            = new Intent(LoginActivity2.this,
                                            MainActivity.class);
                                    startActivity(intent);
                                }

                                else {

                                    // sign-in failed
                                    Toast.makeText(getApplicationContext(),
                                            "Login failed!! \n"+task.getException(),
                                            Toast.LENGTH_LONG)
                                            .show();

                                }
                            }
                        });
    }

    private void loginUser() {
        String email = email_layout.getEditText().getText().toString().trim();
        String password = password_layout.getEditText().getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            addHistoryToFirebase(email);
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LoginActivity2.this, "Logged in successfully.", Toast.LENGTH_SHORT).show();

                            Intent dashboardActivity = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(dashboardActivity);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("error :",""+task.getException());
                            Toast.makeText(LoginActivity2.this, "Error happened !\n" + task.getException(), Toast.LENGTH_LONG).show();
 //                           Toast.makeText(LoginActivity2.this, "Invalid email or password !", Toast.LENGTH_LONG).show();
                        }
                    }
                });

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
        String action = email+" signed in.";
        myRef.child("history").child(key).child("action").setValue(action);
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