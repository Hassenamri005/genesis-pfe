package com.example.pfe;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    public static boolean isAdmin ;
    private com.google.firebase.database.DatabaseReference DatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;
    private com.google.firebase.database.DatabaseReference myRef,myRef1,myRef2,myRef3,myRef4,myRef5,myRef6,myRef7,myRef8;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    SpeechRecognizer speechRecognizer;
    TextToSpeech textToSpeech;
    static String listenedText;
    static int  count=0;
    private TextView txvResult;
    private Switch sw7,sw8;
    static String loggedemail;
    public static ArrayList<String> actionneursPrisesAcces,actionneursLedsAcces,actionneursFenetresAcces,actionneursVentilateursAcces;


    private com.google.firebase.database.DatabaseReference myReff;
    private NotificationManager mNotificationManager;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    private static boolean isStart=false,isStop=false;


    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final int REQUEST_PHONE_CALL = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_settings )
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
       // NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        requestAudioPermission();
        requestContactPermission();
        requestPhoneCallPermission();



        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("/users");
        myRef1 = mFirebaseDatabase.getReference("/users");

        currentUser = mAuth.getCurrentUser();
//        loggedemail =currentUser.getEmail().toString();

        txvResult = (TextView) findViewById(R.id.txvResult);

        // data to populate the RecyclerView with
        actionneursPrisesAcces = new ArrayList<>();
        actionneursLedsAcces = new ArrayList<>();
        actionneursVentilateursAcces = new ArrayList<>();
        actionneursFenetresAcces = new ArrayList<>();



        if(currentUser!=null){
            loggedemail =currentUser.getEmail().toString();
            checkUserState();
            getUserPrisesAccess();
            getUserLedsAccess();
            getUserVentilateursAccess();
            getUserFenetresAccess();
            getReference();
            if (isAdmin==true){
                //Toast.makeText(MainActivity.this,"your role is : ",Toast.LENGTH_LONG).show();
                //getUserInfo();
                Bundle bundle = new Bundle();
                bundle.putString("isAdmin", "true");
                // set Fragmentclass Arguments
                UserFragment fragobj = new UserFragment();
                fragobj.setArguments(bundle);
            }else {
                //Toast.makeText(MainActivity.this,"You are not admin to get access on this part !",Toast.LENGTH_LONG).show();
            }

        }

    }

    private void requestPhoneCallPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
        }
    }

    private void requestAudioPermission() {
        // get audio permessions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},1 );
        }
    }


    public void requestContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Read Contacts permission");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Please enable access to contacts.");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {android.Manifest.permission.READ_CONTACTS}
                                    , PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            }
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
                if (x>28){
                    notifyMe("Increasing of Temperature !","The Temperature degree is "+x+" ,it's greater than normal value.","");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        };
        myReff.addValueEventListener(postListener);
    }
    private void getHumFirebaseValue() {
        myReff = FirebaseDatabase.getInstance().getReference().child("Hum");
        // User data change listener
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                String hm = dataSnapshot.getValue().toString();
                int x = Integer.parseInt(hm);
                if (x>15){
                    notifyMe("Increasing of Humidity !","The Humidity degree is "+x+" ,it's greater than normal value.","");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        };
        myReff.addValueEventListener(postListener);

    }
    private void getGasFirebaseValue() {
        myReff = FirebaseDatabase.getInstance().getReference().child("Gas");
        // User data change listener
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                String gs = dataSnapshot.getValue().toString();
                int x = Integer.parseInt(gs);

                if (x>30){
                    notifyMe("Increasing of GAS !","The Gas degree is "+x+" ,it's greater than normal value.","");
                    //hum.setText(hm);
                }
                else{
                    //hum.setText(hm);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        };
        myReff.addValueEventListener(postListener);

    }
    public void notifyMe(String title,String content,String ticker) {
        mNotificationManager = ( NotificationManager ) MainActivity.this.getSystemService( SettingsActivity.NOTIFICATION_SERVICE );
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity.this, default_notification_channel_id ) ;
        mBuilder.setContentTitle( title ) ;
        mBuilder.setContentText( content ) ;
        mBuilder.setTicker( ticker ) ;
        mBuilder.setSmallIcon(R.drawable.messageicon ) ;
        mBuilder.setAutoCancel( true ) ;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            int importance = NotificationManager.IMPORTANCE_HIGH ;
            NotificationChannel notificationChannel = new NotificationChannel( NOTIFICATION_CHANNEL_ID , "NOTIFICATION_CHANNEL_NAME" , importance) ;
            mBuilder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel) ;
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(( int ) System. currentTimeMillis () , mBuilder.build()) ;

        addNotificationToFirebase(title,content);

    }
    private void addNotificationToFirebase(String text,String content){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        DatabaseReference newChildRef = myRef.push();
        String key = newChildRef.getKey();


        myRef.child("notifications").child(key).child("title").setValue(text);
        myRef.child("notifications").child(key).child("content").setValue(content);
        Date currentTime = Calendar.getInstance().getTime();
        myRef.child("notifications").child(key).child("time").setValue(currentTime.getHours()+":"+currentTime.getMinutes());
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String Date= formatter.format(date);
        myRef.child("notifications").child(key).child("date").setValue(Date);

    }

    public void getUserPrisesAccess(){
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
        //Toast.makeText(getContext(),"Done ",Toast.LENGTH_LONG).show();
    }

    private void getPrises(String emailID) {

        myRef = mFirebaseDatabase.getReference(emailID);
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
                            String value = String.valueOf(next.child("").child("access").getValue());
                            int insertIndex = actionneursPrisesAcces.size();
                            actionneursPrisesAcces.add(insertIndex, value);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
    }

    public void getUserLedsAccess(){
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
                            if (loggedemail.toLowerCase().equals(value.toLowerCase())){
                                String path = "/users/"+value2+"/Leds";
                                getLeds(path);
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

    private void getLeds(String emailID) {

        myRef = mFirebaseDatabase.getReference(emailID);
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
                            String value = String.valueOf(next.child("").child("access").getValue());
                            int insertIndex = actionneursLedsAcces.size();
                            actionneursLedsAcces.add(insertIndex, value);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
    }

    public void getUserVentilateursAccess(){
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
                            if (loggedemail.toLowerCase().equals(value.toLowerCase())){
                                String path = "/users/"+value2+"/Ventilateurs";
                                getVentilateurs(path);
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

    private void getVentilateurs(String emailID) {

        myRef2 = mFirebaseDatabase.getReference(emailID);
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
                            String value = String.valueOf(next.child("").child("access").getValue());
                            int insertIndex = actionneursVentilateursAcces.size();

                            actionneursVentilateursAcces.add(insertIndex, value);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
    }

    public void getUserFenetresAccess(){
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
                            if (loggedemail.toLowerCase().equals(value.toLowerCase())){
                                String path = "/users/"+value2+"/Fenetres";
                                getFenetres(path);
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

    private void getFenetres(String emailID) {

        myRef3 = mFirebaseDatabase.getReference(emailID);
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
                            String value = String.valueOf(next.child("").child("access").getValue());
                            int insertIndex = actionneursFenetresAcces.size();
                            actionneursFenetresAcces.add(insertIndex, value);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
    }

    private void getReference(){
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
                            currentUser = mAuth.getCurrentUser();
                            String email =currentUser.getEmail().toString();
                            String value = String.valueOf(next.child("email").getValue());
                            Log.i("my roooooooooole",value);

                            if(email.toLowerCase().equals(value.toLowerCase())){
                                String valueRef = String.valueOf(next.child("role").getValue());
                                //Toast.makeText(getContext(),"role : "+valueRef,Toast.LENGTH_LONG).show();
                                loggedemail = email;
                                if (valueRef.toLowerCase().equals("admin")){
                                    isAdmin = true;
                                }


                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
        //Toast.makeText(getContext(),"role : "+r,Toast.LENGTH_LONG).show();
        //return r;

    }


    @Override
    protected void onStart() {
        super.onStart();
        checkUserState();
        isStart =true;
        if ((isStart==true)&&(currentUser!=null)&&(isStop==false)){
            getTempFirebaseValue();
            getHumFirebaseValue();
            getGasFirebaseValue();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isStart=false;
        isStop=true;
        if ((isStop==false)&&(currentUser!=null)&&(isStart==false)){
            getTempFirebaseValue();
            getHumFirebaseValue();
            getGasFirebaseValue();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //Permission granted
                Toast.makeText(MainActivity.this,"Permession Granted.",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MainActivity.this,"Permession Denied !!!.",Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestPhoneCallPermission();
            } else {
                Toast.makeText(this, "Permission Denied !!! \n Try Again.",Toast.LENGTH_LONG).show();
            }
        }
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "You have disabled a contacts permission", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void checkUserState() {
        FirebaseUser currentUser2 = mAuth.getCurrentUser();
        if (currentUser2 != null) {
            // User is signed in
            currentUser = mAuth.getCurrentUser();
            //Toast.makeText(MainActivity.this,"Welcome.",Toast.LENGTH_SHORT).show();
        } else {
            // User is signed out

            Intent loginActivity = new Intent(getApplicationContext(), LoginActivity2.class);
            startActivity(loginActivity);
            finish();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id){
            case R.id.logout:
                addHistoryToFirebase();
                FirebaseAuth.getInstance().signOut();
                finish();
                Intent loginActivity = new Intent(getApplicationContext(), LoginActivity2.class);
                startActivity(loginActivity);
            case R.id.settings:
                finish();
                Intent settingsActivity = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsActivity);
            default:
                return super.onOptionsItemSelected(menuItem);
        }

    }



    private void addHistoryToFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        com.google.firebase.database.DatabaseReference myRef = database.getReference();
        DatabaseReference newChildRef = myRef.push();
        String key = newChildRef.getKey();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String email = currentUser.getEmail();
        myRef.child("history").child(key).child("who").setValue(email);

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String Date= formatter.format(date);
        myRef.child("history").child(key).child("date").setValue(Date);

        Date currentTime = Calendar.getInstance().getTime();
        myRef.child("history").child(key).child("time").setValue(currentTime.getHours()+":"+currentTime.getMinutes());
        String action = email+" signed out.";
        myRef.child("history").child(key).child("action").setValue(action);
    }

}