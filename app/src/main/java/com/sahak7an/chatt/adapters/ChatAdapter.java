package com.sahak7an.chatt.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sahak7an.chatt.databinding.ItemContainerReceiverMessageBinding;
import com.sahak7an.chatt.databinding.ItemContainerReceiverMessageLastBinding;
import com.sahak7an.chatt.databinding.ItemContainerReceiverMessageRoundBinding;
import com.sahak7an.chatt.databinding.ItemContainerSentMessageBinding;
import com.sahak7an.chatt.databinding.ItemContainerSentMessageLastBinding;
import com.sahak7an.chatt.databinding.ItemContainerSentMessageRoundBinding;
import com.sahak7an.chatt.models.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final String senderId;
    private final List<ChatMessage> chatMessages;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_SENT_LAST = 2;
    public static final int VIEW_TYPE_SENT_ROUND = 3;
    public static final int VIEW_TYPE_RECEIVER = 4;
    public static final int VIEW_TYPE_RECEIVER_LAST = 5;
    public static final int VIEW_TYPE_RECEIVER_ROUND = 6;

    public ChatAdapter(String senderId, List<ChatMessage> chatMessages) {

        this.senderId = senderId;
        this.chatMessages = chatMessages;

    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerSentMessageBinding sentMessageBinding;

        SentMessageViewHolder(ItemContainerSentMessageBinding sentMessageBinding){

            super(sentMessageBinding.getRoot());
            this.sentMessageBinding = sentMessageBinding;

        }

        void setData(ChatMessage chatMessage) {

            sentMessageBinding.textMessage.setText(chatMessage.message);

        }

    }

    static class SentMessageLastViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerSentMessageLastBinding sentMessageLastBinding;

        SentMessageLastViewHolder(ItemContainerSentMessageLastBinding sentMessageLastBinding) {

            super(sentMessageLastBinding.getRoot());
            this.sentMessageLastBinding = sentMessageLastBinding;

        }

        void setData(ChatMessage chatMessage) {

            sentMessageLastBinding.textMessage.setText(chatMessage.message);

        }

    }

    static class SentMessageRoundViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerSentMessageRoundBinding sentMessageRoundBinding;

        SentMessageRoundViewHolder(ItemContainerSentMessageRoundBinding sentMessageRoundBinding) {

            super(sentMessageRoundBinding.getRoot());
            this.sentMessageRoundBinding = sentMessageRoundBinding;

        }

        void setData(ChatMessage chatMessage) {

            sentMessageRoundBinding.textMessage.setText(chatMessage.message);

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

    static class ReceiverMessageLastViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerReceiverMessageLastBinding receiverMessageLastBinding;

        ReceiverMessageLastViewHolder(ItemContainerReceiverMessageLastBinding receiverMessageLastBinding) {

            super(receiverMessageLastBinding.getRoot());
            this.receiverMessageLastBinding = receiverMessageLastBinding;

        }

        void setData(ChatMessage chatMessage) {

            receiverMessageLastBinding.textMessage.setText(chatMessage.message);

        }

    }

    static class ReceiverMessageRoundViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerReceiverMessageRoundBinding receiverMessageRoundBinding;

        ReceiverMessageRoundViewHolder(ItemContainerReceiverMessageRoundBinding receiverMessageRoundBinding) {

            super(receiverMessageRoundBinding.getRoot());
            this.receiverMessageRoundBinding = receiverMessageRoundBinding;

        }

        void setData(ChatMessage chatMessage) {

            receiverMessageRoundBinding.textMessage.setText(chatMessage.message);

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

        } else if (viewType == VIEW_TYPE_SENT_LAST) {

            return new SentMessageLastViewHolder(
                    ItemContainerSentMessageLastBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );

        } else if (viewType == VIEW_TYPE_SENT_ROUND) {

            return new SentMessageRoundViewHolder(
                    ItemContainerSentMessageRoundBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );

        } else if (viewType == VIEW_TYPE_RECEIVER_LAST) {

            return new ReceiverMessageLastViewHolder(
                    ItemContainerReceiverMessageLastBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );

        } else if (viewType == VIEW_TYPE_RECEIVER_ROUND) {

            return new ReceiverMessageRoundViewHolder(
                    ItemContainerReceiverMessageRoundBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );

        }

        else {

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

        } else if (getItemViewType(position) == VIEW_TYPE_SENT_LAST) {

            ((SentMessageLastViewHolder) holder).setData(chatMessages.get(position));

        } else if (getItemViewType(position) == VIEW_TYPE_SENT_ROUND) {

            ((SentMessageRoundViewHolder) holder).setData(chatMessages.get(position));

        } else if (getItemViewType(position) == VIEW_TYPE_RECEIVER_LAST) {

            ((ReceiverMessageLastViewHolder) holder).setData(chatMessages.get(position));

        } else if (getItemViewType(position) == VIEW_TYPE_RECEIVER_ROUND) {

            ((ReceiverMessageRoundViewHolder) holder).setData(chatMessages.get(position));

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

            if (position == 0) {

                if (position == chatMessages.size() - 1) {

                    return VIEW_TYPE_SENT_LAST;

                } else if (chatMessages.get(position + 1).senderId.equals(senderId)) {

                    return VIEW_TYPE_SENT;

                } else {

                    return VIEW_TYPE_SENT_LAST;

                }

            } else if(position == chatMessages.size() - 1) {

                return VIEW_TYPE_SENT_LAST;

            } else {

                if (!chatMessages.get(position - 1).senderId.equals(senderId)) {

                    return VIEW_TYPE_SENT;

                } else if (chatMessages.get(position + 1).senderId.equals(senderId)) {

                    return VIEW_TYPE_SENT_ROUND;

                } else {

                    return VIEW_TYPE_SENT_LAST;

                }

            }

        } else {

            if (position == 0) {

                if (position == chatMessages.size() - 1) {

                    return VIEW_TYPE_RECEIVER_LAST;

                } else if (!chatMessages.get(position + 1).senderId.equals(senderId)) {

                    return VIEW_TYPE_RECEIVER;

                } else {

                    return VIEW_TYPE_RECEIVER_LAST;

                }

            } else if(position == chatMessages.size() - 1) {

                return VIEW_TYPE_RECEIVER_LAST;

            } else {

                if (chatMessages.get(position - 1).senderId.equals(senderId)) {

                    return VIEW_TYPE_RECEIVER;

                } else if (!chatMessages.get(position + 1).senderId.equals(senderId)) {

                    return VIEW_TYPE_RECEIVER_ROUND;

                } else {

                    return VIEW_TYPE_RECEIVER_LAST;

                }

            }

        }

    }

}
