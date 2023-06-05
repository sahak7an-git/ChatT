package com.sahak7an.chatt.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sahak7an.chatt.databinding.ItemContainerReceiverImageBinding;
import com.sahak7an.chatt.databinding.ItemContainerReceiverImageLastBinding;
import com.sahak7an.chatt.databinding.ItemContainerReceiverImageRoundBinding;
import com.sahak7an.chatt.databinding.ItemContainerReceiverMessageBinding;
import com.sahak7an.chatt.databinding.ItemContainerReceiverMessageLastBinding;
import com.sahak7an.chatt.databinding.ItemContainerReceiverMessageRoundBinding;
import com.sahak7an.chatt.databinding.ItemContainerSentImageBinding;
import com.sahak7an.chatt.databinding.ItemContainerSentImageLastBinding;
import com.sahak7an.chatt.databinding.ItemContainerSentImageRoundBinding;
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
    public static final int VIEW_TYPE_SENT_IMAGE = 3;
    public static final int VIEW_TYPE_SENT_ROUND = 4;
    public static final int VIEW_TYPE_SENT_IMAGE_LAST = 5;
    public static final int VIEW_TYPE_SENT_IMAGE_ROUND = 6;
    public static final int VIEW_TYPE_RECEIVER = 7;
    public static final int VIEW_TYPE_RECEIVER_LAST = 8;
    public static final int VIEW_TYPE_RECEIVER_IMAGE = 9;
    public static final int VIEW_TYPE_RECEIVER_ROUND = 10;
    public static final int VIEW_TYPE_RECEIVER_IMAGE_LAST = 11;
    public static final int VIEW_TYPE_RECEIVER_IMAGE_ROUND = 12;

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
            sentMessageLastBinding.textDateTime.setText(chatMessage.dateTime);

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

    static class SentImageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerSentImageBinding sentImageBinding;

        SentImageViewHolder(ItemContainerSentImageBinding sentImageBinding) {

            super(sentImageBinding.getRoot());
            this.sentImageBinding = sentImageBinding;

        }

        void setData(ChatMessage chatMessage) {

            sentImageBinding.imageInfo.setImageBitmap(getResizedBitmap(chatMessage.message));

        }

        private Bitmap getResizedBitmap(String encodedImage) {

            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            return Bitmap.createScaledBitmap(bitmap, 250 * 2, 350 * 2, false);

        }

    }

    static class SentImageLastViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerSentImageLastBinding sentImageLastBinding;

        SentImageLastViewHolder(ItemContainerSentImageLastBinding sentImageLastBinding) {

            super(sentImageLastBinding.getRoot());
            this.sentImageLastBinding = sentImageLastBinding;

        }

        void setData(ChatMessage chatMessage) {

            sentImageLastBinding.imageInfo.setImageBitmap(getResizedBitmap(chatMessage.message));
            sentImageLastBinding.textDateTime.setText(chatMessage.dateTime);

        }

        private Bitmap getResizedBitmap(String encodedImage) {

            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            return Bitmap.createScaledBitmap(bitmap, 250 * 2, 350 * 2, false);

        }

    }

    static class SentImageRoundViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerSentImageRoundBinding sentImageRoundBinding;

        SentImageRoundViewHolder(ItemContainerSentImageRoundBinding sentImageRoundBinding) {

            super(sentImageRoundBinding.getRoot());
            this.sentImageRoundBinding = sentImageRoundBinding;

        }

        void setData(ChatMessage chatMessage) {

            sentImageRoundBinding.imageInfo.setImageBitmap(getResizedBitmap(chatMessage.message));

        }

        private Bitmap getResizedBitmap(String encodedImage) {

            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            return Bitmap.createScaledBitmap(bitmap, 250 * 2, 350 * 2, false);

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
            receiverMessageLastBinding.textDateTime.setText(chatMessage.dateTime);

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

    static class ReceiverImageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerReceiverImageBinding receiverImageBinding;

        ReceiverImageViewHolder(ItemContainerReceiverImageBinding receiverImageBinding) {

            super(receiverImageBinding.getRoot());
            this.receiverImageBinding = receiverImageBinding;

        }

        void setData(ChatMessage chatMessage) {

            receiverImageBinding.imageInfo.setImageBitmap(getResizedBitmap(chatMessage.message));

        }

        private Bitmap getResizedBitmap(String encodedImage) {

            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            return Bitmap.createScaledBitmap(bitmap, 250 * 2, 350 * 2, false);

        }

    }

    static class ReceiverImageLastViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerReceiverImageLastBinding receiverImageLastBinding;

        ReceiverImageLastViewHolder(ItemContainerReceiverImageLastBinding receiverImageLastBinding) {

            super(receiverImageLastBinding.getRoot());
            this.receiverImageLastBinding = receiverImageLastBinding;

        }

        void setData(ChatMessage chatMessage) {

            receiverImageLastBinding.imageInfo.setImageBitmap(getResizedBitmap(chatMessage.message));
            receiverImageLastBinding.textDateTime.setText(chatMessage.dateTime);

        }

        private Bitmap getResizedBitmap(String encodedImage) {

            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            return Bitmap.createScaledBitmap(bitmap, 250 * 2, 350 * 2, false);

        }

    }

    static class ReceiverImageRoundViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerReceiverImageRoundBinding receiverImageRoundBinding;

        ReceiverImageRoundViewHolder(ItemContainerReceiverImageRoundBinding receiverImageRoundBinding) {

            super(receiverImageRoundBinding.getRoot());
            this.receiverImageRoundBinding = receiverImageRoundBinding;

        }

        void setData(ChatMessage chatMessage) {

            receiverImageRoundBinding.imageInfo.setImageBitmap(getResizedBitmap(chatMessage.message));

        }

        private Bitmap getResizedBitmap(String encodedImage) {

            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            return Bitmap.createScaledBitmap(bitmap, 250 * 2, 350 * 2, false);

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

        } else if (viewType == VIEW_TYPE_SENT_IMAGE) {

            return new SentImageViewHolder(
                    ItemContainerSentImageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );

        } else if (viewType == VIEW_TYPE_SENT_IMAGE_ROUND) {

            return new SentImageRoundViewHolder(
                    ItemContainerSentImageRoundBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );

        } else if (viewType == VIEW_TYPE_SENT_IMAGE_LAST) {

            return new SentImageLastViewHolder(
                    ItemContainerSentImageLastBinding.inflate(
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

        } else if (viewType == VIEW_TYPE_RECEIVER){

            return new ReceiverMessageViewHolder(
                    ItemContainerReceiverMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );

        } else if (viewType == VIEW_TYPE_RECEIVER_IMAGE) {

            return new ReceiverImageViewHolder(
                    ItemContainerReceiverImageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );

        } else if (viewType == VIEW_TYPE_RECEIVER_IMAGE_ROUND) {

            return new ReceiverImageRoundViewHolder(
                    ItemContainerReceiverImageRoundBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );

        } else {

            return new ReceiverImageLastViewHolder(
                    ItemContainerReceiverImageLastBinding.inflate(
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

        } else if (getItemViewType(position) == VIEW_TYPE_SENT_IMAGE) {

            ((SentImageViewHolder) holder).setData(chatMessages.get(position));

        } else if (getItemViewType(position) == VIEW_TYPE_SENT_IMAGE_ROUND) {

            ((SentImageRoundViewHolder) holder).setData(chatMessages.get(position));

        } else if (getItemViewType(position) == VIEW_TYPE_SENT_IMAGE_LAST) {

            ((SentImageLastViewHolder) holder).setData(chatMessages.get(position));

        } else if (getItemViewType(position) == VIEW_TYPE_RECEIVER_LAST) {

            ((ReceiverMessageLastViewHolder) holder).setData(chatMessages.get(position));

        } else if (getItemViewType(position) == VIEW_TYPE_RECEIVER_ROUND) {

            ((ReceiverMessageRoundViewHolder) holder).setData(chatMessages.get(position));

        } else if (getItemViewType(position) == VIEW_TYPE_RECEIVER){

            ((ReceiverMessageViewHolder) holder).setData(chatMessages.get(position));

        } else if (getItemViewType(position) == VIEW_TYPE_RECEIVER_IMAGE) {

            ((ReceiverImageViewHolder) holder).setData(chatMessages.get(position));

        } else if (getItemViewType(position) == VIEW_TYPE_RECEIVER_IMAGE_ROUND) {

            ((ReceiverImageRoundViewHolder) holder).setData(chatMessages.get(position));

        } else {

            ((ReceiverImageLastViewHolder) holder).setData(chatMessages.get(position));

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

                    if (chatMessages.get(position).isImage) {

                        return VIEW_TYPE_SENT_IMAGE_LAST;

                    } else {

                        return VIEW_TYPE_SENT_LAST;

                    }

                } else if (chatMessages.get(position + 1).senderId.equals(senderId)) {

                    if (chatMessages.get(position).isImage) {

                        return VIEW_TYPE_SENT_IMAGE;

                    } else {

                        return VIEW_TYPE_SENT;

                    }

                } else {

                    if (chatMessages.get(position).isImage) {

                        return VIEW_TYPE_SENT_IMAGE_LAST;

                    } else {

                        return VIEW_TYPE_SENT_LAST;

                    }

                }

            } else if(position == chatMessages.size() - 1) {

                if (chatMessages.get(position).isImage) {

                    return VIEW_TYPE_SENT_IMAGE_LAST;

                } else {

                    return VIEW_TYPE_SENT_LAST;

                }

            } else {

                if (!chatMessages.get(position - 1).senderId.equals(senderId)) {

                    if (chatMessages.get(position + 1).senderId.equals(senderId)) {

                        if (chatMessages.get(position).isImage) {

                            return VIEW_TYPE_SENT_IMAGE;

                        } else {

                            return VIEW_TYPE_SENT;

                        }

                    } else {

                        if (chatMessages.get(position).isImage) {

                            return VIEW_TYPE_SENT_IMAGE_LAST;

                        } else {

                            return VIEW_TYPE_SENT_LAST;

                        }

                    }

                } else if (chatMessages.get(position + 1).senderId.equals(senderId)) {

                    if (chatMessages.get(position).isImage) {

                        return VIEW_TYPE_SENT_IMAGE_ROUND;

                    } else {

                        return VIEW_TYPE_SENT_ROUND;

                    }

                } else {

                    if (chatMessages.get(position).isImage) {

                        return VIEW_TYPE_SENT_IMAGE_LAST;

                    } else {

                        return VIEW_TYPE_SENT_LAST;

                    }

                }

            }

        } else {

            if (position == 0) {

                if (position == chatMessages.size() - 1) {

                    if (chatMessages.get(position).isImage) {

                        return VIEW_TYPE_RECEIVER_IMAGE_LAST;

                    } else {

                        return VIEW_TYPE_RECEIVER_LAST;

                    }

                } else if (!chatMessages.get(position + 1).senderId.equals(senderId)) {

                    if (chatMessages.get(position).isImage) {

                        return VIEW_TYPE_RECEIVER_IMAGE;

                    } else {

                        return VIEW_TYPE_RECEIVER;

                    }

                } else {

                    if (chatMessages.get(position).isImage) {

                        return VIEW_TYPE_RECEIVER_IMAGE_LAST;

                    } else {

                        return VIEW_TYPE_RECEIVER_LAST;

                    }

                }

            } else if(position == chatMessages.size() - 1) {

                if (chatMessages.get(position).isImage) {

                    return VIEW_TYPE_RECEIVER_IMAGE_LAST;

                } else {

                    return VIEW_TYPE_RECEIVER_LAST;

                }

            } else {

                if (chatMessages.get(position - 1).senderId.equals(senderId)) {

                    if (!chatMessages.get(position + 1).senderId.equals(senderId)) {

                        if (chatMessages.get(position).isImage) {

                            return VIEW_TYPE_RECEIVER_IMAGE;

                        } else {

                            return VIEW_TYPE_RECEIVER;

                        }

                    } else {

                        if (chatMessages.get(position).isImage) {

                            return VIEW_TYPE_RECEIVER_IMAGE_LAST;

                        } else {

                            return VIEW_TYPE_RECEIVER_LAST;

                        }

                    }

                } else if (!chatMessages.get(position + 1).senderId.equals(senderId)) {

                    if (chatMessages.get(position).isImage) {

                        return VIEW_TYPE_RECEIVER_IMAGE_ROUND;

                    } else {

                        return VIEW_TYPE_RECEIVER_ROUND;

                    }

                } else {

                    if (chatMessages.get(position).isImage) {

                        return VIEW_TYPE_RECEIVER_IMAGE_LAST;

                    } else {

                        return VIEW_TYPE_RECEIVER_LAST;

                    }

                }

            }

        }

    }

}
