package com.example.forestmaze;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.Toast;

public class Door {
    protected ImageView doorImg; // image of a door
    private boolean isRightDoor; // bool is right door. right = true, wrong = false
    private int number; // the number of the door: 1, 2 or 3
    private Context context;
    private String text_for_answer; // explanation text for the answer

    public Door(ImageView doorImg, boolean isRightDoor, int number, Context context, String text_for_answer){
        this.doorImg = doorImg;
        this.isRightDoor = isRightDoor;
        this.number = number;
        this.context = context;
        this.text_for_answer = text_for_answer;
    }

    public ImageView GetDoorImg(){return this.doorImg;}
    public boolean GetIsRightDoor(){return this.isRightDoor;}
    public int GetNumber(){return this.number;}

    public void SetDoorImg(ImageView imgForDoor){this.doorImg = imgForDoor;}
    public void SetISRightDoor(boolean rightOrWrong){this.isRightDoor = rightOrWrong;}
    public void SetNumber(int number){this.number = number;}


    public void CheckDoor(){
        Intent intent = new Intent(context, IsCorrect.class);
        // if clicked the right door
        if(this.isRightDoor){
            // go to true level result
            CurrentUser.currentDoorNumber1 = 0;
            CurrentUser.currentDoorNumber2 = 0;
            intent.putExtra("textForAnswer", this.text_for_answer);
            intent.putExtra("isCorrect", "true");
        }
        // if clicked a wrong door
        else{
            //  go to false level result
            if (CurrentUser.tries == 1) {CurrentUser.currentDoorNumber1 = this.number;}
            if (CurrentUser.tries == 2) {CurrentUser.currentDoorNumber2 = this.number;}
            intent.putExtra("isCorrect", "false");
        }
        // start the activity
        context.startActivity(intent);
    }
}
