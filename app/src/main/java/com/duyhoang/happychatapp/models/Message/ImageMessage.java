package com.duyhoang.happychatapp.models.Message;

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

    public ImageMessage(String senderId, String senderName, Date time, MESSAGE_TYPE type, String photoUrl) {
        super(senderId, senderName, time, type);
        this.photoUrl = photoUrl;
    }
}
