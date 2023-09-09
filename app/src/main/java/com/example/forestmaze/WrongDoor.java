package com.example.forestmaze;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

public class WrongDoor extends Door{

    public WrongDoor(ImageView doorImg, boolean isRightDoor, int number, Context context, String text_for_answer) {
        super(doorImg, isRightDoor, number, context, text_for_answer);
    }

    public void Hide(){
        //hide the img of the door
        this.doorImg.setVisibility(View.INVISIBLE);
    }
}
