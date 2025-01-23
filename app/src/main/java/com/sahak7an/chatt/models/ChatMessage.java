package com.sahak7an.chatt.models;

import android.net.Uri;

import java.util.Date;

public class ChatMessage {

    public Uri uri;
    public int count;
    public Date date;
    public Boolean isImage;
    public String senderId, receiverId, message;
    public String conversionId, conversionName, conversionImage, dateTime;

}
