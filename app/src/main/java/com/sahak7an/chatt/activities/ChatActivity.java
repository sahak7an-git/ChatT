package com.sahak7an.chatt.activities;

import static com.sahak7an.chatt.utilities.Constants.KEY_USER;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.Window;
import android.view.WindowManager;

import com.sahak7an.chatt.R;
import com.sahak7an.chatt.databinding.ActivityChatBinding;
import com.sahak7an.chatt.models.User;
import com.sahak7an.chatt.utilities.PreferenceManager;

public class ChatActivity extends AppCompatActivity {

    private User receiverUser;
    private PreferenceManager preferenceManager;
    private ActivityChatBinding activityChatBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityChatBinding = ActivityChatBinding.inflate(getLayoutInflater());
        changeStatusBarColor();

        setContentView(activityChatBinding.getRoot());

        setListeners();
        loadReceiverDetails();
    }

    private void loadReceiverDetails() {

        receiverUser = (User) getIntent().getSerializableExtra(KEY_USER);
        activityChatBinding.textUserName.setText(receiverUser.userName);
        activityChatBinding.receiverImage.setImageBitmap(getUserImage(receiverUser.image));

    }

    private void setListeners() {
        activityChatBinding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    private Bitmap getUserImage(String encodedImage) {

        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

    }

    private void changeStatusBarColor() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.primary, getTheme()));
    }
}