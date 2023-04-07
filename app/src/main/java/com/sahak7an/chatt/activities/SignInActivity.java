package com.sahak7an.chatt.activities;

import static com.sahak7an.chatt.utilities.Constants.KEY_COLLECTION_USERS;
import static com.sahak7an.chatt.utilities.Constants.KEY_EMAIL;
import static com.sahak7an.chatt.utilities.Constants.KEY_IMAGE;
import static com.sahak7an.chatt.utilities.Constants.KEY_IS_SIGNED_IN;
import static com.sahak7an.chatt.utilities.Constants.KEY_USER_ID;
import static com.sahak7an.chatt.utilities.Constants.KEY_USER_NAME;
import static com.sahak7an.chatt.utilities.Constants.KEY_VERIFIED;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sahak7an.chatt.databinding.ActivitySignInBinding;
import com.sahak7an.chatt.utilities.PreferenceManager;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private PreferenceManager preferenceManager;
    private ActivitySignInBinding activitySignInBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        activitySignInBinding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(activitySignInBinding.getRoot());
        setListeners();
        autoLogIn();

    }

    private void setListeners() {
        activitySignInBinding.textCreateNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));

        activitySignInBinding.buttonSignIn.setOnClickListener(v -> {
            if (isValidSignInDetails()) {
                isVerified();
            }
        });
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            activitySignInBinding.buttonSignIn.setVisibility(View.INVISIBLE);
            activitySignInBinding.progressBar.setVisibility(View.VISIBLE);
        } else {
            activitySignInBinding.buttonSignIn.setVisibility(View.VISIBLE);
            activitySignInBinding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidSignInDetails() {
        if (activitySignInBinding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Enter Email");
            return false;
        } else if (activitySignInBinding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter Password");
            return false;
        } else {
            return true;
        }
    }

    private void signIn() {
        loading(true);
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithEmailAndPassword(activitySignInBinding.inputEmail.getText().toString(),
                        activitySignInBinding.inputPassword.getText().toString())
                .addOnCompleteListener(task -> {
                    loading(false);

                    if (task.isSuccessful()) {

                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                        firestore.collection(KEY_COLLECTION_USERS)
                                        .whereEqualTo(KEY_EMAIL, activitySignInBinding.inputEmail.getText().toString())
                                        .get()
                                        .addOnCompleteListener(v -> {
                                            if (v.isSuccessful() && v.getResult() != null && v.getResult().getDocuments().size() > 0) {

                                                DocumentSnapshot documentSnapshot = v.getResult().getDocuments().get(0);
                                                preferenceManager.putString(KEY_USER_ID, documentSnapshot.getId());
                                                preferenceManager.putString(KEY_USER_NAME, documentSnapshot.getString(KEY_USER_NAME));
                                                preferenceManager.putString(KEY_IMAGE, documentSnapshot.getString(KEY_IMAGE));
                                                preferenceManager.putString(KEY_EMAIL, documentSnapshot.getString(KEY_EMAIL));

                                                showToast("Sign in successful");
                                                preferenceManager.putBoolean(KEY_VERIFIED, true);
                                                preferenceManager.putBoolean(KEY_IS_SIGNED_IN, true);
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(intent);
                                                finish();

                                            } else {
                                                loading(false);
                                                showToast("This account isn't sign up");
                                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                                if (firebaseUser != null) {
                                                    firebaseUser.delete();
                                                }
                                                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                                                finish();
                                            }
                                        });

                    } else if (Objects.equals(
                            Objects.requireNonNull(task.getException()).getClass(),
                            FirebaseAuthInvalidUserException.class)) {

                        showToast("Invalid Email");

                    } else if (Objects.equals(
                            task.getException().getClass(),
                            FirebaseAuthInvalidCredentialsException.class)) {

                        showToast("Incorrect Password");

                    } else {
                        showToast("Doesn't Sign in");
                    }
                });
    }

    private void isVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.reload();
            if (user.isEmailVerified()) {
                signIn();
            } else {
                user.sendEmailVerification();
            }
        }
    }

    private void autoLogIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.reload();
            if (user.isEmailVerified()) {
                if (preferenceManager.getBoolean(KEY_IS_SIGNED_IN) && preferenceManager.getBoolean(KEY_VERIFIED)) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }  else {
                isVerified();
            }
        } else {
            showToast("Sign up");
        }


    }

}