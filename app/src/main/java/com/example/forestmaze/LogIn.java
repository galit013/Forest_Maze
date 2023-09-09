package com.example.forestmaze;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.net.InternetDomainName;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LogIn extends AppCompatActivity {

    FirebaseFirestore db;

    TextView nameCheck, passwordCheck, register;
    EditText name, password;
    Button submit;

    String correct_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // the screen cannot be turned
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        name = findViewById(R.id.userName);
        password = findViewById(R.id.userPassword);
        register = findViewById(R.id.register);
        nameCheck = findViewById(R.id.userNameCheck);
        passwordCheck = findViewById(R.id.userPasswordCheck);
        submit = findViewById(R.id.submit);

        // click register text
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogIn.this, SignUp.class));
            }
        });

    }

    public void submit(View view){
        // if details are correct
        if (name.length() != 0 && password.length() != 0 && (name.length() >= 3 && name.length() <= 20) && (password.length() >= 5 && password.length() <= 10)) {

            db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(name.getText().toString());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                // check if details match data in database
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            correct_password = document.getData().get("password").toString();
                            if(correct_password.equals(password.getText().toString())){
                                CurrentUser.currentUser = name.getText().toString();
                                startActivity(new Intent(LogIn.this, GameMap.class));
                            }
                            else{
                                passwordCheck.setText("wrong password");
                            }
                        } else {
                            nameCheck.setText("such name doesn't exists");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

        }
        // display detail errors
        else {
            if (name.length() == 0) {
                nameCheck.setText("enter your name");
            } else {
                if (name.length() < 3) {
                    nameCheck.setText("at least 3 characters long");
                }
                if (name.length() > 20) {
                    nameCheck.setText("at most 20 characters long");
                }
            }

            if (password.length() == 0) {
                passwordCheck.setText("enter your password");
            } else {
                if (password.length() < 5) {
                    passwordCheck.setText("at least 5 characters long");
                }
                if (password.length() > 10) {
                    passwordCheck.setText("at most 10 characters long");
                }
            }
        }
    }

}



