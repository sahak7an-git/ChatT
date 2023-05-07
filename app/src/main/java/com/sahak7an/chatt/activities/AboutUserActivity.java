package com.sahak7an.chatt.activities;

import static com.sahak7an.chatt.utilities.Constants.KEY_COLLECTION_USERS;
import static com.sahak7an.chatt.utilities.Constants.KEY_IS_ONLINE;
import static com.sahak7an.chatt.utilities.Constants.KEY_USER;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.google.firebase.firestore.FirebaseFirestore;
import com.sahak7an.chatt.R;
import com.sahak7an.chatt.databinding.ActivityAboutUserBinding;
import com.sahak7an.chatt.models.User;

import java.util.Objects;

public class AboutUserActivity extends AppCompatActivity {

    private User receiverUser;
    private Boolean isReceiverOnline = false;
    private FirebaseFirestore firebaseFirestore;
    private ActivityAboutUserBinding activityAboutUserBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityAboutUserBinding = ActivityAboutUserBinding.inflate(getLayoutInflater());

        setContentView(activityAboutUserBinding.getRoot());

        setListeners();
        changeStatusBarColor();

    }

    @Override
    protected void onStart() {
        super.onStart();

        init();

    }

    @Override
    protected void onResume() {
        super.onResume();

        listenAvailabilityOfReceiver();

    }

    private void setListeners() {

        activityAboutUserBinding.imageBack.setOnClickListener(v -> onBackPressed());

    }

    private void init() {

        firebaseFirestore = FirebaseFirestore.getInstance();

        receiverUser = (User) getIntent().getSerializableExtra(KEY_USER);

        activityAboutUserBinding.textUserName.setText(receiverUser.userName);

        activityAboutUserBinding.userImage.setImageBitmap(getResizedBitmap(
                getReceiverUserImage(receiverUser.image),
                displaySize()[0],
                displaySize()[1] / 2
        ));

    }

    private Bitmap getReceiverUserImage(String encodedImage) {

        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

    }

    private void listenAvailabilityOfReceiver() {

        firebaseFirestore.collection(KEY_COLLECTION_USERS).document(receiverUser.id)
                .addSnapshotListener(AboutUserActivity.this, ((value, error) -> {

                    if (error != null) {

                        return;

                    }

                    if (value != null) {

                        if (value.getBoolean(KEY_IS_ONLINE) != null) {

                            isReceiverOnline = Objects.requireNonNull(
                                    value.getBoolean(KEY_IS_ONLINE)
                            );

                        }

                        if (isReceiverOnline) {

                            activityAboutUserBinding.status.setBackground(AppCompatResources
                                    .getDrawable(getApplicationContext(),
                                            R.drawable.background_online_status));

                            activityAboutUserBinding.textStatus.setText(getString(R.string.online));

                        } else {

                            activityAboutUserBinding.status.setBackground(AppCompatResources
                                    .getDrawable(getApplicationContext(),
                                            R.drawable.background_offline_status));

                            activityAboutUserBinding.textStatus.setText(getString(R.string.offline));

                        }

                    }

                }));

    }

    private Bitmap getResizedBitmap(Bitmap bitmap, int newWidth, int newHeight) {

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight + 100, false);

    }

    private int[] displaySize() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};

    }

    private void changeStatusBarColor() {

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        window.setStatusBarColor(this.getResources().getColor(R.color.primary, getTheme()));

    }

}