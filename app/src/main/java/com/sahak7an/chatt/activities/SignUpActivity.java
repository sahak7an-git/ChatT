package com.sahak7an.chatt.activities;

import static com.sahak7an.chatt.utilities.Constants.KEY_COLLECTION_USERS;
import static com.sahak7an.chatt.utilities.Constants.KEY_EMAIL;
import static com.sahak7an.chatt.utilities.Constants.KEY_IMAGE;
import static com.sahak7an.chatt.utilities.Constants.KEY_PASSWORD;
import static com.sahak7an.chatt.utilities.Constants.KEY_USER_ID;
import static com.sahak7an.chatt.utilities.Constants.KEY_USER_NAME;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sahak7an.chatt.databinding.ActivitySignUpBinding;
import com.sahak7an.chatt.utilities.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private String encodedImage;
    private FirebaseAuth firebaseAuth;
    private PreferenceManager preferenceManager;

    private FirebaseFirestore firebaseFirestore;
    private ActivitySignUpBinding activitySignUpBinding;
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {

                    if (result.getData() != null) {

                        Uri imageUri = result.getData().getData();

                        try {

                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                            activitySignUpBinding.imageProfile.setImageBitmap(bitmap);
                            activitySignUpBinding.textAddImage.setVisibility(View.GONE);
                            encodedImage = encodedImage(bitmap);

                        } catch (FileNotFoundException e) {

                            e.printStackTrace();

                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferenceManager = new PreferenceManager(getApplicationContext());
        activitySignUpBinding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(activitySignUpBinding.getRoot());

        setListeners();
    }

    private void setListeners() {

        activitySignUpBinding.textSignIn.setOnClickListener(v -> onBackPressed());

        activitySignUpBinding.buttonSignUp.setOnClickListener(v -> {
            if (isValidSignUpDetails()) {

                signUp();

            }
        });

        activitySignUpBinding.layoutImage.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);

        });
    }

    private void showToast(String message) {

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }

    private void signUp() {

        loading(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection(KEY_COLLECTION_USERS)
                        .get()
                        .addOnCompleteListener(v -> {
                           Boolean flag = true;

                           for (QueryDocumentSnapshot queryDocumentSnapshot: v.getResult()) {
                               if (activitySignUpBinding.inputUserName.getText().toString().trim()
                                       .equals(queryDocumentSnapshot.getString(KEY_USER_NAME))) {
                                   flag = false;
                               }
                           }

                           if (flag) {
                               firebaseAuth.createUserWithEmailAndPassword(activitySignUpBinding.inputEmail.getText().toString(),
                                               activitySignUpBinding.inputPassword.getText().toString())
                                       .addOnCompleteListener(task -> {
                                           if (task.isSuccessful()) {

                                               HashMap<String, Object> userData = new HashMap<>();

                                               userData.put(KEY_USER_NAME, activitySignUpBinding.inputUserName.getText().toString());
                                               userData.put(KEY_EMAIL, activitySignUpBinding.inputEmail.getText().toString());
                                               userData.put(KEY_IMAGE, encodedImage);

                                               firebaseFirestore.collection(KEY_COLLECTION_USERS)
                                                       .add(userData)
                                                       .addOnSuccessListener(documentReference -> {
                                                           preferenceManager.putString(KEY_USER_ID, documentReference.getId());
                                                           preferenceManager.putString(KEY_USER_NAME, activitySignUpBinding.inputUserName.getText().toString());
                                                           preferenceManager.putString(KEY_IMAGE, encodedImage);
                                                           preferenceManager.putString(KEY_PASSWORD, activitySignUpBinding.inputPassword.getText().toString());

                                                           showToast("Sign up is successful\n" +
                                                                   "Verify your account");

                                                           Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                                           startActivity(intent);
                                                           finish();

                                                           loading(false);
                                                       })
                                                       .addOnFailureListener(e -> showToast(e.getMessage()));


                                           } else if (Objects.equals(
                                                   Objects.requireNonNull(task.getException()).getClass(),
                                                   FirebaseAuthUserCollisionException.class)) {
                                               loading(false);
                                               showToast("Email already used");

                                           } else if (Objects.equals(
                                                   Objects.requireNonNull(task.getException()).getClass(),
                                                   FirebaseAuthWeakPasswordException.class)) {
                                               loading(false);
                                               showToast("Password is short");

                                           } else {
                                               showToast("Sign up isn't successful");
                                               Log.d("HELLO", String.valueOf(task.getException()));
                                               loading(false);
                                           }
                                       });
                           }

                           else {
                               showToast("This username is already used");
                               loading(false);
                               return;
                           }
                        });
    }

    private Boolean isValidSignUpDetails() {

        if (encodedImage == null) {
            showToast("Select profile image");
            return false;
        } else if (activitySignUpBinding.inputUserName.getText().toString().trim().isEmpty()) {
            showToast("Enter name");
            return false;
        } else if (activitySignUpBinding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Enter email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(activitySignUpBinding.inputEmail.getText().toString()).matches()) {
            showToast("Enter valid email!");
            return false;
        } else if (activitySignUpBinding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter password");
            return false;
        } else if (activitySignUpBinding.inputConfirmPassword.getText().toString().trim().isEmpty()) {
            showToast("Confirm password");
            return false;
        } else if (!activitySignUpBinding.inputPassword.getText().toString().equals(activitySignUpBinding.inputConfirmPassword.getText().toString())) {
            showToast("Passwords doesn't matching");
            return false;
        } else {
            return true;
        }
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            activitySignUpBinding.buttonSignUp.setVisibility(View.INVISIBLE);
            activitySignUpBinding.progressBar.setVisibility(View.VISIBLE);
        } else {
            activitySignUpBinding.buttonSignUp.setVisibility(View.VISIBLE);
            activitySignUpBinding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private String encodedImage(Bitmap bitmap) {
        int previewWidth = 200;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();

        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

}