package com.sahak7an.chatt.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sahak7an.chatt.databinding.ItemContainerUserBinding;
import com.sahak7an.chatt.models.User;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder>{

    private final List<User> userList;

    public UsersAdapter(List<User> userList) {
        this.userList = userList;
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        ItemContainerUserBinding itemContainerUserBinding;

        UserViewHolder(ItemContainerUserBinding itemContainerUserBinding) {

            super(itemContainerUserBinding.getRoot());
            this.itemContainerUserBinding = itemContainerUserBinding;

        }

        void setUserData(User user) {

            itemContainerUserBinding.textUserName.setText(user.userName);
            itemContainerUserBinding.textEmail.setText(user.email);
            itemContainerUserBinding.imageProfile.setImageBitmap(getUserImage(user.image));

        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(

                LayoutInflater.from(parent.getContext()),
                parent,
                false

        );

        return new UserViewHolder(itemContainerUserBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        holder.setUserData(userList.get(position));

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private Bitmap getUserImage(String encodedImage) {

        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

    }
}
