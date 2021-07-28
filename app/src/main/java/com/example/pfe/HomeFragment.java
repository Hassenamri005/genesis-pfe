package com.example.pfe;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.pfe.ui.home.HomeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.Locale;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class HomeFragment extends Fragment {

    SpeechRecognizer speechRecognizer;
    TextToSpeech textToSpeech;
    static String listenedText;
    static int count = 0;
    private ImageView imageView5, imageView6, btnSpeak,imageView7, logout, settings, fan, humiditeCapteurs,imageView9;
    private FirebaseAuth mAuth;
    private Switch sw7, sw8;
    private TextView temp, hum;
    private TextView txvResult;
    private com.google.firebase.database.DatabaseReference myReff,myReff1,myReff2,myReff3,myReff4;
    private com.google.firebase.database.DatabaseReference DatabaseReference;
    private HomeViewModel homeViewModel;
    private static boolean status = false;
    private static boolean status2 = false;
    private ProgressBar pbTemp, pbHum, pbGas;
    int progressStatus = 0, progressStatus2 = 0, progressStatus3 = 0;
    private Handler handler = new Handler();
    private TextView date, myName,temperature,hum2,gasdetector;
    private FirebaseUser currentUser;
    private String myLoggedEmail;
    Date mydate;
    String myDate;
    SimpleDateFormat formatter;
    public static boolean ledAccess = false, tvAccess = false;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    ArrayList<String> nameList,phoneList,emailList;
    Handler handler2;
    static int countHandler=0;
    private static String receiver,subject,content;
    ArrayList<String> data2;
    static String language;
    static String phoneNumber;
    static String loadsEmail;
    private FirebaseDatabase mFirebaseDatabase;
    private com.google.firebase.database.DatabaseReference myRefff,myRefff1,myRefff2,myRefff3,myRefff4,myRefff5,myRefff6;
    private com.google.firebase.database.DatabaseReference myReffff;
    static String ledId;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //enable full screen

        pbTemp = (ProgressBar) root.findViewById(R.id.pbTemp);
        pbTemp.setProgress(0);   // Main Progress
        pbHum = (ProgressBar) root.findViewById(R.id.pbHum);
        pbHum.setProgress(00);   // Main Progress
        pbGas = (ProgressBar) root.findViewById(R.id.pb);
        pbGas.setProgress(0);   // Main Progress


        date = root.findViewById(R.id.date);
        mydate = new Date();
        formatter = new SimpleDateFormat("dd/MM/yyyy");
        myDate = formatter.format(mydate);
        date.setText(myDate);

        // for send email
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference = FirebaseDatabase.getInstance().getReference();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("/users");



        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        myName = root.findViewById(R.id.myName);
        if (currentUser!=null){
            myLoggedEmail = currentUser.getEmail().toString();
        }

        //myLoggedEmail = loggedemail;
       /* myLoggedEmail = currentUser.getEmail();
        String str = myLoggedEmail;
        if (!str.isEmpty()){
            int index = str.indexOf("@");
            String who2 = str.substring(0, index);
            myName.setText(who2);
        }

        */
/*        int index = str.indexOf("@");
        String who2 = str.substring(0, index);
        myName.setText(who2);
*/




        settings = (ImageView) root.findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent settingsActivity = new Intent(getContext(), SettingsActivity.class);
                settingsActivity.putExtra("loadsEmail",myLoggedEmail);
                startActivity(settingsActivity);
            }
        });
        fan = (ImageView) root.findViewById(R.id.imageView88);
        fan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent settingsActivity = new Intent(getContext(), Leds.class);
                startActivity(settingsActivity);

            }
        });
        humiditeCapteurs = (ImageView) root.findViewById(R.id.imageView99);
        humiditeCapteurs.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent settingsActivity = new Intent(getContext(), MyPrise.class);
                startActivity(settingsActivity);
            }
        });
        imageView9 = (ImageView) root.findViewById(R.id.imageView9);
        imageView9.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent settingsActivity = new Intent(getContext(), Fenetres.class);
                startActivity(settingsActivity);
            }
        });
        imageView7 = (ImageView) root.findViewById(R.id.imageView7);
        imageView7.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent settingsActivity = new Intent(getContext(), Ventilateurs.class);
                startActivity(settingsActivity);
            }
        });
        logout = (ImageView) root.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addHistoryToFirebase();
                FirebaseAuth.getInstance().signOut();
                Intent loginActivity = new Intent(getContext(), LoginActivity2.class);
                startActivity(loginActivity);
            }
        });

        txvResult = (TextView) root.findViewById(R.id.txvResult);
        temp = (TextView) root.findViewById(R.id.temp);
        hum = (TextView) root.findViewById(R.id.hum);
        getTempFirebaseValue();
        getHumFirebaseValue();
        getGasFirebaseValue();


        language = Locale.getDefault().getLanguage();

        // vocale
        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int lang = textToSpeech.setLanguage(Locale.getDefault());

                    if (lang == TextToSpeech.LANG_MISSING_DATA || lang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(getContext(), "Language is not supported !", Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(getContext(), "Language Supported", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        FloatingActionButton btnSpeak = (FloatingActionButton) root.findViewById(R.id.fab);
        btnSpeak.setClickable(true);
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(),"begin",Toast.LENGTH_SHORT).show();
                listeningBTN();

            }
        });

        gasdetector = root.findViewById(R.id.gasdetector);
        hum2 = root.findViewById(R.id.hum2);
        temperature = root.findViewById(R.id.chambre);
        if (language.equals(new Locale("ar").getLanguage())){
            txvResult.setText("ليلي في خدمتك.");
            gasdetector.setText("الغاز");
            hum2.setText("الرطوبة");
            temperature.setText("الحرارة");
        }


        RelativeLayout relativeclic1 =(RelativeLayout)root.findViewById(R.id.RelativeTemp);
        relativeclic1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivityForResult(new Intent(getContext(), TemperatureCapteurs.class), 0);
            }
        });

        RelativeLayout relativeclic2 =(RelativeLayout)root.findViewById(R.id.RelativeHum);
        relativeclic2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivityForResult(new Intent(getContext(),HumiditeCapteurs2.class), 0);
            }
        });

// Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        myRefff = database.getReference("/actionneurs/leds");
        loadsEmail = myLoggedEmail;

        return root;
    }


    private void addLEDHistoryToFirebase(String act) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        com.google.firebase.database.DatabaseReference myRef = database.getReference();
        DatabaseReference newChildRef = myRef.push();
        String key = newChildRef.getKey();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String email = currentUser.getEmail();
        myRef.child("actions").child("historyLed").child(key).child("who").setValue(email);

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String Date = formatter.format(date);
        myRef.child("actions").child("historyLed").child(key).child("date").setValue(Date);

        Date currentTime = Calendar.getInstance().getTime();
        myRef.child("actions").child("historyLed").child(key).child("time").setValue(currentTime.getHours() + ":" + currentTime.getMinutes());
        String action = email + " "+act;
        myRef.child("actions").child("historyLed").child(key).child("action").setValue(action);
    }
    private void addPRISEHistoryToFirebase(String act) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        com.google.firebase.database.DatabaseReference myRef = database.getReference();
        DatabaseReference newChildRef = myRef.push();
        String key = newChildRef.getKey();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String email = currentUser.getEmail();
        myRef.child("actions").child("historyPrise").child(key).child("who").setValue(email);

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String Date = formatter.format(date);
        myRef.child("actions").child("historyPrise").child(key).child("date").setValue(Date);

        Date currentTime = Calendar.getInstance().getTime();
        myRef.child("actions").child("historyPrise").child(key).child("time").setValue(currentTime.getHours() + ":" + currentTime.getMinutes());
        String action = email + " "+act;
        myRef.child("actions").child("historyPrise").child(key).child("action").setValue(action);
    }
    private void addVENTILATEURHistoryToFirebase(String act) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        com.google.firebase.database.DatabaseReference myRef = database.getReference();
        DatabaseReference newChildRef = myRef.push();
        String key = newChildRef.getKey();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String email = currentUser.getEmail();
        myRef.child("actions").child("historyVentilateur").child(key).child("who").setValue(email);

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String Date = formatter.format(date);
        myRef.child("actions").child("historyVentilateur").child(key).child("date").setValue(Date);

        Date currentTime = Calendar.getInstance().getTime();
        myRef.child("actions").child("historyVentilateur").child(key).child("time").setValue(currentTime.getHours() + ":" + currentTime.getMinutes());
        String action = email + " "+act;
        myRef.child("actions").child("historyVentilateur").child(key).child("action").setValue(action);
    }
    private void addFENETREHistoryToFirebase(String act) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        com.google.firebase.database.DatabaseReference myRef = database.getReference();
        DatabaseReference newChildRef = myRef.push();
        String key = newChildRef.getKey();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String email = currentUser.getEmail();
        myRef.child("actions").child("historyFenetre").child(key).child("who").setValue(email);

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String Date = formatter.format(date);
        myRef.child("actions").child("historyFenetre").child(key).child("date").setValue(Date);

        Date currentTime = Calendar.getInstance().getTime();
        myRef.child("actions").child("historyFenetre").child(key).child("time").setValue(currentTime.getHours() + ":" + currentTime.getMinutes());
        String action = email + " "+act;
        myRef.child("actions").child("historyFenetre").child(key).child("action").setValue(action);
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
        String Date = formatter.format(date);
        myRef.child("history").child(key).child("date").setValue(Date);

        Date currentTime = Calendar.getInstance().getTime();
        myRef.child("history").child(key).child("time").setValue(currentTime.getHours() + ":" + currentTime.getMinutes());
        String action = email + " signed out.";
        myRef.child("history").child(key).child("action").setValue(action);
    }
    /*LED ---------------------------------------------------------------------------------*/
    private void getLedId(String chambre, String action) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRefff = FirebaseDatabase.getInstance().getReference("/actionneurs/leds");
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
                            String key = String.valueOf(next.getKey());

                            if (value.toLowerCase().equals(chambre)){
                                Log.i("leeeeeed : ",value);
                                Log.i("keyyyy : ",key);
                                Log.i("statuuuusss : ",status);
                                ledId = key;
                                Log.i("keyyyyy : ",ledId);
                                checkAccess(key,action);
                            }


                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
    }
    private void checkAccess(String key, String action) {
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
                            Log.i("user id : ",value);
                            Log.i("email : ",loadsEmail);
                            if (value.toLowerCase().equals(loadsEmail)){
                                String path = "/users/"+value2+"/Leds";

                                getPrises(path,key,action);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
    }
    private void getPrises(String emailID, String key, String action) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myReff = mFirebaseDatabase.getReference(emailID);
        Log.i("haw path",emailID);

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
                            String keyy = String.valueOf(next.getKey());
                            String value = String.valueOf(next.child("").child("access").getValue());
                            Log.i("haw key",value);
                            if (keyy.equals(key)){
                                Log.i("value key",value);
                                changeLedValue(key,value,action);
                            }





                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );

    }
    public void changeLedValue(String key,String acces, String action){
        myRefff = mFirebaseDatabase.getReference("/actionneurs/leds");
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
                            String keyy = String.valueOf(next.getKey());
                            if ((keyy.equals(key))&&(acces.equals("true"))){
                                Log.i("LED STATUS : ",key+" "+value+" "+status);
                                if (action.equals("on")){
                                    if (status.equals("on")){
                                        Log.i("heyyyyy : ","onnn doneeee");
                                        textToSpeech.speak("light already on", TextToSpeech.QUEUE_FLUSH, null);
                                    }else{
                                        myRefff.child(keyy).child("status").setValue("on");
                                        addLEDHistoryToFirebase("was turned on "+value+" light.");
                                        textToSpeech.speak("light switched on", TextToSpeech.QUEUE_FLUSH, null);
                                    }
                                }else if (action.equals("off")){
                                    if (status.equals("off")){
                                        Log.i("heyyyyy : ","offf doneeee");
                                        textToSpeech.speak("light already off", TextToSpeech.QUEUE_FLUSH, null);
                                    }else{
                                        myRefff.child(keyy).child("status").setValue("off");
                                        addLEDHistoryToFirebase("was turned off "+value+" light.");
                                        textToSpeech.speak("light switched off", TextToSpeech.QUEUE_FLUSH, null);
                                    }
                                }
                            }else if (acces.equals("false")){
                                textToSpeech.speak("ouups ! "+value+" light access denied", TextToSpeech.QUEUE_FLUSH, null);
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

    /*---------------------------------------------------------------------------------*/
    /*Prise ---------------------------------------------------------------------------------*/
    private void getPriseId(String chambre, String action) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRefff1 = mFirebaseDatabase.getReference("/actionneurs/prise");
        // Read from the database
        Query query = myRefff1.orderByKey();
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
                            String key = String.valueOf(next.getKey());

                            if (value.toLowerCase().equals(chambre)){
                                Log.i("leeeeeed : ",value);
                                Log.i("keyyyy : ",key);
                                Log.i("statuuuusss : ",status);
                                ledId = key;
                                checkAccessPrise(key,action);
                            }


                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
    }
    private void checkAccessPrise(String key, String action) {
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
                            if (value.toLowerCase().equals(loadsEmail)){
                                String path = "/users/"+value2+"/Prises";
                                getPrises2(path,key,action);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
    }
    private void getPrises2(String emailID, String key, String action) {

        myReff1 = mFirebaseDatabase.getReference(emailID);
        // Read from the database
        Query query = myReff1.orderByKey();
        query.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                        Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                        while (iterator.hasNext()) {
                            DataSnapshot next = (DataSnapshot) iterator.next();
                            String keyy = String.valueOf(next.getKey());
                            String value = String.valueOf(next.child("").child("access").getValue());
                            if (keyy.equals(key)){
                                Log.i("value key",value);
                                changePriseValue(key,value,action);
                            }





                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );

    }
    public void changePriseValue(String key,String acces, String action){
        myRefff1 = mFirebaseDatabase.getReference("/actionneurs/prise");
        // Read from the database
        Query query = myRefff1.orderByKey();
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
                            String keyy = String.valueOf(next.getKey());
                            if ((keyy.equals(key))&&(acces.equals("true"))){
                                Log.i("heyyyyy : ","doneeee");
                                if (action.equals("on")){
                                    if (status.equals("on")){
                                        textToSpeech.speak("plug already on", TextToSpeech.QUEUE_FLUSH, null);
                                    }else{
                                        myRefff1.child(keyy).child("status").setValue("on");
                                        addPRISEHistoryToFirebase("was turned on "+value+" plug.");
                                        textToSpeech.speak("plug turned on", TextToSpeech.QUEUE_FLUSH, null);
                                    }
                                }else if (action.equals("off")){
                                    if (status.equals("off")){
                                        textToSpeech.speak("plug already off", TextToSpeech.QUEUE_FLUSH, null);
                                    }else{
                                        myRefff1.child(keyy).child("status").setValue("off");
                                        addPRISEHistoryToFirebase("was turned off "+value+" fan.");
                                        textToSpeech.speak("plug turned off", TextToSpeech.QUEUE_FLUSH, null);
                                    }
                                }
                            }else if (acces.equals("false")){
                                textToSpeech.speak("ouups ! "+value+" plug access denied", TextToSpeech.QUEUE_FLUSH, null);
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

    /*---------------------------------------------------------------------------------*/
    /*Fenetre---------------------------------------------------------------------------------*/
    private void getFenetreId(String chambre, String action) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRefff2 = mFirebaseDatabase.getReference("/actionneurs/fenetre");
        // Read from the database
        Query query = myRefff2.orderByKey();
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
                            String key = String.valueOf(next.getKey());

                            if (value.toLowerCase().equals(chambre)){
                                Log.i("leeeeeed : ",value);
                                Log.i("keyyyy : ",key);
                                Log.i("statuuuusss : ",status);
                                ledId = key;
                                checkAccessFenetre(key,action);
                            }


                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
    }
    private void checkAccessFenetre(String key, String action) {
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
                            if (value.toLowerCase().equals(loadsEmail)){
                                String path = "/users/"+value2+"/Fenetres";
                                getFenetres(path,key,action);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
    }
    private void getFenetres(String emailID, String key, String action) {

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
                            String keyy = String.valueOf(next.getKey());
                            String value = String.valueOf(next.child("").child("access").getValue());
                            if (keyy.equals(key)){
                                Log.i("value key",value);
                                changeFenetreValue(key,value,action);
                            }





                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );

    }
    public void changeFenetreValue(String key,String acces, String action){
        myRefff3 = mFirebaseDatabase.getReference("/actionneurs/fenetre");
        // Read from the database
        Query query = myRefff3.orderByKey();
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
                            String keyy = String.valueOf(next.getKey());
                            if ((keyy.equals(key))&&(acces.equals("true"))){
                                Log.i("heyyyyy : ","doneeee");
                                if (action.equals("open")){
                                    if (status.equals("open")){
                                        textToSpeech.speak("window already opened", TextToSpeech.QUEUE_FLUSH, null);
                                    }else{
                                        myRefff3.child(keyy).child("status").setValue("open");
                                        addFENETREHistoryToFirebase("was opened "+value+" window.");
                                        textToSpeech.speak("window opened successfully", TextToSpeech.QUEUE_FLUSH, null);
                                    }
                                }else if (action.equals("close")){
                                    if (status.equals("close")){
                                        textToSpeech.speak("window already closed", TextToSpeech.QUEUE_FLUSH, null);
                                    }else{
                                        myRefff3.child(keyy).child("status").setValue("close");
                                        addFENETREHistoryToFirebase("was closed "+value+" window.");
                                        textToSpeech.speak("window closed successfully", TextToSpeech.QUEUE_FLUSH, null);
                                    }
                                }
                            }else if (acces.equals("false")){
                                textToSpeech.speak("ouups ! "+value+" window access denied", TextToSpeech.QUEUE_FLUSH, null);
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

    /*---------------------------------------------------------------------------------*/
    /*Ventilateur ---------------------------------------------------------------------------------*/
    private void getVentilateurId(String chambre, String action) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRefff4 = mFirebaseDatabase.getReference("/actionneurs/ventilateur");
        // Read from the database
        Query query = myRefff4.orderByKey();
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
                            String key = String.valueOf(next.getKey());

                            if (value.toLowerCase().equals(chambre)){
                                Log.i("leeeeeed : ",value);
                                Log.i("keyyyy : ",key);
                                Log.i("statuuuusss : ",status);
                                ledId = key;
                                checkAccessVentilateur(key,action);
                            }


                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
    }
    private void checkAccessVentilateur(String key, String action) {
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
                            if (value.toLowerCase().equals(loadsEmail)){
                                String path = "/users/"+value2+"/Ventilateurs";
                                getVentilateur(path,key,action);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
    }
    private void getVentilateur(String emailID, String key, String action) {

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
                            String keyy = String.valueOf(next.getKey());
                            String value = String.valueOf(next.child("").child("access").getValue());
                            if (keyy.equals(key)){
                                Log.i("value key",value);
                                changeVentilateurValue(key,value,action);
                            }





                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );

    }
    public void changeVentilateurValue(String key,String acces, String action){
        myRefff4 = mFirebaseDatabase.getReference("/actionneurs/ventilateur");
        // Read from the database
        Query query = myRefff4.orderByKey();
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
                            String keyy = String.valueOf(next.getKey());
                            if ((keyy.equals(key))&&(acces.equals("true"))){
                                Log.i("heyyyyy : ","doneeee");
                                if (action.equals("on")){
                                    if (status.equals("on")){
                                        textToSpeech.speak("fan already on", TextToSpeech.QUEUE_FLUSH, null);
                                    }else{
                                        myRefff4.child(keyy).child("status").setValue("on");
                                        addVENTILATEURHistoryToFirebase("was turned on "+value+" fan.");
                                        textToSpeech.speak("fan turned on", TextToSpeech.QUEUE_FLUSH, null);
                                    }
                                }else if (action.equals("off")){
                                    if (status.equals("off")){
                                        textToSpeech.speak("fan turned off", TextToSpeech.QUEUE_FLUSH, null);
                                    }else{
                                        myRefff4.child(keyy).child("status").setValue("off");
                                        addVENTILATEURHistoryToFirebase("was turned off "+value+" fan.");
                                        textToSpeech.speak("fan turned off", TextToSpeech.QUEUE_FLUSH, null);
                                    }
                                }
                            }else if (acces.equals("false")){
                                textToSpeech.speak("ouups ! "+value+" fan access denied", TextToSpeech.QUEUE_FLUSH, null);
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

    /*---------------------------------------------------------------------------------*/
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    private void listeningBTN() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
        Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        if (count==0){
            speechRecognizer.startListening(speechRecognizerIntent);
            count=1;
        }else{
            speechRecognizer.stopListening();
            count=0;
        }
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {


            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> data = results.getStringArrayList(speechRecognizer.RESULTS_RECOGNITION);
                txvResult.setText(data.get(0));
                String myTxt = txvResult.getText().toString();




                if (language.equals(new Locale("en").getLanguage())){
                    if(myTxt.contains("Genesis")) {
                        if (myTxt.toLowerCase().contains("turn")&&myTxt.toLowerCase().contains("on")
                                ||myTxt.toLowerCase().contains("on")||myTxt.toLowerCase().contains("switch on"))
                        {
                            if (myTxt.toLowerCase().contains("light")||myTxt.toLowerCase().contains("lights")){
                                String str = myTxt.toLowerCase();
                                int index = str.indexOf("on");
                                int endIndex = str.indexOf("light");
                                String who2 = str.substring(index+3, endIndex-1);
                                Log.i("whooooo",who2);
                                getLedId(who2,"on");
                            }
                            else if(myTxt.toLowerCase().contains("plug")||myTxt.toLowerCase().contains("plugs")){
                                String str = myTxt.toLowerCase();
                                int index = str.indexOf("on");
                                int endIndex = str.indexOf("plug");
                                String who2 = str.substring(index+3, endIndex-1);
                                Log.i("whooooo",who2);
                                getPriseId(who2,"on");
                            }
                            else if(myTxt.toLowerCase().contains("fan")||myTxt.toLowerCase().contains("fans")){
                                String str = myTxt.toLowerCase();
                                int index = str.indexOf("on");
                                int endIndex = str.indexOf("fan");
                                String who2 = str.substring(index+3, endIndex-1);
                                Log.i("whooooo",who2);
                                getVentilateurId(who2,"on");
                            }
                        }
                        else if(myTxt.toLowerCase().contains("off")||myTxt.toLowerCase().contains("switch off"))
                        {
                            if (myTxt.toLowerCase().contains("light")||myTxt.toLowerCase().contains("lights"))
                            {
                                String str = myTxt.toLowerCase();
                                int index = str.indexOf("off");
                                int endIndex = str.indexOf("light");
                                String who2 = str.substring(index+4, endIndex-1);
                                Log.i("whooooo",who2);
                                getLedId(who2,"off");
                            }
                            else if(myTxt.toLowerCase().contains("plug")||myTxt.toLowerCase().contains("plugs")){
                                String str = myTxt.toLowerCase();
                                int index = str.indexOf("off");
                                int endIndex = str.indexOf("plug");
                                String who2 = str.substring(index+4, endIndex-1);
                                Log.i("whooooo",who2);
                                getPriseId(who2,"off");
                            }
                            else if(myTxt.toLowerCase().contains("fan")||myTxt.toLowerCase().contains("fans")){
                                String str = myTxt.toLowerCase();
                                int index = str.indexOf("off");
                                int endIndex = str.indexOf("fan");
                                String who2 = str.substring(index+4, endIndex-1);
                                Log.i("whooooo",who2);
                                getVentilateurId(who2,"off");
                            }
                        }
                        else if(myTxt.toLowerCase().contains("open")&&myTxt.toLowerCase().contains("window"))
                        {
                            String str = myTxt.toLowerCase();
                            int index = str.indexOf("open");
                            int endIndex = str.indexOf("window");
                            String who2 = str.substring(index+5, endIndex-1);
                            Log.i("whooooo",who2);
                            getFenetreId(who2,"open");
                        }
                        else if(myTxt.toLowerCase().contains("close")&&myTxt.toLowerCase().contains("window"))
                        {
                            String str = myTxt.toLowerCase();
                            int index = str.indexOf("close");
                            int endIndex = str.indexOf("window");
                            String who2 = str.substring(index+6, endIndex-1);
                            Log.i("whooooo",who2);
                            getFenetreId(who2,"close");
                        }


                        else if (myTxt.toLowerCase().contains("email")){
/*
                        int index = myTxt.indexOf("to");
                        String who = myTxt.substring(index+2, myTxt.length());
                        Log.i("email for :",who);
                        int index2 = myTxt.indexOf("subject");
                        int index3 = myTxt.indexOf("subject");
                        String subject = myTxt.substring(index2+10, myTxt.length());
                        Log.i("subject :",subject);
*/
                            countHandler++;
                            askForWho();
                            askSubject();
                            askContent();
                            beforeSend();
                        }
                        else if (myTxt.toLowerCase().contains("call")){
                            textToSpeech.speak("Ok sir, wait a moment please", TextToSpeech.QUEUE_FLUSH, null);
                            int index = myTxt.indexOf("call");
                            String who2 = myTxt.substring(index+5, myTxt.length());
                            searchContactByName(who2);
                            handler2 = new Handler();
                            handler2.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    //call(phoneList.get(phoneList.size()-1).toString());
                                    call(phoneNumber);

                                }
                            }, 6000);
                        }
                        else{
                            textToSpeech.speak("oops, I didn't catch that, repeat your command please!", TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                }




            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }

        });
    }

    private void searchContactByName(String who2) {
        //
//  Find contact based on name.
//
        phoneNumber="";
        if (!who2.isEmpty()){
            String NAME = who2;
            ContentResolver cr = getActivity().getContentResolver();
            Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                    "DISPLAY_NAME = '" + NAME + "'", null, null);
            if (cursor.moveToFirst()) {
                String contactId =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                //
                //  Get all phone numbers.
                //
                Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                while (phones.moveToNext()) {
                    String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Log.i("Number = ",number);

                    phoneNumber = number;
                    Log.i("PhooneNumber = ",phoneNumber);
                }
                phones.close();
            }
            else {
                if(language.equals(new Locale("en").getLanguage())){
                    textToSpeech.speak("i didn't found this phone number, you should save it in your contacts list", TextToSpeech.QUEUE_FLUSH, null);
                }
                if(language.equals(new Locale("ar").getLanguage())){
                    textToSpeech.speak("الرقم غير موجود, الرجاء التاكد من وجوده في جهة الاتصال", TextToSpeech.QUEUE_FLUSH, null);
                }
                if(language.equals(new Locale("fr").getLanguage())){
                    textToSpeech.speak("le numéro de tétéphone est introuvale, vous devez l'enregistrer dans votre liste des contactes ", TextToSpeech.QUEUE_FLUSH, null);

                }
            }
            cursor.close();

        }else{
            if(language.equals(new Locale("en").getLanguage())){
                Toast.makeText(getContext(), "Phone number invalid", Toast.LENGTH_SHORT).show();            }
            if(language.equals(new Locale("ar").getLanguage())){
                Toast.makeText(getContext(), "خطأ في صيغة الرقم", Toast.LENGTH_SHORT).show();            }
            if(language.equals(new Locale("fr").getLanguage())){
                Toast.makeText(getContext(), "Numéro de téléphone invqlide", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void call(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+phoneNumber));
        startActivity(callIntent);
    }



    public ArrayList<String> searchPhoneNumberContact2(String searchedContact) {

        nameList = new ArrayList<String>();
        phoneList = new ArrayList<String>();
        emailList = new ArrayList<String>();

        ContentResolver cr = getActivity().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                null, null, null);




        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //Log.i("Name",  name);
                        //Log.i("Phone Number: ", phoneNo);
                        if ((phoneNo.toLowerCase().contains(searchedContact))){
                            Log.i("Searched phoneNumber : ",phoneNo);
                            phoneList.add(phoneNo); // Here you will get list of email
                        }
                    }
                    pCur.close();
                }
            }

        }
        if(cur!=null){
            cur.close();
        }




        return phoneList; // here you can return whatever you want.
    }

    public ArrayList<String> searchPhoneNumberContact(String searchedContact) {

        nameList = new ArrayList<String>();
        phoneList = new ArrayList<String>();
        emailList = new ArrayList<String>();

        ContentResolver cr = getActivity().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur
                        .getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur
                        .getString(cur
                                .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer
                        .parseInt(cur.getString(cur
                                .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    // Query phone here. Covered next

                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                    + " = ?", new String[] { id }, null);
                    while (pCur.moveToNext()) {
                        // Do something with phones
                        String phoneNo = pCur
                                .getString(pCur
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        nameList.add(name); // Here you can list of contact.
                        phoneList.add(phoneNo); // Here you will get list of phone number.


                        Cursor emailCur = cr.query(
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (emailCur.moveToNext()) {
                            String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                            if (email.toLowerCase().contains(searchedContact)){
                                Log.i("Searched email : ",email);
                                Log.i("Phone number : ",phoneNo);
                                emailList.add(email); // Here you will get list of email
                            }
                        }
                        emailCur.close();
                    }
                    pCur.close();
                }
            }
        }

        return emailList; // here you can return whatever you want.
    }


    protected void sendEmail(String receiver,String s,String c) {

            String adress = "vocaleassistant@gmail.com";
            String password = "hassenmahdi123";

            Properties props = new Properties();
            props.put("mail.smtp.auth","true");
            props.put("mail.smtp.starttls.enable","true");
            props.put("mail.smtp.host","smtp.gmail.com");
            props.put("mail.smtp.port","587");
            Session session = Session.getInstance(props, new javax.mail.Authenticator(){
                @Override
                protected PasswordAuthentication getPasswordAuthentication(){
                    return new PasswordAuthentication(adress,password);
                }
            });
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(adress));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("hassenamri005@gmail.com"));
                message.setSubject(s);
                message.setText(c);
                Transport.send(message);
                Toast.makeText(getContext(), "Email sent successfully.", Toast.LENGTH_LONG).show();
            } catch (AddressException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }

        countHandler=0;


    }

    private void askForWho() {
        handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 10000ms
                if(language.equals(new Locale("ar").getLanguage())){
                    textToSpeech.speak("الى من تريد ارساله ؟", TextToSpeech.QUEUE_FLUSH, null);
                }else if(language.equals(new Locale("fr").getLanguage())){
                    textToSpeech.speak("Pour qui ?", TextToSpeech.QUEUE_FLUSH, null);
                }else if (language.equals(new Locale("en").getLanguage())){
                    textToSpeech.speak("For who sir ?", TextToSpeech.QUEUE_FLUSH, null);
                }

            }
        }, 2000);
        handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 10000ms
                //textToSpeech.speak("i will search it for you sir", TextToSpeech.QUEUE_FLUSH, null);
                listening();
                receiver = txvResult.getText().toString();
                Log.i("Receiver :",receiver);

            }
        }, 4000);


    }

    private void askSubject() {

            handler2 = new Handler();
            handler2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 10000ms
                    receiver = txvResult.getText().toString();
                    Log.i("Receiver 2222 :",receiver);
                    if(language.equals(new Locale("ar").getLanguage())){
                        textToSpeech.speak("حول أيّ موضوع ؟ ?", TextToSpeech.QUEUE_FLUSH, null);
                    }else if(language.equals(new Locale("fr").getLanguage())){
                        textToSpeech.speak("Quel sujet dans votre esprit ?", TextToSpeech.QUEUE_FLUSH, null);
                    }else if (language.equals(new Locale("en").getLanguage())){
                        textToSpeech.speak("What subject in your mind ?", TextToSpeech.QUEUE_FLUSH, null);
                    }

                }
            }, 8000);

            handler2 = new Handler();
            handler2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 10000ms
                    //textToSpeech.speak("i will search it for you sir", TextToSpeech.QUEUE_FLUSH, null);
                    listening();
                    subject = txvResult.getText().toString();
                    Log.i("Subject : ",subject);
                }
            }, 11000);



    }
    private void askContent() {

            handler2 = new Handler();
            handler2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 10000ms
                    subject = txvResult.getText().toString();
                    Log.i("Subject 2222 : ",subject);
                    if(language.equals(new Locale("ar").getLanguage())){
                        textToSpeech.speak("ماذا عليّ ان اقول ؟", TextToSpeech.QUEUE_FLUSH, null);
                    }else if(language.equals(new Locale("fr").getLanguage())){
                        textToSpeech.speak("Qu'est-ce que je devrais dire ?", TextToSpeech.QUEUE_FLUSH, null);
                    }else if (language.equals(new Locale("en").getLanguage())){
                        textToSpeech.speak("What should i say ?", TextToSpeech.QUEUE_FLUSH, null);
                    }

                }
            }, 17000);
            handler2 = new Handler();
            handler2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 10000ms
                    //textToSpeech.speak("i will search it for you sir", TextToSpeech.QUEUE_FLUSH, null);
                    listening();
                    content = txvResult.getText().toString();
                    Log.i("cONTENT : ",content);
                }
            }, 20000);


        countHandler++;

    }
    private void beforeSend() {

        handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 10000ms
                content = txvResult.getText().toString();
                Log.i("cONTENT 2222 : ",content);
                addHistoryToFirebase(myLoggedEmail+" send an email to "+receiver);
                sendEmail(receiver,subject,content);
                if(language.equals(new Locale("ar").getLanguage())){
                    textToSpeech.speak("تم التحقق من الايميل وارساله.", TextToSpeech.QUEUE_FLUSH, null);
                }else if(language.equals(new Locale("fr").getLanguage())){
                    textToSpeech.speak("vérification de mail et l'envoi ont effectués avec succès.", TextToSpeech.QUEUE_FLUSH, null);
                }else if (language.equals(new Locale("en").getLanguage())){
                    textToSpeech.speak("checking email and sending are done.", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        }, 30000);



        countHandler++;

    }

    private void listening() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
        Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizer.startListening(speechRecognizerIntent);


        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                data2 = results.getStringArrayList(speechRecognizer.RESULTS_RECOGNITION);
                listenedText =  data2.get(0).toString();
                txvResult.setText(data2.get(0));
                countHandler++;

            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
    }



    private void addHistoryToFirebase(String text){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
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
        String action = email+" "+text;
        myRef.child("history").child(key).child("action").setValue(action);
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

    private void getHumFirebaseValue() {
        myReff = FirebaseDatabase.getInstance().getReference().child("Hum");
        // User data change listener
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                String hm = dataSnapshot.getValue().toString();
                int x = Integer.parseInt(hm);
                animateProgressBarHumidity(x);

                hum.setText(hm);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        };
        myReff.addValueEventListener(postListener);

    }

    private void animateProgressBarHumidity(int x) {
        if (progressStatus2 == 100) {
            progressStatus2 = 0;
        }
        if (progressStatus2<x){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (progressStatus2 < x+1) {
                        // Update the progress status
                        progressStatus2 += 1;
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
                                pbHum.setProgress(progressStatus2);
                            }
                        });
                    }
                }
            }).start(); // Start the operation
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (progressStatus2 > x) {
                        // Update the progress status
                        progressStatus2 -= 1;
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
                                pbHum.setProgress(progressStatus2);
                            }
                        });
                    }
                }
            }).start(); // Start the operation
        }
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
                animateProgressBarGas(x);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        };
        myReff.addValueEventListener(postListener);

    }

    private void animateProgressBarGas(int x) {
        if (progressStatus3 == 100) {
            progressStatus3 = 0;
        }
        if (progressStatus3<x){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (progressStatus3 < x+1) {
                        // Update the progress status
                        progressStatus3 += 1;
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
                                pbGas.setProgress(progressStatus3);
                            }
                        });
                    }
                }
            }).start(); // Start the operation
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (progressStatus3 > x) {
                        // Update the progress status
                        progressStatus3 -= 1;
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
                                pbGas.setProgress(progressStatus3);
                            }
                        });
                    }
                }
            }).start(); // Start the operation
        }
    }









}