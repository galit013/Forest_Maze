package com.example.forestmaze;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

public class GameMap extends AppCompatActivity {

    FirebaseFirestore db;
    TextView userNametv, userScoretv;
    ImageView locationIcon, playBtn, userProfileImgView;

    String userName = CurrentUser.currentUser;
    Object score, level;

    // location array for the x and y position of the location image
    int[][] locations_array = {
            {10, 1380},
            {420, 1300},
            {655, 1050},
            {240, 980},
            {12, 810},
            {520, 650},
            {40, 460},
            {180, 40},
            {405, 395},
            {680, 80}
    };

    private static int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_map);

        // the screen cannot be turned
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        userNametv = findViewById(R.id.userNameTextView);
        userScoretv = findViewById(R.id.userScoreTextView);
        locationIcon = findViewById(R.id.locationIcon);
        playBtn = findViewById(R.id.playBtn);
        userProfileImgView = findViewById(R.id.userProfileImgView);

        userScoretv.setX(60);

        db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("users").document(userName);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // if the user is on level 11, show finish screen
                        level = document.getData().get("level");
                        if(level.toString().equals("11")){
                            startActivity(new Intent(GameMap.this, FinishScreen.class));
                        }
                        // if not, continue
                        else {
                            score = document.getData().get("score");
                            userScoretv.setText("SCORE: " + score.toString());

                            // show dialog for a new user
                            if (level.toString().equals("1")) {
                                showCustomDialog();
                            }

                            locationIcon.setX(locations_array[Integer.valueOf(level.toString()) - 1][0]);
                            locationIcon.setY(locations_array[Integer.valueOf(level.toString()) - 1][1]);

                            // Retrieve the file path and file name from the shared preference
                            SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
                            String filePath = prefs.getString(userName, null);

                            // Load the bitmap image from the saved file
                            if (filePath != null) {
                                File file = new File(filePath);
                                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                                // Use the loaded bitmap image as needed
                                userProfileImgView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 300, 300, false));
                            }
                            else{userProfileImgView.setImageResource(R.drawable.empty_profile);}

                            if (ContextCompat.checkSelfPermission(GameMap.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(GameMap.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                            }
                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        userNametv.setText(userName);

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
        switch (id){
            case R.id.top_players:
                startActivity(new Intent(GameMap.this, LeaderBoard.class));
                return true;
            case R.id.how_to_play:
                CurrentUser.page_number = 1;
                startActivity(new Intent(GameMap.this, Instructions.class));
                return true;
            case R.id.settings:
                Intent intent = new Intent(GameMap.this, Settings.class);
                intent.putExtra("previous_activity", "Map");
                startActivity(intent);
                return true;
            case R.id.log_out:
                startActivity(new Intent(GameMap.this, LogIn.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Function to display the custom dialog.
    void showCustomDialog() {
        final Dialog dialog = new Dialog(GameMap.this);
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(false);
        //Mention the name of the layout of your custom dialog.
        dialog.setContentView(R.layout.activity_custom_dialog);

        //Initializing the views of the dialog.
        final TextView greeting = dialog.findViewById(R.id.greetingtv);
        Button closeButton = dialog.findViewById(R.id.closebtn);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        greeting.setText("Hi " + userName);
        dialog.show();
    }

    // click play button
    public void onClickPlayBtn(View view) {
        startActivity(new Intent(GameMap.this, Level.class));
    }
}