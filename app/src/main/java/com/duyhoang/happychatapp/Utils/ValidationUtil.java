package com.duyhoang.happychatapp.Utils;

import android.text.TextUtils;

public class ValidationUtil {

    public static final int MINIMUM_PASSWORD_LENGTH = 6;

    public static String isEmailValid(String email) {
        String strResult = null;
        if(!email.contains("@"))
            strResult = "Unvalid email";
        else if( TextUtils.isEmpty(email) )
            strResult = "Email is not empty";
        return strResult;
    }

    public static String isPasswordValid(String password) {
        String strResult = null;

        if(TextUtils.isEmpty(password)) strResult = "Password is not empty";
        else if(password.length() < MINIMUM_PASSWORD_LENGTH) strResult = "Password must have length greater 6 characters";

        return strResult;
    }

    public static String isUsernameValid(String username) {
        String strResult = null;

        if(TextUtils.isEmpty(username)) strResult = "Username is not empty";
        return strResult;
    }

}
