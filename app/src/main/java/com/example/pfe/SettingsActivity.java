package com.example.pfe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import android.os.StrictMode;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.google.firebase.auth.FirebaseAuth;
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

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import javax.mail.internet.MimeMessage;

import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;

public class SettingsActivity extends AppCompatActivity {
    Button button;

    private ImageView lg;
    private TextView syslg;
    static String language;
    Intent intent;
    ComponentName cn;

    ArrayList<String> nameList,phoneList,emailList;
    TextView emailsender,autoemergency,helpcenter,about,textView8;


    Context context;
    static String phoneNumber;


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
    static String ledId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Intent intent=getIntent();
        loadsEmail = intent.getStringExtra("loadsEmail");
     /*   String str = loadsEmail;
        int index = str.indexOf("@");
        String who2 = str.substring(0, index);
        TextView textView7 = findViewById(R.id.textView7);
        textView7.setText("Hello, "+who2);
*/
// Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        myRefff = database.getReference("/actionneurs/leds");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);





        language = Locale.getDefault().getLanguage();


        autoemergency= findViewById(R.id.autoemergency);
        about = findViewById(R.id.about);
        helpcenter = findViewById(R.id.helpcenter);
        textView8 = findViewById(R.id.textView8);

        syslg = (TextView) findViewById(R.id.syslg);
        syslg.setText(language);
        lg = (ImageView) findViewById(R.id.imageView14);
        lg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeLanguage();
            }
        });

        if (language.equals(new Locale("ar").getLanguage())){
            textView8.setText("اللغة");
            emailsender.setText("المرسل");
            autoemergency.setText("الاتصال بالحماية تلقائيا");
            helpcenter.setText("مركز المساعدة");
            about.setText("حولنا");
        }
        TextView commande = (TextView) findViewById(R.id.commande);
        commande.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(),Commande.class);
                startActivity(intent);
            }
        });

        context = getApplicationContext();

    }
    /*---------------------------------------------------------------------------------*/
    private void getLedId(String chambre) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
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
                            String key = String.valueOf(next.getKey());

                            if (value.toLowerCase().equals(chambre)){
                                Log.i("leeeeeed : ",value);
                                Log.i("keyyyy : ",key);
                                Log.i("statuuuusss : ",status);
                                ledId = key;
                                checkAccess(key);
                            }


                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
    }
    private void checkAccess(String key) {
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
                                String path = "/users/"+value2+"/Leds";
                                getPrises(path,key);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
    }
    private void getPrises(String emailID, String key) {

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
                            String value = String.valueOf(next.child("").getValue());
                            if (keyy.equals(key)){
                                Log.i("value key",value);
                                changeLedValue(key,value);
                            }





                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );

    }
    public void changeLedValue(String key,String acces){
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
                                Log.i("heyyyyy : ","doneeee");
                                myRefff.child(keyy).child("status").setValue("on");
                            }else{
                                Log.i("nooooooooooooooooo : ","nooooooooooooooooo");
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
    private void searchContact2(String searchedContact) {
        //
//  Find contact based on name.
//
        String NAME = searchedContact;
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                "DISPLAY_NAME = '" + NAME + "'", null, null);
        if (cursor.moveToFirst()) {
            String contactId =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            //
            //
            //
            //
            Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
            while (phones.moveToNext()) {
                String email = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                Log.i("Email = ",email);

            }
            phones.close();
        }
        cursor.close();

    }

    private void searchContact(String searchedContact) {

        //
//  Find contact based on name.
//
        String NAME = "Father";
        ContentResolver cr = getContentResolver();
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
        cursor.close();

    }

    private void changeLanguage() {
        intent=new Intent(Intent.ACTION_MAIN,null);

        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        cn= new ComponentName("com.android.settings","com.android.settings.Settings$InputMethodAndLanguageSettingsActivity" );
        intent.setComponent(cn);
        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    private void call() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+96425454));
        startActivity(callIntent);
    }


    protected void sendEmail() {
        String adress = "vocaleassistant@gmail.com";
        String password = "hassenmahdi123";
        String subject = "Test email";
        String content = "hello this is a test email.";
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
            message.setSubject(subject);
            message.setText(content);
            Transport.send(message);
            Toast.makeText(this, "Emial sent successfully.", Toast.LENGTH_LONG).show();
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }


    }

    /* chercher un Email d'un contact

    ShowContact("achref");

    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
*/

    public ArrayList<String> searchEmailContact(String searchedContact) {

        nameList = new ArrayList<String>();
        phoneList = new ArrayList<String>();
        emailList = new ArrayList<String>();

        ContentResolver cr = getContentResolver();
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

                        //nameList.add(name); // Here you can list of contact.
                        //phoneList.add(phoneNo); // Here you will get list of phone number.


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
                                phoneList.add(phoneNo);
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



    public ArrayList<String> searchPhoneNumberContact(String searchedContact) {

        nameList = new ArrayList<String>();
        phoneList = new ArrayList<String>();
        emailList = new ArrayList<String>();

        ContentResolver cr = getContentResolver();
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
                            Log.i("Name",  name);
                            Log.i("Phone Number: ", phoneNo);
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

}