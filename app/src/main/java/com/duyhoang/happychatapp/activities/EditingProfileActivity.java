package com.duyhoang.happychatapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.utils.RealTimeDataBaseUtil;
import com.duyhoang.happychatapp.utils.StorageUtil;
import com.duyhoang.happychatapp.fragments.dialog.AlertDialogFragment;
import com.duyhoang.happychatapp.models.ChattingUser;
import com.google.firebase.auth.FirebaseAuth;


public class EditingProfileActivity extends BaseActivity implements AlertDialogFragment.AlertDialogFragmentListener,
        RealTimeDataBaseUtil.UpdatingCompletionProfileListener, StorageUtil.UploadingProfileImageListener{



    private ImageView imgAvatar;
    private AppCompatEditText etFullName, etAddress, etBio, etEmail, etMaritalStatus;
    private ChattingUser mSentUser;

    private Uri selectedImageUri;
    private String downloadImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing_profile);
        mSentUser = (ChattingUser) getIntent().getSerializableExtra("current_user");
        RealTimeDataBaseUtil.getInstance().setUpdatingCompletionListener(this);
        StorageUtil.getInstance().setUploadingProfileImageListener(this);

        initUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_editProfile_done: showAlertDialogForAskingSure();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_GALLERY_REQUEST && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            RequestOptions requestOptions = new RequestOptions()
                    .override(120)
                    .centerCrop();
            Glide.with(this)
                    .load(selectedImageUri)
                    .apply(requestOptions)
                    .into(imgAvatar);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MY_PERMISSION_READ_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImageInGallery();
        }
    }

    private void initUI() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("Profile Editing");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        imgAvatar = findViewById(R.id.imageView_editingProfile_avatar);
        etFullName = findViewById(R.id.editText_editingProfile_full_name);
        etAddress = findViewById(R.id.editText_editingProfile_address);
        etBio = findViewById(R.id.editText_editingProfile_bio);
        etEmail = findViewById(R.id.editText_editingProfile_email);
        etMaritalStatus = findViewById(R.id.editText_editingProfile_marital_status);

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(EditingProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    pickImageInGallery();
                } else {
                    String message = "Need permissions to access your gallery";
                    runRequestRuntimePermission(EditingProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_READ_EXTERNAL_STORAGE, message);
                }


            }
        });

        RequestOptions requestOptions = new RequestOptions()
                .override(120)
                .placeholder(R.drawable.ic_account_circle_black_60dp)
                .centerCrop();
        Glide.with(this)
                .load(mSentUser.getPhotoUrl())
                .apply(requestOptions)
                .into(imgAvatar);
        resetAllEditTextInProfile();

        etFullName.setText(mSentUser.getName());
        if(TextUtils.isEmpty(mSentUser.getCurrAddress()))
            etAddress.setHint("Address");
        else
            etAddress.setText(mSentUser.getCurrAddress());
        etEmail.setText(mSentUser.getEmail());
        if(TextUtils.isEmpty(mSentUser.getBio()))
            etBio.setHint("Talk something about yourself");
        else
            etBio.setText(mSentUser.getBio());
        if(TextUtils.isEmpty(mSentUser.getMaritalStatus()))
            etMaritalStatus.setHint("Single/Maried/Complicated");
        else
            etMaritalStatus.setText(mSentUser.getMaritalStatus());

    }



    private void resetAllEditTextInProfile() {
        etFullName.setText("");
        etBio.setText("");
        etAddress.setText("");
        etEmail.setText("");
        etMaritalStatus.setText("");
    }

    public void showAlertDialogForAskingSure() {
        AlertDialogFragment dialogFragment = AlertDialogFragment.getInstance("Confirmation");
        dialogFragment.show(getSupportFragmentManager(), "alert_dialog_frag");
    }


    @Override
    public void onPositiveButtonClicked() {
        if(selectedImageUri != null) {
            StorageUtil.getInstance().uploadProfileImageToStorage(selectedImageUri, this);

        } else {
            ChattingUser updatedUser = new ChattingUser(FirebaseAuth.getInstance().getUid(), etFullName.getText().toString(),
                    etEmail.getText().toString(), mSentUser.getPhotoUrl(),  etBio.getText().toString(), etAddress.getText().toString(),
                    etMaritalStatus.getText().toString());
            RealTimeDataBaseUtil.getInstance().updateProfile(updatedUser);
        }


    }

    @Override
    public void onCompleteGettingDownloadUrl(String downloadUrl) {
        downloadImageUrl = downloadUrl;
        ChattingUser updatedUser = new ChattingUser(FirebaseAuth.getInstance().getUid(), etFullName.getText().toString(),
                etEmail.getText().toString(), downloadImageUrl,  etBio.getText().toString(), etAddress.getText().toString(),
                etMaritalStatus.getText().toString());
        RealTimeDataBaseUtil.getInstance().updateProfile(updatedUser);
    }

    @Override
    public void onCompleteUpdatingUser() {
        Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }
}
