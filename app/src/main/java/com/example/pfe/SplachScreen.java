package com.example.pfe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SplachScreen extends AppCompatActivity {
    int progressStatus = 0;
    private Handler handler = new Handler();
    private ProgressBar progressBar;
    TextView textView;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splach_screen);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        intent =new Intent(SplachScreen.this,MainActivity.class);
        progressBar=(ProgressBar)findViewById(R.id.pb);
        progressBar.setProgress(0);
        textView=(TextView)findViewById(R.id.textView);
        textView.setText("0");

        animateProgressBar(100);
    }

    private void animateProgressBar(int x) {
        if (progressStatus == 100) {
            progressStatus = 0;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressStatus < x) {
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
                            textView.setText(progressStatus+" %");
                            progressBar.setProgress(progressStatus);
                        }
                    });



                }

                startActivity(intent);
                // close this activity
                finish();

            }
        }).start(); // Start the operation



    }

}