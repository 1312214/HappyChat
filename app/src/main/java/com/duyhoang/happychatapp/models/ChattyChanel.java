package com.duyhoang.happychatapp.models;

import com.duyhoang.happychatapp.models.Message.Message;

public class ChattyChanel {

    private ChattingUser hostUser;
    private ChattingUser guestUser;
    private Message lastestMessage;
    private String messagesOfThisChanelId;

    public ChattyChanel() {

    }

    public ChattyChanel(ChattingUser hostUser, ChattingUser guestUser, Message lastestMessage, String messagesOfThisChanelId) {
        this.hostUser = hostUser;
        this.guestUser = guestUser;
        this.lastestMessage = lastestMessage;
        this.messagesOfThisChanelId = messagesOfThisChanelId;
    }



}
