package com.duyhoang.happychatapp.models.Message;

import java.util.Date;

public class TextMessage extends Message {

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public TextMessage() {}
    public TextMessage(String senderId, String senderName, Date time, MESSAGE_TYPE type, String content) {
        super(senderId, senderName, time, type);
        this.content = content;
    }
}
