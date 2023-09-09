package com.example.forestmaze;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class LeaderboardAdapter extends BaseAdapter {
    Context context;
    String names[]; // list of users names
    int scores[]; // list of users scores
    LayoutInflater inflter;

    public LeaderboardAdapter(Context applicationContext, String[] names, int[] scores) {
        this.context = applicationContext;
        this.names = names;
        this.scores = scores;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // get TextView of name and score
        view = inflter.inflate(R.layout.list_item, null);
        TextView name = view.findViewById(R.id.user_name);
        TextView score = view.findViewById(R.id.user_score);
        ImageView medal = (ImageView) view.findViewById(R.id.medal);

        // display medal images
        if (i == 0){
            medal.setImageResource(R.drawable.gold_medal);
        }
        else if (i == 1){
            medal.setImageResource(R.drawable.silver_medal);
        }
        else if (i == 2){
            medal.setImageResource(R.drawable.bronze_medal);
        }

        // display names and scores
        name.setText((i + 1) + ". " + names[i]);
        score.setText("score: " + scores[i]);

        return view;
    }
}