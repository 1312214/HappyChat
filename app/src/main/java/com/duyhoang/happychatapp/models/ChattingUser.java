package com.duyhoang.happychatapp.models;

// This class is used for ContactFragment, ChatRoomFragment
public class ChattingUser {

    private String uid;
    private String email;
    private String displayName;
    private String photoUrl;

    public ChattingUser() {

    }


    public ChattingUser(String uid, String displayName, String email, String photoUrl) {
        this.uid = uid;
        this.displayName = displayName;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return displayName;
    }

    public void setName(String name) {
        this.displayName = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
