package com.sahak7an.chatt.activities;

import static com.sahak7an.chatt.utilities.Constants.KEY_COLLECTION_USERS;
import static com.sahak7an.chatt.utilities.Constants.KEY_EMAIL;
import static com.sahak7an.chatt.utilities.Constants.KEY_FCM_TOKEN;
import static com.sahak7an.chatt.utilities.Constants.KEY_IMAGE;
import static com.sahak7an.chatt.utilities.Constants.KEY_IP_ADDRESS;
import static com.sahak7an.chatt.utilities.Constants.KEY_PORT;
import static com.sahak7an.chatt.utilities.Constants.KEY_RECEIVER_ID;
import static com.sahak7an.chatt.utilities.Constants.KEY_RECEIVER_IMAGE;
import static com.sahak7an.chatt.utilities.Constants.KEY_RECEIVER_USER_NAME;
import static com.sahak7an.chatt.utilities.Constants.KEY_USER;
import static com.sahak7an.chatt.utilities.Constants.KEY_USER_ID;
import static com.sahak7an.chatt.utilities.Constants.KEY_USER_NAME;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sahak7an.chatt.R;
import com.sahak7an.chatt.adapters.UsersAdapter;
import com.sahak7an.chatt.databinding.ActivityUsersBinding;
import com.sahak7an.chatt.listeners.UserListener;
import com.sahak7an.chatt.models.User;
import com.sahak7an.chatt.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends BaseActivity implements UserListener {

    private PreferenceManager preferenceManager;
    private ActivityUsersBinding activityUsersBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferenceManager = new PreferenceManager(getApplicationContext());
        activityUsersBinding = ActivityUsersBinding.inflate(getLayoutInflater());
        changeStatusBarColor();

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
                            user.id = queryDocumentSnapshot.getId();
                            user.ip = queryDocumentSnapshot.getString(KEY_IP_ADDRESS);
                            user.port = queryDocumentSnapshot.getString(KEY_PORT);
                            userList.add(user);

                        }

                        if (userList.size() > 0) {

                            UsersAdapter usersAdapter = new UsersAdapter(userList, this);
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

    private void changeStatusBarColor() {

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.primary, getTheme()));

    }

    @Override
    public void onUserClicked(User user) {

        preferenceManager.putString(KEY_RECEIVER_IMAGE, user.image);
        preferenceManager.putString(KEY_RECEIVER_ID, user.id);
        preferenceManager.putString(KEY_RECEIVER_USER_NAME, user.userName);

        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(KEY_USER, user);
        startActivity(intent);
        finish();

    }

}