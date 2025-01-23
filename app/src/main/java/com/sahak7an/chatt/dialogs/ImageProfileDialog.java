package com.sahak7an.chatt.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.makeramen.roundedimageview.RoundedImageView;
import com.sahak7an.chatt.R;

import java.util.Objects;

public class ImageProfileDialog extends DialogFragment implements View.OnClickListener {

    Bitmap userImage;

    public ImageProfileDialog(Bitmap userImage) {

        this.userImage = userImage;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_user_image, null);
        RoundedImageView roundedImageView = view.findViewById(R.id.imageProfile);

        Objects.requireNonNull(getDialog()).getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.argb(0, 0, 0, 0)));

        getDialog().getWindow().setDimAmount(5);

        view.startAnimation(AnimationUtils.loadAnimation(
                getContext(), R.anim.fade_in
        ));

        roundedImageView.setImageBitmap(getResizedBitmap(userImage));

        return view;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;

    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        dialog.dismiss();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);

        dialog.cancel();
    }

    @Nullable
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onClick(View view) {

        dismiss();

    }

    private Bitmap getResizedBitmap(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) 1440) / width;
        float scaleHeight = ((float) 2560) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, width, height, matrix, false);
        bitmap.recycle();

        return resizedBitmap;

    }

}
