package com.example.forestmaze;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import org.checkerframework.checker.units.qual.C;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

public class Level extends AppCompatActivity {

    FirebaseFirestore db;
    Object score, level, description, answer, explanation;
    Object doorNum1, doorNum2, doorNum3;
    String userName = CurrentUser.currentUser;

    ImageView door1, door2, door3, img_door1, img_door2, img_door3, level_img;
    ImageView speaker_off, speaker_on, bird;
    TextView level_num, level_text;
    String level_num_doc;
    TextToSpeech txt_to_speech;

    int door_number_to_hide1 = CurrentUser.currentDoorNumber1;
    int door_number_to_hide2 = CurrentUser.currentDoorNumber2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        // the screen cannot be turned
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        door1 = findViewById(R.id.door1);
        door2 = findViewById(R.id.door2);
        door3 = findViewById(R.id.door3);
        img_door1 = findViewById(R.id.img_door1);
        img_door2 = findViewById(R.id.img_door2);
        img_door3 = findViewById(R.id.img_door3);
        speaker_off = findViewById(R.id.speaker_off);
        speaker_on = findViewById(R.id.speaker_on);
        level_num = findViewById(R.id.level_num_tv);
        level_text = findViewById(R.id.level_text);
        speaker_off.setVisibility(View.INVISIBLE);
        level_img = findViewById(R.id.levelImg);
        bird = findViewById(R.id.bird);
        bird.setX(-60);
        bird.setY(200);
        level_num.setX(100);
        door1.setY(-10);

        db = FirebaseFirestore.getInstance();

        DocumentReference docRefUsers = db.collection("users").document(userName);
        docRefUsers.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    score = document.getData().get("score");
                    // display the level number
                    level = document.getData().get("level");
                    level_num_doc = "level" + level.toString();
                    level_num.setText("LEVEL " + level.toString());

                    DocumentReference docRefLevels = db.collection("levels").document(level_num_doc);
                    docRefLevels.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                // display the level description
                                description = document.getData().get("description");
                                level_text.setText(description.toString());
                                explanation = document.getData().get("explanation");

                                // get image resources for each door from database and display
                                int resId1 = getResources().getIdentifier(document.getData().get("door1").toString(), "drawable", getPackageName());
                                img_door1.setImageResource(resId1);
                                int resId2 = getResources().getIdentifier(document.getData().get("door2").toString(), "drawable", getPackageName());
                                img_door2.setImageResource(resId2);
                                int resId3 = getResources().getIdentifier(document.getData().get("door3").toString(), "drawable", getPackageName());
                                img_door3.setImageResource(resId3);
                                int resId_backImg = getResources().getIdentifier(document.getData().get("backImg").toString(), "drawable", getPackageName());
                                level_img.setImageResource(resId_backImg);

                                img_door1.setX(190);
                                img_door1.setY(1600);
                                img_door2.setX(510);
                                img_door2.setY(1610);
                                img_door3.setX(830);
                                img_door3.setY(1600);
                                level_img.setX(600);
                                level_img.setY(850);

                                answer = document.getData().get("answer");
                                // create door objects from Door class
                                CreateDoors(Integer.valueOf(answer.toString()), explanation.toString());
                                // hide wrong doors if necessary
                                HideDoor(door_number_to_hide1, door_number_to_hide2);

                            }
                        }
                    });
                }
            }

        });

        // mute or unmute game sounds
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean soundEnabled = prefs.getBoolean("sound_enabled", true);

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (soundEnabled) {
            // Unmute the game's audio system
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        } else {
            // Mute the game's audio system
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
        }

        // create text to speech
        txt_to_speech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // set language to US English
                    int result = txt_to_speech.setLanguage(Locale.US);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(Level.this, "Language not supported", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Level.this, "Initialization failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // click speaker on button to start text to speech
        speaker_on.setOnClickListener(v -> {
            speaker_on.setVisibility(View.INVISIBLE);
            speaker_off.setVisibility(View.VISIBLE);
            String text = level_text.getText().toString();
            if (text.isEmpty()) {
                Toast.makeText(Level.this, "Enter text to speak", Toast.LENGTH_SHORT).show();
            } else {
                // speak the entered text
                txt_to_speech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        speaker_on.setVisibility(View.VISIBLE);
                        speaker_off.setVisibility(View.INVISIBLE);
                    }
                }, 15000);

            }
        });

        // click speaker off button
        speaker_off.setOnClickListener(v -> {
            txt_to_speech.stop();
            speaker_on.setVisibility(View.VISIBLE);
            speaker_off.setVisibility(View.INVISIBLE);
        });
    }

    // stop text to speech
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // release resources
        if (txt_to_speech != null) {
            txt_to_speech.stop();
            txt_to_speech.shutdown();
        }
    }

    // creating door objects
    public void CreateDoors(int answer, String expl){
        if (answer == 1){
        doorNum1 = new Door(door1, true, 1, this, expl);
        doorNum2 = new WrongDoor(door2, false, 2, this, expl);
        doorNum3 = new WrongDoor(door3, false, 3, this, expl);
    }
        else if (answer == 2){
        doorNum2 = new Door(door2, true, 2, this, expl);
        doorNum1 = new WrongDoor(door1, false, 1, this, expl);
        doorNum3 = new WrongDoor(door3, false, 3, this, expl);
        }
        else{
        doorNum3 = new Door(door3, true, 3, this, expl);
        doorNum2 = new WrongDoor(door2, false, 2, this, expl);
        doorNum1 = new WrongDoor(door1, false, 1, this, expl);
        }
    }

    // click door 1
    public void onClickDoor1(View view) {
        Door door_1;
        if(((Door) doorNum1).GetIsRightDoor()){
            door_1 = (Door) doorNum1;
        }
        else{
           door_1 = (WrongDoor) doorNum1;
           CurrentUser.tries++;
        }
        // check if this is the right door (to go to isCorrect screen)
        door_1.CheckDoor();
    }

    // click door 2
    public void onClickDoor2(View view) {
        Door door_2;
        if(((Door) doorNum2).GetIsRightDoor()){
            door_2 = (Door) doorNum2;
        }
        else{
            door_2 = (WrongDoor) doorNum2;
            CurrentUser.tries++;
        }
        // check if this is the right door (to go to isCorrect screen)
        door_2.CheckDoor();
    }

    // click door 3
    public void onClickDoor3(View view) {
        Door door_3;
        if(((Door) doorNum3).GetIsRightDoor()){
            door_3 = (Door) doorNum3;
        }
        else{
            door_3 = (WrongDoor) doorNum3;
            CurrentUser.tries++;
        }
        // check if this is the right door (to go to isCorrect screen)
        door_3.CheckDoor();
    }

    // hide wrong doors if necessary
    public void HideDoor(int number1, int number2){
        if(number1 == 1 || number2 == 1){
            img_door1.setVisibility(View.INVISIBLE);
            WrongDoor door_1 = (WrongDoor) doorNum1;
            door_1.Hide();
        }
        if(number1 == 2 || number2 == 2){
            img_door2.setVisibility(View.INVISIBLE);
            WrongDoor door_2 = (WrongDoor) doorNum2;
            door_2.Hide();
        }
        if(number1 == 3 || number2 == 3){
            img_door3.setVisibility(View.INVISIBLE);
            WrongDoor door_3 = (WrongDoor) doorNum3;
            door_3.Hide();
        }
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
                startActivity(new Intent(Level.this, LeaderBoard.class));
                return true;
            case R.id.how_to_play:
                CurrentUser.page_number = 1;
                startActivity(new Intent(Level.this, Instructions.class));
                return true;
            case R.id.settings:
                Intent intent = new Intent(Level.this, Settings.class);
                intent.putExtra("previous_activity", "Level");
                startActivity(intent);
                return true;
            case R.id.log_out:
                startActivity(new Intent(Level.this, LogIn.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}