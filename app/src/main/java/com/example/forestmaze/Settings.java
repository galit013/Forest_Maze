package com.example.forestmaze;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        // the screen cannot be turned
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public Context getContext() {
        // Use the application context
        return  getApplicationContext();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // click back arrow button
        if (item.getItemId() == android.R.id.home) {
            Intent intent = getIntent();
            String previous_act = intent.getStringExtra("previous_activity");
            if (previous_act.equals("Map")){startActivity(new Intent(getContext(), GameMap.class));}
            if (previous_act.equals("Finish")){startActivity(new Intent(getContext(), FinishScreen.class));}
            if (previous_act.equals("Level")){startActivity(new Intent(getContext(), Level.class));}
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        FirebaseFirestore db;

        @Override
        // create preferences for settings
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            // change user name option
            EditTextPreference userNamePref = findPreference("user_name");
            userNamePref.setText("");
            // show dialog with option to change user name
            userNamePref.setDialogMessage("Your current user name is " + CurrentUser.currentUser);
            userNamePref.setSummary(CurrentUser.currentUser);

            // handle the change in TextView (new user name)
            userNamePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    db = FirebaseFirestore.getInstance();
                    String newUserName = newValue.toString();
                    DocumentReference newDocRef_check = db.collection("users").document(newUserName);
                    newDocRef_check.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // if document exists, the new user name is taken
                                    Toast.makeText(getContext(), "This user name is already taken \n pick a different name", Toast.LENGTH_LONG).show();
                                }
                                else{
                                    userNamePref.setSummary(newUserName);

                                    // update new user name in database
                                    DocumentReference docRef = db.collection("users").document(CurrentUser.currentUser);
                                    CurrentUser.currentUser = newUserName;
                                    docRef.update("name", newUserName).addOnSuccessListener(new OnSuccessListener<Void>() {
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

                                    // change document ID to match new user name
                                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {
                                                // create a new document with ID as the new name
                                                Map<String, Object> data = documentSnapshot.getData();
                                                // copy the data to it
                                                DocumentReference newDocRef = db.collection("users").document(newUserName);
                                                newDocRef.set(data)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                // New document created successfully
                                                                // Delete the old document
                                                                docRef.delete()
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                // Old document deleted successfully
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                // Error deleting old document
                                                                            }
                                                                        });
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                // Error creating new document
                                                            }
                                                        });
                                            } else {
                                                // Old document doesn't exist
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });

                    return true; // return true to update the summary of the preference with the new value
                }
            });

            // change profile picture option
            Preference profilePicturePref = findPreference("profile_picture");
            profilePicturePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(getContext(), ChangeProfileImg.class));
                    return true;
                }
            });

            // switch sound on or off option
            SwitchPreferenceCompat soundPref = findPreference("sound");
            soundPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // get new value of switch preference and save it
                    boolean soundEnabled = (boolean) newValue;
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                    editor.putBoolean("sound_enabled", soundEnabled);
                    editor.apply();
                    return true; // Return true to save the new preference value
                }
            });

        }
    }
}