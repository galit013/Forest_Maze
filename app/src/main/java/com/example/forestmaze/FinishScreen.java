package com.example.forestmaze;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;

public class FinishScreen extends AppCompatActivity {

    FirebaseFirestore db;
    String userName = CurrentUser.currentUser;
    Object score;
    TextView summary_text, user_name_tv;
    ImageView profile_iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_screen);

        // the screen cannot be turned
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        summary_text = findViewById(R.id.summary_text);
        user_name_tv = findViewById(R.id.user_name_tv);
        profile_iv = findViewById(R.id.profile_iv);

        user_name_tv.setText(userName);

        // Retrieve the file path and file name from the shared preference
        SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
        String filePath = prefs.getString(userName, null);

        // Load the bitmap image from the saved file
        if (filePath != null) {
            File file = new File(filePath);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            // show on screen on ImageView
            profile_iv.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 100, 100, false));
        }
        else{profile_iv.setImageResource(R.drawable.empty_profile);}

        db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("users").document(userName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // get from database the score
                        score = document.getData().get("score");
                        summary_text.setText("Your total score is " + score.toString() + ". \n Go to the menu to see if you made it to the leader board!");
                    }
                }
            }
        });
    }

    public void onClickRateGame(View view) {
        // show the rate dialog
        DialogRateGame dialog = new DialogRateGame();
        dialog.show(getSupportFragmentManager(), "Rate Game");
    }

    public void onClickLogOut(View view) {
        // go to log in activity
        startActivity(new Intent(FinishScreen.this, LogIn.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // create menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // check which menu item was clicked
        int id = item.getItemId();
        switch (id) {
            case R.id.top_players:
                startActivity(new Intent(FinishScreen.this, LeaderBoard.class));
                return true;
            case R.id.how_to_play:
                CurrentUser.page_number = 1;
                startActivity(new Intent(FinishScreen.this, Instructions.class));
                return true;
            case R.id.settings:
                Intent intent = new Intent(FinishScreen.this, Settings.class);
                intent.putExtra("previous_activity", "Finish");
                startActivity(intent);
                return true;
            case R.id.log_out:
                startActivity(new Intent(FinishScreen.this, LogIn.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}