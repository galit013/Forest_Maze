package com.example.forestmaze;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ChangeProfileImg extends AppCompatActivity {

    ImageView profile_img;
    ImageButton gallery, camera;
    Bitmap bitmap = null;
    Uri filepath;
    File file = null;

    private static final int CAMERA_REQUEST_CODE = 0;
    private static final int GALLERY_REQUEST_CODE = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_img);

        // the screen cannot be turned
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        gallery = findViewById(R.id.gallery);
        camera = findViewById(R.id.camera);
        profile_img = findViewById(R.id.profile_img);

        // click on gallery button
        gallery.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // open gallery
                Intent intentGallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentGallery, GALLERY_REQUEST_CODE);
            }
        });

        // click on camera button
        camera.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View v) {
                // open camera
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        });

    }

    @Override
    // create bitmap to display image on ImageView from gallery or camera
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch(requestCode){
                case GALLERY_REQUEST_CODE:
                    filepath = data.getData();
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(filepath));
                        profile_img.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 700, 700, false));

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;

                case CAMERA_REQUEST_CODE:
                    bitmap = (Bitmap) data.getExtras().get("data");
                    profile_img.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 700, 700, false));
                    break;
            }
        }
    }

    // click the x button
    public void onClickCancel(View view) {
        finish();
    }

    // click save button
    public void onClickSave(View view) {
        // create bitmap from image file
        if(bitmap != null) {
            file = new File(getFilesDir(), CurrentUser.currentUser + ".png");
            try {
                OutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Store the file path and file name in a shared preference
            SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(CurrentUser.currentUser, file.getPath());
            editor.commit();
        }

        // if theres no image file save an empty profile image
        else{SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(CurrentUser.currentUser, null);
            editor.commit();
        }

        Toast.makeText(getApplicationContext(), "saved", Toast.LENGTH_LONG).show();
    }
}