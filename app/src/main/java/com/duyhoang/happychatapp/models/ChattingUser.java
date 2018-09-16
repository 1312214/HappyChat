package com.duyhoang.happychatapp.models;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

// This class is used for ContactFragment, ChatRoomFragment
public class ChattingUser implements Serializable{

    private String uid;
    private String email;
    private String name;
    private String photoUrl;

    private String bio;
    private String currAddress;
    private String maritalStatus;


    public ChattingUser() {

    }

    public ChattingUser(String uid, String name, String email, String photoUrl) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
    }


    public ChattingUser(String uid, String name, String email, String photoUrl, String bio,
                        String currAddress, String maritalStatus) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
        this.bio = bio;
        this.currAddress = currAddress;
        this.maritalStatus = maritalStatus;
    }



    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCurrAddress() {
        return currAddress;
    }

    public void setCurrAddress(String currAddress) {
        this.currAddress = currAddress;
    }


    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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


    public static ChattingUser valueOf(FirebaseUser firebaseUser) {
        if(firebaseUser.getPhotoUrl() != null) {
            return new ChattingUser(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail(), firebaseUser.getPhotoUrl().toString());
        } else {
            return new ChattingUser(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail(), null);
        }
    }
}
