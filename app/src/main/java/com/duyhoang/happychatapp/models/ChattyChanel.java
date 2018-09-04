package com.duyhoang.happychatapp.models;

public class ChattyChanel {

    private ChattingUser hostUser;
    private ChattingUser guestUser;
    private ChatMessage lastestMessage;
    private String messagesOfThisChanelId;

    public ChattyChanel() {

    }

    public ChattyChanel(ChattingUser hostUser, ChattingUser guestUser, ChatMessage lastestMessage, String messagesOfThisChanelId) {
        this.hostUser = hostUser;
        this.guestUser = guestUser;
        this.lastestMessage = lastestMessage;
        this.messagesOfThisChanelId = messagesOfThisChanelId;
    }



}
