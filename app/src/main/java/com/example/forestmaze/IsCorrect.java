package com.example.forestmaze;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class IsCorrect extends AppCompatActivity {

    TextView message_title, explanation, score_tv;
    Button next, try_again;
    String isCorrect, explanation_text;
    String userName = CurrentUser.currentUser;
    int level, score;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_is_correct);

        // the screen cannot be turned
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        message_title = findViewById(R.id.message_title);
        explanation = findViewById(R.id.explanation);
        score_tv = findViewById(R.id.score);
        next = findViewById(R.id.next);
        try_again = findViewById(R.id.try_again);

        // get bool value for right door
        Intent intent = getIntent();
        isCorrect = intent.getStringExtra("isCorrect");

        db = FirebaseFirestore.getInstance();

        // if its the right door
        if (isCorrect.equals("true")){
            message_title.setText("Good Job! You answered correctly!");
            explanation_text = intent.getStringExtra("textForAnswer");
            explanation.setText(explanation_text);
            score_tv.setText("You can see your score on the Game Map screen");

            next.setVisibility(View.VISIBLE);
            try_again.setVisibility(View.INVISIBLE);
        }
        // if its a wrong door
        else{
            message_title.setText("Oh No! You picked the wrong door. Try Again!");
            explanation.setText("Get back to the same level to try and escape again\n Don't give up!");
            score_tv.setText("");

            next.setVisibility(View.INVISIBLE);
            try_again.setVisibility(View.VISIBLE);
        }

        // click try again button
        try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IsCorrect.this, Level.class));
            }
        });


    }

    // click next button
    public void onClickNext(View view) {
        DocumentReference docRef = db.collection("users").document(userName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // get users level and score from database
                    DocumentSnapshot document = task.getResult();
                    level = Integer.valueOf(document.getData().get("level").toString());
                    score = Integer.valueOf(document.getData().get("score").toString());
                }
            }
        });

        // update the score in database based on the number of tries
        if (CurrentUser.tries == 0) {
            docRef.update("score", FieldValue.increment(10)).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // The update was successful
                    Log.d(TAG, "updated");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle any errors that occurred
                    Log.d(TAG, "error");
                }
            });
        }
        if (CurrentUser.tries == 1) {
            docRef.update("score", FieldValue.increment(8)).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // The update was successful
                    Log.d(TAG, "updated");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle any errors that occurred
                    Log.d(TAG, "error");
                }
            });
        }
        if (CurrentUser.tries == 2) {
            docRef.update("score", FieldValue.increment(5)).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // The update was successful
                    Log.d(TAG, "updated");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle any errors that occurred
                    Log.d(TAG, "error");
                }
            });
        }

        // reset number of tries
        CurrentUser.tries = 0;

        // update the level in database
        docRef.update("level", FieldValue.increment(1)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (level == 11){startActivity(new Intent(IsCorrect.this, FinishScreen.class));}
                else{startActivity(new Intent(IsCorrect.this, GameMap.class));}
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "error");
            }
        });
    }
}