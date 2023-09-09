package com.example.forestmaze;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class DialogRateGame extends DialogFragment {

    private RatingBar mRatingBar;
    FirebaseFirestore db;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // Create a new AlertDialog.Builder instance
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate the layout for this dialog
        View view = inflater.inflate(R.layout.activity_dialog_rate_game, null);

        // Get the RatingBar from the layout
        mRatingBar = view.findViewById(R.id.ratingBar);

        // Set the dialog's view to the inflated layout
        builder.setView(view)

                // Set the dialog's title
                .setTitle("Rate the Game")

                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get the user's rating and save it in database
                        float rating = mRatingBar.getRating();
                        db = FirebaseFirestore.getInstance();
                        DocumentReference docRef = db.collection("users").document(CurrentUser.currentUser);
                        docRef.update("rating", rating).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}