package com.duyhoang.happychatapp.models.message;

import java.util.Date;

public class Message {

    public enum MESSAGE_TYPE {IMAGE, TEXT};


    protected String msgId;
    protected String senderId;
    protected String senderName;
    protected Date time;
    protected MESSAGE_TYPE type;
    protected String chanelId;
    protected boolean isRead;

    public boolean getIsRead() {
        return isRead;
    }


    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }



    public String getChanelId() {
        return chanelId;
    }

    public void setChanelId(String chanelId) {
        this.chanelId = chanelId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public MESSAGE_TYPE getType() {
        return type;
    }

    public void setType(MESSAGE_TYPE type) {
        this.type = type;
    }

    public Message() {}

    public Message(String senderId, String senderName, Date time, MESSAGE_TYPE type, boolean isRead) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.time = time;
        this.type = type;
        this.isRead = isRead;
    }


}
