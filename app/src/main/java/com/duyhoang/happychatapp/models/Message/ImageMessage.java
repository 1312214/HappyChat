package com.duyhoang.happychatapp.models.Message;

import java.util.Date;

public class ImageMessage extends Message {
    private String photoUrl;
    private String fileName;



    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ImageMessage() {}

    public ImageMessage(String senderId, String senderName, Date time, MESSAGE_TYPE type, String photoUrl, String fileName) {
        super(senderId, senderName, time, type);
        this.photoUrl = photoUrl;
        this.fileName = fileName;
    }
}
