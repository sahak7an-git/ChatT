package com.sahak7an.chatt.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sahak7an.chatt.databinding.ItemContainerRecentConversionBinding;
import com.sahak7an.chatt.listeners.ConversationListener;
import com.sahak7an.chatt.models.ChatMessage;
import com.sahak7an.chatt.models.User;

import java.util.List;

public class RecentConversationsAdapter extends RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder> {

    private final List<ChatMessage> chatMessages;
    private final ConversationListener conversationListener;

    public RecentConversationsAdapter(List<ChatMessage> chatMessages, ConversationListener conversationListener) {
        this.chatMessages = chatMessages;
        this.conversationListener = conversationListener;
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder {

        ItemContainerRecentConversionBinding recentConversionBinding;

        ConversionViewHolder(ItemContainerRecentConversionBinding recentConversionBinding) {

            super(recentConversionBinding.getRoot());

            this.recentConversionBinding = recentConversionBinding;

        }

        void setData(ChatMessage chatMessage) {

            recentConversionBinding.imageProfile.setImageBitmap(getConversionImage(chatMessage.conversionImage));
            recentConversionBinding.textUserName.setText(chatMessage.conversionName);
            recentConversionBinding.textRecentMessage.setText(chatMessage.message);
            recentConversionBinding.getRoot().setOnClickListener(v -> {

                User user = new User();
                user.id = chatMessage.conversionId;
                user.userName = chatMessage.conversionName;
                user.image = chatMessage.conversionImage;
                conversationListener.onConversationClicked(user);

            });

        }

    }

    private Bitmap getConversionImage(String encodedImage) {

        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

    }

    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ConversionViewHolder(

                ItemContainerRecentConversionBinding.inflate(

                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false

                )

        );

    }

    @Override
    public void onBindViewHolder(@NonNull ConversionViewHolder holder, int position) {

        holder.setData(chatMessages.get(position));

    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }
}