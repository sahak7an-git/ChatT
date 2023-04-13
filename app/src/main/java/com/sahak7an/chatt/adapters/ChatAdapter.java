package com.sahak7an.chatt.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sahak7an.chatt.databinding.ItemContainerReceiverMessageBinding;
import com.sahak7an.chatt.databinding.ItemContainerSentMessageBinding;
import com.sahak7an.chatt.databinding.ItemContainerUserBinding;
import com.sahak7an.chatt.models.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final String senderId;
    private final List<ChatMessage> chatMessages;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVER = 2;

    public ChatAdapter(String senderId, List<ChatMessage> chatMessages) {
        this.senderId = senderId;
        this.chatMessages = chatMessages;
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerSentMessageBinding sentMessageBinding;

        SentMessageViewHolder(ItemContainerSentMessageBinding sentMessageBinding) {
            super(sentMessageBinding.getRoot());
            this.sentMessageBinding = sentMessageBinding;
        }

        void setData(ChatMessage chatMessage) {
            sentMessageBinding.textMessage.setText(chatMessage.message);

        }

    }

    static class ReceiverMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerReceiverMessageBinding receiverMessageBinding;

        ReceiverMessageViewHolder(ItemContainerReceiverMessageBinding receiverMessageBinding) {
            super(receiverMessageBinding.getRoot());
            this.receiverMessageBinding = receiverMessageBinding;
        }

        void setData(ChatMessage chatMessage) {
            receiverMessageBinding.textMessage.setText(chatMessage.message);
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        } else {
            return new ReceiverMessageViewHolder(
                    ItemContainerReceiverMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(chatMessages.get(position));
        } else {
            ((ReceiverMessageViewHolder) holder).setData(chatMessages.get(position));
        }

    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).senderId.equals(senderId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVER;
        }
    }
}
