package com.duyhoang.happychatapp.models;

import com.duyhoang.happychatapp.models.message.Message;

import java.util.Comparator;
import java.util.Date;

public class ChattyChanel {

    private String chanelId;
    private ChattingUser guestUser;
    private Message lastestMessage;

    public ChattingUser getGuestUser() {
        return guestUser;
    }

    public String getChanelId() {
        return chanelId;
    }

    public void setChanelId(String chanelId) {
        this.chanelId = chanelId;
    }

    public void setGuestUser(ChattingUser guestUser) {
        this.guestUser = guestUser;
    }

    public Message getLastestMessage() {
        return lastestMessage;
    }

    public void setLastestMessage(Message lastestMessage) {
        this.lastestMessage = lastestMessage;
    }

    public ChattyChanel() {

    }

    public ChattyChanel(String chanelId, ChattingUser guestUser, Message lastestMessage) {
        this.chanelId = chanelId;
        this.guestUser = guestUser;
        this.lastestMessage = lastestMessage;
    }


    public class SortDescendingByLatestMessageDate implements Comparator<ChattyChanel> {
        @Override
        public int compare(ChattyChanel t1, ChattyChanel t2) {
            Date d1 = t1.lastestMessage.getTime();
            Date d2 = t2.lastestMessage.getTime();
            return (d1.compareTo(d2)) * (-1);
        }


    }


}
