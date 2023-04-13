package com.sahak7an.chatt.activities;

import static com.sahak7an.chatt.utilities.Constants.KEY_COLLECTION_USERS;
import static com.sahak7an.chatt.utilities.Constants.KEY_FCM_TOKEN;
import static com.sahak7an.chatt.utilities.Constants.KEY_IMAGE;
import static com.sahak7an.chatt.utilities.Constants.KEY_USER_ID;
import static com.sahak7an.chatt.utilities.Constants.KEY_USER_NAME;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sahak7an.chatt.R;
import com.sahak7an.chatt.databinding.ActivityMainBinding;
import com.sahak7an.chatt.models.User;
import com.sahak7an.chatt.utilities.PreferenceManager;

import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    private PreferenceManager preferenceManager;
    private ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        preferenceManager = new PreferenceManager(getApplicationContext());
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        changeStatusBarColor();

        setContentView(activityMainBinding.getRoot());

        loadUserDetails();
        getToken();
        setListeners();
    }

    private void setListeners() {

        activityMainBinding.imageSignOut.setOnClickListener(v -> signOut());

        activityMainBinding.newChat.setOnClickListener(v -> {

            startActivity(new Intent(getApplicationContext(), UsersActivity.class));

        });

    }

    private void loadUserDetails() {

        activityMainBinding.textName.setText(preferenceManager.getString(KEY_USER_NAME));

        byte[] bytes = Base64.decode(preferenceManager.getString(KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        activityMainBinding.imageProfile.setImageBitmap(bitmap);

    }

    private void showToast(String message) {

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }

    private void getToken() {

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);

    }

    private void updateToken(String token) {

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                firebaseFirestore.collection(KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(KEY_USER_ID)
                );

        documentReference.update(KEY_FCM_TOKEN, token)
                .addOnFailureListener(e -> {

                    if (firebaseUser != null) {

                        firebaseUser.delete();

                    }

                    preferenceManager.clear();

                    showToast("Please Sign in again\n " +
                            "Your account will be deleted\n" +
                            "    Or connection Error    ");

                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();

                });
    }

    private void signOut() {

        showToast("Signing out...");
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference documentReference =
                firebaseFirestore.collection(KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(KEY_USER_ID)
                );

        HashMap<String, Object> updates = new HashMap<>();
        updates.put(KEY_FCM_TOKEN, FieldValue.delete());

        documentReference.update(updates)
                .addOnSuccessListener(unused -> {

                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();

                })
                .addOnFailureListener(e -> {

                    showToast("Unable to sign out");

                    if (firebaseUser != null) {

                        firebaseUser.delete();

                    }

                    preferenceManager.clear();

                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();

                });

    }

    private void changeStatusBarColor() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.primary, getTheme()));
    }
}