package com.duyhoang.happychatapp.utils;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.duyhoang.happychatapp.activities.EditingProfileActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;



import java.util.UUID;


public class StorageUtil {

    public static String TAG = "StorageUtil";

    private StorageReference mStorage;
    private UploadingProfileImageListener mUploadingProfileImageListener;

    private static StorageUtil instance;

    private StorageUtil() {
        mStorage = FirebaseStorage.getInstance().getReference();
    }

    public static StorageUtil getInstance() {
        if(instance == null)
            instance = new StorageUtil();
        return instance;
    }

    private boolean isUploadingSucessful;
    public void uploadProfileImageToStorage(Uri selectedImageUri, final EditingProfileActivity activity) {

        final StorageReference imgStorageRef = mStorage.child("profile_images/" + UUID.randomUUID());
        final UploadTask uploadTask = imgStorageRef.putFile(selectedImageUri);
        uploadTask.addOnSuccessListener(activity, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i(TAG, "Uploading the image successfully");
                isUploadingSucessful = true;
                downloadImageUrlWhenUploadingSuccessfully(uploadTask, imgStorageRef, activity);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"Uploading the image failed");
                e.printStackTrace();
            }
        });

    }


    private void downloadImageUrlWhenUploadingSuccessfully(UploadTask uploadTask,final StorageReference imgStorageRef, Activity activity ) {
        if(isUploadingSucessful) {
            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return imgStorageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(activity, new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()) {
                        if(mUploadingProfileImageListener != null)
                            mUploadingProfileImageListener.onCompleteGettingDownloadUrl(task.getResult().toString());

                    } else {
                        Log.e(TAG, "GetDownloadUrl failed");
                        task.getException().printStackTrace();
                    }

                }
            });
            isUploadingSucessful = false;
        }
    }

    public void uploadMessageImageToStorage(Uri selectedImageUri, final Activity activity){
       final StorageReference imgRef = mStorage.child("message_images/" + UUID.randomUUID());
       final UploadTask uploadTask = imgRef.putFile(selectedImageUri);
       uploadTask.addOnSuccessListener(activity, new OnSuccessListener<UploadTask.TaskSnapshot>() {
           @Override
           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               isUploadingSucessful = true;
               downloadImageUrlWhenUploadingSuccessfully(uploadTask, imgRef, activity);
           }
       }).addOnFailureListener(activity, new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               Log.e(TAG, "Uploading image failed");
               e.printStackTrace();
           }
       });

       isUploadingSucessful = false;
    }


    public void setUploadingProfileImageListener(UploadingProfileImageListener listener) {
        mUploadingProfileImageListener = listener;
    }

    public interface UploadingProfileImageListener{
        void onCompleteGettingDownloadUrl(String downloadUrl);
    }

}
