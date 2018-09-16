package com.duyhoang.happychatapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.Utils.RealTimeDataBaseUtil;
import com.duyhoang.happychatapp.fragments.ChatRoomFragment;
import com.duyhoang.happychatapp.models.ChattingUser;
import com.squareup.picasso.Picasso;


public class ProfileActivity extends AppCompatActivity implements RealTimeDataBaseUtil.DownloadCurrentUserInfoListener, View.OnClickListener
        {

    private ImageView imgAvatar;
    private TextView txtFullName, txtAddress, txtBio, txtMaritalStatus, txtEmail;
    private Button btnAddFriend;
    private ImageButton btnEditProfile;
    private ChattingUser mGuest;
    private ChattingUser mCurrUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mGuest = (ChattingUser) getIntent().getSerializableExtra("selected_user");
        initUI();
        if(mGuest != null) {
            showSelectedGuestProfile(mGuest);
        } else {
            btnAddFriend.setVisibility(View.GONE);
            RealTimeDataBaseUtil.getInstance().setDownloadCurrentUserInfoListener(this);
            RealTimeDataBaseUtil.getInstance().downloadCurrentUser();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        RealTimeDataBaseUtil.getInstance().setDownloadCurrentUserInfoListener(this);
        RealTimeDataBaseUtil.getInstance().downloadCurrentUser();

    }


    @Override
    protected void onDestroy() {
        mGuest = null;
        mCurrUser = null;
        super.onDestroy();
    }

    private void showSelectedGuestProfile(ChattingUser guest) {
btnEditProfile.setVisibility(View.GONE);
Picasso.get().load(guest.getPhotoUrl())
        .placeholder(R.drawable.ic_account_circle_black_60dp)
        .resize(120, 120)
        .centerCrop()
        .into(imgAvatar);
txtFullName.setText(guest.getName());
txtEmail.setText(guest.getEmail());
txtMaritalStatus.setText(guest.getMaritalStatus());
txtBio.setText(guest.getBio());
txtAddress.setText(guest.getBio());
if(RealTimeDataBaseUtil.getInstance().checkSelectedContactAlreadyAdded(mGuest.getUid())) {
    btnAddFriend.setText("Already added");
    btnAddFriend.setEnabled(false);
    btnEditProfile.setVisibility(View.GONE);
}
}



    @Override
    public void onFinishDownloadingCurrentUser(ChattingUser user) {
        mCurrUser = user;

        Picasso.get().load(user.getPhotoUrl())
                .placeholder(R.drawable.ic_account_circle_black_60dp)
                .resize(120, 120)
                .centerCrop()
                .into(imgAvatar);
        txtFullName.setText(user.getName());
        txtEmail.setText(user.getEmail());
        if(TextUtils.isEmpty(user.getMaritalStatus()))
            txtMaritalStatus.setText("Empty");
        else
            txtMaritalStatus.setText(user.getMaritalStatus());

        if(TextUtils.isEmpty(user.getBio()))
            txtBio.setText("Empty");
        else
            txtBio.setText(user.getBio());

        if(TextUtils.isEmpty(user.getCurrAddress()))
            txtAddress.setText("No Address");
        else
            txtAddress.setText(user.getCurrAddress());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_profile_add_friend: addNewFriend();
                break;
            case R.id.imageButton_profile_edit_profile: editMyProfile();
                break;
        }

    }




    private void initUI() {
        getSupportActionBar().hide();
        RealTimeDataBaseUtil.getInstance().setDownloadCurrentUserInfoCallback(this);

        imgAvatar = findViewById(R.id.imageView_Profile_avatar);
        txtFullName = findViewById(R.id.textView_Profile_full_name);
        txtAddress = findViewById(R.id.textView_Profile_address);
        txtBio = findViewById(R.id.textView_Profile_bio);
        txtMaritalStatus = findViewById(R.id.editText_Profile_marital_status);
        txtEmail = findViewById(R.id.textView_Profile_email);
        btnAddFriend = findViewById(R.id.button_profile_add_friend);
        btnEditProfile = findViewById(R.id.imageButton_profile_edit_profile);

        btnEditProfile.setOnClickListener(this);
        btnAddFriend.setOnClickListener(this);

    }

    private void addNewFriend() {
        RealTimeDataBaseUtil.getInstance().addNewFriendToContact(mGuest.getUid());
        btnAddFriend.setText("Added");
        btnAddFriend.setEnabled(false);
    }

    private void editMyProfile() {
        Intent intent = new Intent(this, EditingProfileActivity.class);
        intent.putExtra("current_user", mCurrUser);
        startActivity(intent);
    }

}
