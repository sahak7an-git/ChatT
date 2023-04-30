package com.sahak7an.chatt.activities;

import static com.sahak7an.chatt.utilities.Constants.KEY_COLLECTION_CHAT;
import static com.sahak7an.chatt.utilities.Constants.KEY_COLLECTION_CONVERSATIONS;
import static com.sahak7an.chatt.utilities.Constants.KEY_COLLECTION_USERS;
import static com.sahak7an.chatt.utilities.Constants.KEY_COUNT;
import static com.sahak7an.chatt.utilities.Constants.KEY_IMAGE;
import static com.sahak7an.chatt.utilities.Constants.KEY_IS_ONLINE;
import static com.sahak7an.chatt.utilities.Constants.KEY_LAST_MESSAGE;
import static com.sahak7an.chatt.utilities.Constants.KEY_MESSAGE;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sahak7an.chatt.R;
import com.sahak7an.chatt.adapters.ChatAdapter;
import com.sahak7an.chatt.databinding.ActivityChatBinding;
import com.sahak7an.chatt.dialogs.ImageProfileDialog;
import com.sahak7an.chatt.models.ChatMessage;
import com.sahak7an.chatt.models.User;
import com.sahak7an.chatt.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ChatActivity extends BaseActivity {
    private int count = 0;
    private User receiverUser;
    private boolean flag = true;
    private ChatAdapter chatAdapter;
    private String conversionId = null;
    private List<ChatMessage> chatMessages;
    private Boolean isReceiverOnline = false;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore firebaseFirestore;
    private ActivityChatBinding activityChatBinding;

    @SuppressLint("NotifyDataSetChanged")
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {

        if (error != null) {
            return;
        }

        if (value != null) {

            int count = chatMessages.size();

            for (DocumentChange documentChange: value.getDocumentChanges()) {

                if (documentChange.getType() == DocumentChange.Type.ADDED) {

                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString(KEY_SENDER_ID);
                    chatMessage.receiverId = documentChange.getDocument().getString(KEY_RECEIVER_ID);
                    chatMessage.message = Objects.requireNonNull(documentChange.getDocument().getString(KEY_MESSAGE)).strip();
                    chatMessage.date = documentChange.getDocument().getDate(KEY_TIMESTAMP);
                    chatMessage.count = Objects.requireNonNull(documentChange.getDocument().get(KEY_COUNT)).hashCode();
                    chatMessages.add(chatMessage);

                }

            }

            chatMessages.sort(Comparator.comparing(obj -> obj.count));

            if (count == 0) {

                chatAdapter.notifyDataSetChanged();

            } else {

                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                activityChatBinding.chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);

            }

            activityChatBinding.chatRecyclerView.setVisibility(View.VISIBLE);

        }

        activityChatBinding.progressBar.setVisibility(View.GONE);

        if (conversionId == null) {

            checkForConversion();

        }

    };

    private final OnCompleteListener<QuerySnapshot> conversationOnCompleteListener = task -> {

        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {

            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversionId = documentSnapshot.getId();

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityChatBinding = ActivityChatBinding.inflate(getLayoutInflater());
        changeStatusBarColor();

        setContentView(activityChatBinding.getRoot());

        setListeners();
        loadReceiverDetails();

        init();
        listenMessage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        listenAvailabilityOfReceiver();
    }

    private void loadReceiverDetails() {

        receiverUser = (User) getIntent().getSerializableExtra(KEY_USER);
        activityChatBinding.textUserName.setText(receiverUser.userName);
        activityChatBinding.receiverImage.setImageBitmap(getReceiverUserImage(receiverUser.image));

    }

    private void listenMessage() {

        firebaseFirestore.collection(KEY_COLLECTION_CHAT)
                .whereEqualTo(KEY_SENDER_ID, preferenceManager.getString(KEY_USER_ID))
                .whereEqualTo(KEY_RECEIVER_ID, receiverUser.id)
                .addSnapshotListener(eventListener);

        firebaseFirestore.collection(KEY_COLLECTION_CHAT)
                .whereEqualTo(KEY_SENDER_ID, receiverUser.id)
                .whereEqualTo(KEY_RECEIVER_ID, preferenceManager.getString(KEY_USER_ID))
                .addSnapshotListener(eventListener);

    }

    private void listenAvailabilityOfReceiver() {

        firebaseFirestore.collection(KEY_COLLECTION_USERS).document(receiverUser.id)
                .addSnapshotListener(ChatActivity.this, ((value, error) -> {

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

                           activityChatBinding.status.setBackground(AppCompatResources
                                   .getDrawable(getApplicationContext(),
                                           R.drawable.background_online_status));

                           activityChatBinding.textStatus.setText(getString(R.string.online));

                       } else {

                           activityChatBinding.status.setBackground(AppCompatResources
                                   .getDrawable(getApplicationContext(),
                                           R.drawable.background_offline_status));

                           activityChatBinding.textStatus.setText(getString(R.string.offline));

                       }

                    }

                }));

    }

    private void init() {

        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();

        chatAdapter = new ChatAdapter(

                preferenceManager.getString(KEY_USER_ID),
                chatMessages

        );

        activityChatBinding.chatRecyclerView.setAdapter(chatAdapter);
        firebaseFirestore = FirebaseFirestore.getInstance();

        activityChatBinding.inputMessage.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                changeLayoutButton(charSequence.length() > 0);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        activityChatBinding.receiverImage.setOnClickListener(v -> {

            if (flag) {

                showDialog();

            }

        });

    }

    private void sendMessage() {

        if (conversionId != null) {

            updateConversion(activityChatBinding.inputMessage.getText().toString());

        } else {

            HashMap<String, Object> message = new HashMap<>();
            message.put(KEY_SENDER_ID, preferenceManager.getString(KEY_USER_ID));
            message.put(KEY_RECEIVER_ID, receiverUser.id);
            message.put(KEY_MESSAGE, activityChatBinding.inputMessage.getText().toString().strip());
            message.put(KEY_TIMESTAMP, new Date());
            message.put(KEY_COUNT, count);

            firebaseFirestore.collection(KEY_COLLECTION_CHAT).add(message);

            HashMap<String, Object> conversion = new HashMap<>();
            conversion.put(KEY_SENDER_ID, preferenceManager.getString(KEY_USER_ID));
            conversion.put(KEY_SENDER_USER_NAME, preferenceManager.getString(KEY_USER_NAME));
            conversion.put(KEY_SENDER_IMAGE, preferenceManager.getString(KEY_IMAGE));
            conversion.put(KEY_RECEIVER_ID, receiverUser.id);
            conversion.put(KEY_RECEIVER_USER_NAME, receiverUser.userName);
            conversion.put(KEY_RECEIVER_IMAGE, receiverUser.image);
            conversion.put(KEY_LAST_MESSAGE, activityChatBinding.inputMessage.getText().toString().strip());
            conversion.put(KEY_TIMESTAMP, new Date());
            conversion.put(KEY_COUNT, count);
            addConversion(conversion);

        }

        activityChatBinding.inputMessage.setText(null);
    }

    private void setListeners() {

        activityChatBinding.imageBack.setOnClickListener(v -> onBackPressed());

        activityChatBinding.layoutSend.setOnClickListener(v -> {

            if (!activityChatBinding.inputMessage.getText().toString().trim().isEmpty()) {
                sendMessage();
            }

        });
        
    }

    private void addConversion(HashMap<String, Object> conversion) {

        firebaseFirestore.collection(KEY_COLLECTION_CONVERSATIONS)
                .add(conversion)
                .addOnSuccessListener(documentReference -> conversionId = documentReference.getId());

    }

    private void updateConversion(String message) {

        DocumentReference documentReference =
                firebaseFirestore.collection(KEY_COLLECTION_CONVERSATIONS).document(conversionId);

        documentReference.get().addOnSuccessListener(v -> {

            count = Objects.requireNonNull(v.get(KEY_COUNT)).hashCode() + 1;

            documentReference.update(
                    KEY_LAST_MESSAGE, message.strip(),
                    KEY_TIMESTAMP, new Date(),
                    KEY_COUNT, count
            );


            HashMap<String, Object> messageData = new HashMap<>();
            messageData.put(KEY_SENDER_ID, preferenceManager.getString(KEY_USER_ID));
            messageData.put(KEY_RECEIVER_ID, receiverUser.id);
            messageData.put(KEY_MESSAGE, message.strip());
            messageData.put(KEY_TIMESTAMP, new Date());
            messageData.put(KEY_COUNT, count);

            firebaseFirestore.collection(KEY_COLLECTION_CHAT).add(messageData);

        });



    }

    private Bitmap getReceiverUserImage(String encodedImage) {

        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

    }

    private void changeStatusBarColor() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.primary, getTheme()));
    }

    private void checkForConversion() {

        if (chatMessages.size() != 0) {

            checkForConversionRemotely(
                    preferenceManager.getString(KEY_USER_ID),
                    receiverUser.id
            );

            checkForConversionRemotely(
                    receiverUser.id,
                    preferenceManager.getString(KEY_USER_ID)
            );

        }

    }

    private void checkForConversionRemotely(String senderId, String receiverId) {

        firebaseFirestore.collection(KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(KEY_SENDER_ID, senderId)
                .whereEqualTo(KEY_RECEIVER_ID, receiverId)
                .get()
                .addOnCompleteListener(conversationOnCompleteListener);

    }

    private void changeLayoutButton(boolean flag) {

        if (flag) {

            activityChatBinding.layoutSend.setVisibility(View.VISIBLE);
            activityChatBinding.layoutFiles.setVisibility(View.INVISIBLE);

        } else {

            activityChatBinding.layoutSend.setVisibility(View.INVISIBLE);
            activityChatBinding.layoutFiles.setVisibility(View.VISIBLE);

        }

    }

    private void showDialog() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        ImageProfileDialog imageProfileDialog = new ImageProfileDialog(getReceiverUserImage(receiverUser.image));

        imageProfileDialog.show(fragmentManager, "dialog");

    }

}