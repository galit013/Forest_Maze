package com.example.forestmaze;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LeaderBoard extends AppCompatActivity {

    FirebaseFirestore db;
    TextView user_name, user_score;
    ImageView medal;
    ListView listView;
    String names[] = new String[5];
    int scores[] = new int[5];

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        // the screen cannot be turned
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        listView = findViewById(R.id.listView);
        medal = findViewById(R.id.medal);
        user_name = findViewById(R.id.user_name);
        user_score = findViewById(R.id.user_score);

        db = FirebaseFirestore.getInstance();
        // create a query of the top 5 players from database based on score
        CollectionReference usersCollection = db.collection("users");
        Query leader_board_query = usersCollection.orderBy("score", Query.Direction.DESCENDING).limit(5);

        leader_board_query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                // go through query and save the top users in lists
                int i = 0;
                for (DocumentSnapshot document : querySnapshot) {
                    String name = document.getString("name") != null ? document.getString("name") : "";
                    int score = document.getLong("score") != null ? document.getLong("score").intValue() : 0;

                    names[i] = name;
                    scores[i] = score;
                    i++;
                }

                // create and set adapter for leader board to show the top 5 players
                LeaderboardAdapter adapter = new LeaderboardAdapter(getApplicationContext(), names, scores);
                listView.setAdapter(adapter);

            }
        });
    }

    // click on the x button
    public void escape(View view) {
        finish();
    }
}