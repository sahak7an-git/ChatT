package com.sahak7an.chatt.activities;

import static com.sahak7an.chatt.utilities.Constants.KEY_COLLECTION_USERS;
import static com.sahak7an.chatt.utilities.Constants.KEY_EMAIL;
import static com.sahak7an.chatt.utilities.Constants.KEY_FCM_TOKEN;
import static com.sahak7an.chatt.utilities.Constants.KEY_IMAGE;
import static com.sahak7an.chatt.utilities.Constants.KEY_USER_ID;
import static com.sahak7an.chatt.utilities.Constants.KEY_USER_NAME;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sahak7an.chatt.adapters.UsersAdapter;
import com.sahak7an.chatt.databinding.ActivityUsersBinding;
import com.sahak7an.chatt.models.User;
import com.sahak7an.chatt.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private ActivityUsersBinding activityUsersBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferenceManager = new PreferenceManager(getApplicationContext());
        activityUsersBinding = ActivityUsersBinding.inflate(getLayoutInflater());

        setContentView(activityUsersBinding.getRoot());

        setListeners();
        getUsers();
    }

    private void setListeners() {

        activityUsersBinding.imageBack.setOnClickListener(v -> onBackPressed());

    }

    private void getUsers() {
        loading(true);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(KEY_USER_ID);

                    if (task.isSuccessful() && task.getResult() != null) {

                        List<User> userList = new ArrayList<>();

                        for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()) {

                            if (currentUserId.equals(queryDocumentSnapshot.getId())) {

                                continue;

                            }

                            User user = new User();
                            user.userName = queryDocumentSnapshot.getString(KEY_USER_NAME);
                            user.email = queryDocumentSnapshot.getString(KEY_EMAIL);
                            user.image = queryDocumentSnapshot.getString(KEY_IMAGE);
                            user.token = queryDocumentSnapshot.getString(KEY_FCM_TOKEN);
                            userList.add(user);

                        }

                        if (userList.size() > 0) {

                            UsersAdapter usersAdapter = new UsersAdapter(userList);
                            activityUsersBinding.usersRecyclerView.setAdapter(usersAdapter);

                            activityUsersBinding.usersRecyclerView.setVisibility(View.VISIBLE);

                        } else {

                            showErrorMessage();

                        }

                    } else {

                        showErrorMessage();

                    }
                });

    }

    private void showErrorMessage() {

        activityUsersBinding.textErrorMessage.setText(String.format("%s", "No User available"));
        activityUsersBinding.textErrorMessage.setVisibility(View.VISIBLE);

    }

    private void loading(Boolean isLoading) {

        if (isLoading) {

            activityUsersBinding.progressBar.setVisibility(View.VISIBLE);

        } else {

            activityUsersBinding.progressBar.setVisibility(View.INVISIBLE);

        }
    }
}