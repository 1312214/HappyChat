package com.duyhoang.happychatapp.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class BaseActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    public final int RC_GALLERY_REQUEST = 1000;
    public final int MY_PERMISSION_READ_EXTERNAL_STORAGE = 100;

    protected ProgressDialog mProgressDialog;

    protected void runRequestRuntimePermission(final Activity activity, final String[] permissions, final int customRequestCode, String notifiedMessage) {
        if(permissions.length == 1) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity,permissions[0])) {
                Snackbar.make(findViewById(android.R.id.content), notifiedMessage, Snackbar.LENGTH_INDEFINITE )
                        .setAction("Enable", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(activity, permissions, customRequestCode);
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(activity, permissions, customRequestCode);
            }
        }
    }

    public void pickImageInGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, RC_GALLERY_REQUEST);
        }
    }

    protected void showBusyDialog(String title, String message) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
//        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    protected void dismissBusyDialog() {
        if(mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }


}
