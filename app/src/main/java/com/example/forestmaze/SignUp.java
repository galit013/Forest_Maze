package com.example.forestmaze;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    EditText userName, userPassword;
    TextView userNameCheck, userPasswordCheck, login;
    ImageButton galleryBtn, cameraBtn;
    ImageView imgViewProfile;
    Uri filepath;
    FirebaseFirestore db;
    Bitmap bitmap = null;
    File file;

    private static final int MY_CAMERA_PERMISSION_CODE = 2;
    private static final int MY_GALLERY_PERMISSION_CODE = 3;
    private static final int CAMERA_REQUEST_CODE = 0;
    private static final int GALLERY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // the screen cannot be turned
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        userName = findViewById(R.id.name);
        userPassword = findViewById(R.id.password);
        userNameCheck = findViewById(R.id.userNameCheck);
        userPasswordCheck = findViewById(R.id.userPasswordCheck);
        login = findViewById(R.id.login);

        galleryBtn = findViewById(R.id.galleryBtn);
        cameraBtn = findViewById(R.id.cameraBtn);
        imgViewProfile = findViewById(R.id.imgViewProfile);

        // click gallery button
        galleryBtn.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check permission and open gallery
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_GALLERY_PERMISSION_CODE);
                }
                else
                {
                    Intent intentGallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intentGallery, GALLERY_REQUEST_CODE);
                }
            }
        });

        // click camera button
        cameraBtn.setOnClickListener(new ImageButton.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                // check permission and open camera
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                }
            }
        });

        // click log in text
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, LogIn.class));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // create bitmap to display image on ImageView from gallery or camera
        if (resultCode == RESULT_OK) {
            switch(requestCode){
                case GALLERY_REQUEST_CODE:
                    filepath = data.getData();
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(filepath));
                        imgViewProfile.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 700, 700, false));

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;

                case CAMERA_REQUEST_CODE:
                    bitmap = (Bitmap) data.getExtras().get("data");
                    imgViewProfile.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 700, 700, false));
                    break;
            }
        }
    }

    // click sign up button
    public void signUp(View view) {
        userNameCheck.setText("");
        userPasswordCheck.setText("");
        // if details are correct
        if (userName.length() != 0 && userPassword.length() != 0 && (userName.length() >= 3 && userName.length() <= 20) && (userPassword.length() >= 5 && userPassword.length() <= 10)) {
            db = FirebaseFirestore.getInstance();

            // create bitmap from image file to save profile picture
            if(bitmap != null) {
                file = new File(getFilesDir(), userName.getText().toString() + ".png");
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
                editor.putString(userName.getText().toString(), file.getPath());
                editor.commit();
            }

            // save new user in database
            DocumentReference docRef = db.collection("users").document(userName.getText().toString());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            userNameCheck.setText("this user name is already taken");
                        } else {
                            db.collection("users")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                            if (task.isSuccessful()) {
                                                Map<String, Object> currentUser = new HashMap<>();
                                                currentUser.put("name", userName.getText().toString());
                                                currentUser.put("password", userPassword.getText().toString());
                                                currentUser.put("level", 1);
                                                currentUser.put("score", 0);
                                                currentUser.put("rating", 0);

                                                db.collection("users").document(userName.getText().toString()).set(currentUser);

                                                CurrentUser.currentUser = userName.getText().toString();

                                                startActivity(new Intent(SignUp.this, GameMap.class));

                                            } else {
                                                Log.w(TAG, "Error getting documents.", task.getException());
                                            }
                                        }
                                    });
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });


        }
        // display detail errors
        else {
            if (userName.length() == 0) {
                userNameCheck.setText("enter your name");
            } else {
                if (userName.length() < 3) {
                    userNameCheck.setText("at least 3 characters long");
                }
                if (userName.length() > 20) {
                    userNameCheck.setText("at most 20 characters long");
                }
            }

            if (userPassword.length() == 0) {
                userPasswordCheck.setText("enter your password");
            } else {
                if (userPassword.length() < 5) {
                    userPasswordCheck.setText("at least 5 characters long");
                }
                if (userPassword.length() > 10) {
                    userPasswordCheck.setText("at most 10 characters long");
                }
            }
        }
    }
}