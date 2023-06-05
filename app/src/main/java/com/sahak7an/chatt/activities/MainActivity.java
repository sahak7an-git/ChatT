package com.sahak7an.chatt.activities;

import static com.sahak7an.chatt.utilities.Constants.IMAGE_HEIGHT;
import static com.sahak7an.chatt.utilities.Constants.IMAGE_WIDTH;
import static com.sahak7an.chatt.utilities.Constants.KEY_COLLECTION_CONVERSATIONS;
import static com.sahak7an.chatt.utilities.Constants.KEY_COLLECTION_USERS;
import static com.sahak7an.chatt.utilities.Constants.KEY_FCM_TOKEN;
import static com.sahak7an.chatt.utilities.Constants.KEY_IMAGE;
import static com.sahak7an.chatt.utilities.Constants.KEY_IS_IMAGE;
import static com.sahak7an.chatt.utilities.Constants.KEY_LAST_MESSAGE;
import static com.sahak7an.chatt.utilities.Constants.KEY_RECEIVER_ID;
import static com.sahak7an.chatt.utilities.Constants.KEY_RECEIVER_IMAGE;
import static com.sahak7an.chatt.utilities.Constants.KEY_RECEIVER_USER_NAME;
import static com.sahak7an.chatt.utilities.Constants.KEY_SENDER_ID;
import static com.sahak7an.chatt.utilities.Constants.KEY_SENDER_IMAGE;
import static com.sahak7an.chatt.utilities.Constants.KEY_SENDER_USER_NAME;
import static com.sahak7an.chatt.utilities.Constants.KEY_TIMESTAMP;
import static com.sahak7an.chatt.utilities.Constants.KEY_USER;
import static com.sahak7an.chatt.utilities.Constants.KEY_USER_ID;
import static com.sahak7an.chatt.utilities.Constants.KEY_USER_NAME;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sahak7an.chatt.R;
import com.sahak7an.chatt.adapters.RecentConversationsAdapter;
import com.sahak7an.chatt.databinding.ActivityMainBinding;
import com.sahak7an.chatt.listeners.ConversationListener;
import com.sahak7an.chatt.models.ChatMessage;
import com.sahak7an.chatt.models.User;
import com.sahak7an.chatt.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends BaseActivity implements ConversationListener {

    private int count = 0;
    private FirebaseUser firebaseUser;
    private List<ChatMessage> conversations;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore firebaseFirestore;
    private ActivityMainBinding activityMainBinding;
    private RecentConversationsAdapter conversationsAdapter;

    @SuppressLint("NotifyDataSetChanged")
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {

        if (error != null) {

            return;

        }

        if (value != null) {

            for (DocumentChange documentChange: value.getDocumentChanges()) {

                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    conversations.clear();

                    String senderId = documentChange.getDocument().getString(KEY_SENDER_ID);
                    String receiverId = documentChange.getDocument().getString(KEY_RECEIVER_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = senderId;
                    chatMessage.receiverId = receiverId;

                    if (preferenceManager.getString(KEY_USER_ID).equals(senderId)) {

                        chatMessage.conversionId = documentChange.getDocument().getString(KEY_RECEIVER_ID);
                        chatMessage.conversionName = documentChange.getDocument().getString(KEY_RECEIVER_USER_NAME);
                        chatMessage.conversionImage = documentChange.getDocument().getString(KEY_RECEIVER_IMAGE);

                    } else {

                        chatMessage.conversionId = documentChange.getDocument().getString(KEY_SENDER_ID);
                        chatMessage.conversionName = documentChange.getDocument().getString(KEY_SENDER_USER_NAME);
                        chatMessage.conversionImage = documentChange.getDocument().getString(KEY_SENDER_IMAGE);

                    }

                    chatMessage.message = Objects.requireNonNull(documentChange.getDocument().getString(KEY_LAST_MESSAGE)).strip();
                    chatMessage.date = documentChange.getDocument().getDate(KEY_TIMESTAMP);
                    chatMessage.isImage = documentChange.getDocument().getBoolean(KEY_IS_IMAGE);
                    conversations.add(chatMessage);

                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {

                    for (int item = 0; item < conversations.size(); item++) {

                        String senderId = documentChange.getDocument().getString(KEY_SENDER_ID);
                        String receiverId = documentChange.getDocument().getString(KEY_RECEIVER_ID);

                        if (conversations.get(item).senderId.equals(senderId) &&
                            conversations.get(item).receiverId.equals(receiverId)) {

                            conversations.get(item).message = Objects.requireNonNull(documentChange.getDocument().getString(KEY_LAST_MESSAGE)).strip();
                            conversations.get(item).date = documentChange.getDocument().getDate(KEY_TIMESTAMP);
                            break;

                        }

                    }

                }

            }

            conversations.sort((obj1, obj2) -> obj2.date.compareTo(obj1.date));
            conversationsAdapter.notifyDataSetChanged();
            activityMainBinding.conversationsRecyclerView.smoothScrollToPosition(0);
            activityMainBinding.conversationsRecyclerView.setVisibility(View.VISIBLE);
            activityMainBinding.progressBar.setVisibility(View.GONE);

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        preferenceManager = new PreferenceManager(getApplicationContext());
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        changeStatusBarColor();

        setContentView(activityMainBinding.getRoot());

        init();

        loadUserDetails();
        getToken();

        setListeners();

    }

    @Override
    protected void onResume() {
        super.onResume();
        listenConversations();
    }

    private void init() {

        conversations = new ArrayList<>();
        conversationsAdapter = new RecentConversationsAdapter(conversations, this);
        firebaseFirestore = FirebaseFirestore.getInstance();

        activityMainBinding.conversationsRecyclerView.setAdapter(conversationsAdapter);

    }

    private void setListeners() {

        activityMainBinding.imageSignOut.setOnClickListener(v -> signOut());

        activityMainBinding.newChat.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), UsersActivity.class)));

        activityMainBinding.imageProfile.setOnClickListener(v -> {

            if (count % 2 == 0) {

                activityMainBinding.chatView.startAnimation(AnimationUtils.loadAnimation(
                        getApplicationContext(), R.anim.move_down
                ));

                activityMainBinding.newChat.startAnimation(AnimationUtils.loadAnimation(
                        getApplicationContext(), R.anim.move_down
                ));

            } else {

                activityMainBinding.chatView.startAnimation(AnimationUtils.loadAnimation(
                        getApplicationContext(), R.anim.move_up
                ));

                activityMainBinding.newChat.startAnimation(AnimationUtils.loadAnimation(
                        getApplicationContext(), R.anim.move_up
                ));

            }
            count++;


        });

    }

    private void listenConversations() {

        firebaseFirestore.collection(KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(KEY_SENDER_ID, preferenceManager.getString(KEY_USER_ID))
                .addSnapshotListener(eventListener);

        firebaseFirestore.collection(KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(KEY_RECEIVER_ID, preferenceManager.getString(KEY_USER_ID))
                .addSnapshotListener(eventListener);

    }

    private void loadUserDetails() {

        activityMainBinding.textName.setText(preferenceManager.getString(KEY_USER_NAME));

        byte[] bytes = Base64.decode(preferenceManager.getString(KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        activityMainBinding.imageProfile.setImageBitmap(getResizedBitmap(bitmap));

    }

    private void showToast(String message) {

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }

    private void getToken() {

        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(this::updateToken)
                .addOnFailureListener(v -> {

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

    private void updateToken(String token) {

        preferenceManager.putString(KEY_FCM_TOKEN, token);

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

    @Override
    public void onConversationClicked(User user) {

        preferenceManager.putString(KEY_RECEIVER_IMAGE, user.image);
        preferenceManager.putString(KEY_RECEIVER_ID, user.id);
        preferenceManager.putString(KEY_RECEIVER_USER_NAME, user.userName);

        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(KEY_USER, user);
        startActivity(intent);

    }
    private Bitmap getResizedBitmap(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) IMAGE_HEIGHT) / width;
        float scaleHeight = ((float) IMAGE_WIDTH) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, width, height, matrix, false);
        bitmap.recycle();

        return resizedBitmap;

    }

}