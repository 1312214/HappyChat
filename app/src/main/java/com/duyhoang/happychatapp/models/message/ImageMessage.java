package com.duyhoang.happychatapp.models.message;

import java.util.Date;

public class ImageMessage extends Message {
    private String photoUrl;


    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }


    public ImageMessage() {}

    public ImageMessage(String senderId, String senderName, Date time, MESSAGE_TYPE type, String photoUrl, boolean isRead) {
        super(senderId, senderName, time, type, isRead);
        this.photoUrl = photoUrl;
    }
}
