package com.example.pfe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MyProfile extends AppCompatActivity {
    private String loadsEmail;
    private TextView txt,txt2;
    private Button chnagePassword,changeEmail,deleteAccount;
    Button notif;
    static String language;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    
        Intent intent=getIntent();
        loadsEmail = intent.getStringExtra("email");

        txt = findViewById(R.id.textView);
        txt.setText(""+loadsEmail);
        txt2 = findViewById(R.id.textView2);

        String str = loadsEmail;
        int index = str.indexOf("@");
        String who2 = str.substring(0,index);
        txt2.setText(who2);

        //static String language;
        language = Locale.getDefault().getLanguage();

        if (language.equals(new Locale("ar").getLanguage())){
            getSupportActionBar().setTitle("اعدادت الحساب");

        }

    }


}