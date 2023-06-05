package com.sahak7an.chatt.activities;

import static com.sahak7an.chatt.utilities.Constants.IMAGE_HEIGHT;
import static com.sahak7an.chatt.utilities.Constants.IMAGE_WIDTH;
import static com.sahak7an.chatt.utilities.Constants.KEY_COLLECTION_CHAT;
import static com.sahak7an.chatt.utilities.Constants.KEY_COLLECTION_CONVERSATIONS;
import static com.sahak7an.chatt.utilities.Constants.KEY_COLLECTION_USERS;
import static com.sahak7an.chatt.utilities.Constants.KEY_COUNT;
import static com.sahak7an.chatt.utilities.Constants.KEY_EMAIL;
import static com.sahak7an.chatt.utilities.Constants.KEY_FCM_TOKEN;
import static com.sahak7an.chatt.utilities.Constants.KEY_IMAGE;
import static com.sahak7an.chatt.utilities.Constants.KEY_IS_IMAGE;
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
import static com.sahak7an.chatt.utilities.Constants.REMOTE_MSG_DATA;
import static com.sahak7an.chatt.utilities.Constants.REMOTE_MSG_REGISTRATION_IDS;
import static com.sahak7an.chatt.utilities.Constants.getRemoteMsgHeaders;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sahak7an.chatt.R;
import com.sahak7an.chatt.adapters.ChatAdapter;
import com.sahak7an.chatt.databinding.ActivityChatBinding;
import com.sahak7an.chatt.dialogs.ImageProfileDialog;
import com.sahak7an.chatt.models.ChatMessage;
import com.sahak7an.chatt.models.User;
import com.sahak7an.chatt.network.ApiClient;
import com.sahak7an.chatt.network.ApiService;
import com.sahak7an.chatt.network.Client;
import com.sahak7an.chatt.utilities.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends BaseActivity {

    private Uri imageUri;
    private int count = 0;
    private Client client;
    private Bitmap bitmap;
    private User receiverUser;
    private Thread clientThread;
    private Boolean flag = false;
    private String imageExtension;
    private ChatAdapter chatAdapter;
    private String conversionId = null;
    private List<ChatMessage> chatMessages;
    private Boolean isReceiverOnline = false;
    private StorageReference storageReference;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore firebaseFirestore;
    private ActivityChatBinding activityChatBinding;

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {

                    if (result.getData() != null) {

                        Uri uri = result.getData().getData();

                        try {

                            InputStream inputStream = getContentResolver().openInputStream(uri);
                            bitmap = BitmapFactory.decodeStream(inputStream);

                            imageUri = result.getData().getData();
                            imageExtension = getFileExtension(imageUri);
                            uploadFile();

                        } catch (FileNotFoundException e) {

                            e.printStackTrace();

                        }
                    }
                }
            }
    );

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
                    chatMessage.dateTime = getReadableDateTime(documentChange.getDocument().getDate(KEY_TIMESTAMP));
                    chatMessage.isImage = documentChange.getDocument().getBoolean(KEY_IS_IMAGE);
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

        storageReference = FirebaseStorage.getInstance().getReference();
        activityChatBinding = ActivityChatBinding.inflate(getLayoutInflater());
        changeStatusBarColor();

        setContentView(activityChatBinding.getRoot());

        setListeners();

        init();

        loadReceiverDetails();

        listenMessage();

    }

    @Override
    protected void onResume() {
        super.onResume();

        listenAvailabilityOfReceiver();

    }

    private void loadReceiverDetails() {

        listenAvailabilityOfReceiver();
        receiverUser = (User) getIntent().getSerializableExtra(KEY_USER);

    }

    private void listenMessage() {

        firebaseFirestore.collection(KEY_COLLECTION_CHAT)
                .whereEqualTo(KEY_SENDER_ID, preferenceManager.getString(KEY_USER_ID))
                .whereEqualTo(KEY_RECEIVER_ID, preferenceManager.getString(KEY_RECEIVER_ID))
                .addSnapshotListener(eventListener);

        firebaseFirestore.collection(KEY_COLLECTION_CHAT)
                .whereEqualTo(KEY_SENDER_ID, preferenceManager.getString(KEY_RECEIVER_ID))
                .whereEqualTo(KEY_RECEIVER_ID, preferenceManager.getString(KEY_USER_ID))
                .addSnapshotListener(eventListener);

    }

    @SuppressLint("SetTextI18n")
    private void listenAvailabilityOfReceiver() {

        if (flag) {

            activityChatBinding.textStatus.setText("Sending Image");
            activityChatBinding.status.setBackground(AppCompatResources
                    .getDrawable(getApplicationContext(),
                            R.drawable.background_sending_status));

        } else {

            firebaseFirestore.collection(KEY_COLLECTION_USERS).document(preferenceManager.getString(KEY_RECEIVER_ID))
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

                            receiverUser.token = value.getString(KEY_FCM_TOKEN);

                            if (receiverUser.image == null) {

                                receiverUser.image = value.getString(KEY_IMAGE);
                                receiverUser.userName = value.getString(KEY_USER_NAME);
                                receiverUser.email = value.getString(KEY_EMAIL);
                                receiverUser.token = value.getString(KEY_FCM_TOKEN);
                                receiverUser.id = value.getId();

                                activityChatBinding.receiverImage.setImageBitmap(getResizedBitmap(getReceiverUserImage(
                                        receiverUser.image
                                )));

                                activityChatBinding.textUserName.setText(receiverUser.userName);

                            } else {

                                activityChatBinding.receiverImage.setImageBitmap(getResizedBitmap(getReceiverUserImage(
                                        preferenceManager.getString(KEY_RECEIVER_IMAGE)
                                )));

                                activityChatBinding.textUserName.setText(preferenceManager.getString(KEY_RECEIVER_USER_NAME));

                            }

                        }

                    }));

        }

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

    }

    private void sendMessage(String messageText, boolean isImage) {

        if (conversionId != null) {

            updateConversion(messageText, isImage);

        } else {

            HashMap<String, Object> message = new HashMap<>();
            message.put(KEY_SENDER_ID, preferenceManager.getString(KEY_USER_ID));
            message.put(KEY_RECEIVER_ID, receiverUser.id);
            message.put(KEY_MESSAGE, messageText.strip());
            message.put(KEY_TIMESTAMP, new Date());
            message.put(KEY_IS_IMAGE, isImage);
            message.put(KEY_COUNT, count);

            firebaseFirestore.collection(KEY_COLLECTION_CHAT).add(message);

            HashMap<String, Object> conversion = new HashMap<>();
            conversion.put(KEY_SENDER_ID, preferenceManager.getString(KEY_USER_ID));
            conversion.put(KEY_SENDER_USER_NAME, preferenceManager.getString(KEY_USER_NAME));
            conversion.put(KEY_SENDER_IMAGE, preferenceManager.getString(KEY_IMAGE));
            conversion.put(KEY_RECEIVER_ID, receiverUser.id);
            conversion.put(KEY_RECEIVER_USER_NAME, receiverUser.userName);
            conversion.put(KEY_RECEIVER_IMAGE, receiverUser.image);
            conversion.put(KEY_LAST_MESSAGE, messageText.strip());
            conversion.put(KEY_TIMESTAMP, new Date());
            conversion.put(KEY_IS_IMAGE, isImage);
            conversion.put(KEY_COUNT, count);
            addConversion(conversion);

        }

        flag = false;
        listenAvailabilityOfReceiver();

        if (!isReceiverOnline) {

            try {

                JSONArray tokens = new JSONArray();
                tokens.put(receiverUser.token);

                JSONObject data = new JSONObject();
                data.put(KEY_USER_ID, preferenceManager.getString(KEY_USER_ID));
                data.put(KEY_USER_NAME, preferenceManager.getString(KEY_USER_NAME));
                data.put(KEY_FCM_TOKEN, preferenceManager.getString(KEY_FCM_TOKEN));
                data.put(KEY_RECEIVER_IMAGE, encodedImage(getReceiverUserImage(preferenceManager.getString(KEY_IMAGE))));
                data.put(KEY_MESSAGE, activityChatBinding.inputMessage.getText().toString().strip());

                JSONObject body = new JSONObject();
                body.put(REMOTE_MSG_DATA, data);
                body.put(REMOTE_MSG_REGISTRATION_IDS, tokens);

                sendNotification(body.toString());

            } catch (Exception exception) {

                showToast(exception.getMessage());

            }

        }

        activityChatBinding.inputMessage.setText(null);
    }

    private void setListeners() {

        activityChatBinding.imageBack.setOnClickListener(v -> onBackPressed());

        activityChatBinding.layoutFiles.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);

        });

        activityChatBinding.layoutSend.setOnClickListener(v -> {

            if (!activityChatBinding.inputMessage.getText().toString().trim().isEmpty()) {
                sendMessage(activityChatBinding.inputMessage.getText().toString(), false);
            }

        });

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

        activityChatBinding.receiverImage.setOnClickListener(v -> showDialog());

        activityChatBinding.imageInfo.setOnClickListener(v -> {

            Intent intent = new Intent(getApplicationContext(), AboutUserActivity.class);
            intent.putExtra(KEY_USER, receiverUser);
            startActivity(intent);

        });
        
    }

    private void addConversion(HashMap<String, Object> conversion) {

        firebaseFirestore.collection(KEY_COLLECTION_CONVERSATIONS)
                .add(conversion)
                .addOnSuccessListener(documentReference -> conversionId = documentReference.getId());

    }

    private void updateConversion(String message, boolean isImage) {

        DocumentReference documentReference =
                firebaseFirestore.collection(KEY_COLLECTION_CONVERSATIONS).document(conversionId);

        documentReference.get().addOnSuccessListener(v -> {

            count = Objects.requireNonNull(v.get(KEY_COUNT)).hashCode() + 1;

            documentReference.update(
                    KEY_LAST_MESSAGE, message.strip(),
                    KEY_TIMESTAMP, new Date(),
                    KEY_IS_IMAGE, isImage,
                    KEY_COUNT, count
            );

            HashMap<String, Object> messageData = new HashMap<>();
            messageData.put(KEY_SENDER_ID, preferenceManager.getString(KEY_USER_ID));
            messageData.put(KEY_RECEIVER_ID, receiverUser.id);
            messageData.put(KEY_MESSAGE, message.strip());
            messageData.put(KEY_TIMESTAMP, new Date());
            messageData.put(KEY_IS_IMAGE, isImage);
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

    private void showToast(String message) {

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    }

    private void sendNotification(String messageBody) {

        ApiClient.getClient().create(ApiService.class).sendMessage(
                getRemoteMsgHeaders(),
                messageBody
        ).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                if (response.isSuccessful()) {

                    try {

                        if (response.body() != null) {

                            JSONObject responseJson = new JSONObject(response.body());
                            JSONArray results = responseJson.getJSONArray("results");

                            if (responseJson.getInt("failure") == 1) {

                                JSONObject error = (JSONObject) results.get(0);
                                showToast(error.getString("error"));

                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

            }

        });
    }

    private String getReadableDateTime(Date date) {

        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);

    }

    private String encodedImage(Bitmap bitmap) {

        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_WIDTH, IMAGE_HEIGHT, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.WEBP, 95, byteArrayOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);

    }

    private String getFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    @SuppressLint("SetTextI18n")
    private void uploadFile() {

        flag = true;

        if (imageExtension != null) {

            StorageReference fileReference = storageReference.child(
                    preferenceManager.getString(KEY_USER_ID) + "_" +
                    receiverUser.id + "/" + System.currentTimeMillis() + "." +
                            imageExtension
            );

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {

                        sendMessage(encodedImage(bitmap), true);

                    });

        }

    }

}