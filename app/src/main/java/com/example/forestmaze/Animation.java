package com.example.forestmaze;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;

public class Animation extends AppCompatActivity {

    // the image view that will display the animation
    ImageView forestImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        // the screen cannot be turned
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        forestImg = findViewById(R.id.forest_img); // image view

        // new thread for displaying animation
        new Thread(new Runnable() {
            @Override
            public void run() {
                // loop for 45 images
                for(int i=1;i<=45;i++)
                {
                    // get current relevant img from drawable
                    String imgName = "img" + i;
                    int resId = getResources().getIdentifier(imgName, "drawable", getPackageName());
                    // set relevant img in imageView
                    forestImg.setImageResource(resId);
                    // sleep for 200 mill sec
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over - go to log in screen
                Intent i = new Intent(Animation.this, LogIn.class);
                startActivity(i);
            }
        }, 10000);
    }
}
