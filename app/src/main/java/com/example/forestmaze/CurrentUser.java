package com.example.forestmaze;

public class CurrentUser {

    public static String currentUser; // user name
    public static int currentDoorNumber1; // number of first wrong door
    public static int currentDoorNumber2; // number of second wrong door
    public static int tries; // number of tries
    public static int page_number; // page number in instructions


    public void SetUser(String userName){
        this.currentUser = userName;
        this.currentDoorNumber1 = 0;
        this.currentDoorNumber2 = 0;
        this.tries = 0;
        this.page_number = 1;
    }
}


