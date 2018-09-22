package com.duyhoang.happychatapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.utils.RealTimeDataBaseUtil;
import com.duyhoang.happychatapp.utils.StorageUtil;
import com.duyhoang.happychatapp.adapters.ChatChanelRecycleViewAdapter;
import com.duyhoang.happychatapp.fragments.dialog.ViewingMessagePhotoDialogFrag;
import com.duyhoang.happychatapp.models.ChattingUser;
import com.duyhoang.happychatapp.models.message.ImageMessage;
import com.duyhoang.happychatapp.models.message.Message;
import com.duyhoang.happychatapp.models.message.TextMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Date;

public class ChatChanelActivity extends BaseActivity implements View.OnClickListener,
        RealTimeDataBaseUtil.ChattyChanelMessageListListener,
        StorageUtil.UploadingProfileImageListener, RealTimeDataBaseUtil.DownloadCurrentUserInfoListener,
        ChatChanelRecycleViewAdapter.SeeingPhotoListener{

    private RecyclerView rvMessages;
    private ChatChanelRecycleViewAdapter mChatChanelAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private ImageView imgUploadImage;
    private EditText etInputMessage;
    private ImageView imageSend;
    private ChattingUser mGuest;
    private Uri selectedImgUri;
    private ChattingUser currLoginedUser;
    private boolean isChoosingImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_chanel);
        mGuest = (ChattingUser) getIntent().getSerializableExtra("selected_contact");
        initUI();
        RealTimeDataBaseUtil.getInstance().mChattyChanelMessageList = new ArrayList<>();
        RealTimeDataBaseUtil.getInstance().setChattyChanelMessageListListener(this);
        RealTimeDataBaseUtil.getInstance().setDownloadCurrentUserInfoListener(this);
        StorageUtil.getInstance().setUploadingProfileImageListener(this);
        RealTimeDataBaseUtil.getInstance().downloadMessageChanelWithSelectedContact(mGuest.getUid());
        RealTimeDataBaseUtil.getInstance().downloadCurrentUser();

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mChatChanelAdapter = new ChatChanelRecycleViewAdapter(this, RealTimeDataBaseUtil.getInstance().mChattyChanelMessageList, mGuest);
        rvMessages.setAdapter(mChatChanelAdapter);
        rvMessages.setLayoutManager(mLinearLayoutManager);
        mChatChanelAdapter.setSeeingPhotoListener(this);
    }

    @Override
    protected void onStop() {
        if(!isChoosingImage) {
            RealTimeDataBaseUtil.getInstance().removeChildEventListenerOnCurrentChanelMessageId();
        }
        super.onStop();

    }


    @Override
    protected void onDestroy() {
        RealTimeDataBaseUtil.getInstance().mChattyChanelMessageList = null;
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_chanel_activity_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_chatchanel_profile:
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("selected_user", mGuest);
                startActivity(intent);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default: return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageView_chatChanel_upload_image: sendImageMessage();
                break;
            case R.id.imageView_chatChanel_send: sendTextMessage();
                break;
        }
    }

    @Override
    public void onNewMessageInserted(int position) {
        mChatChanelAdapter.notifyItemInserted(position);
        rvMessages.scrollToPosition(position);
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MY_PERMISSION_READ_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            isChoosingImage = true;
            pickImageInGallery();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_GALLERY_REQUEST) {
            isChoosingImage = false;
            if(resultCode == RESULT_OK){
                selectedImgUri = data.getData();
                StorageUtil.getInstance().uploadMessageImageToStorage(selectedImgUri, this);
            }
        }
    }


    @Override
    public void onCompleteGettingDownloadUrl(String downloadUrl) {
        ImageMessage imageMessage = new ImageMessage(currLoginedUser.getUid(),
                currLoginedUser.getName(), new Date(), Message.MESSAGE_TYPE.IMAGE, downloadUrl, false);
        RealTimeDataBaseUtil.getInstance().uploadMessageToFirebaseDatabase(imageMessage, mGuest.getUid());
    }

    @Override
    public void onFinishDownloadingCurrentUser(ChattingUser user) {
        currLoginedUser = user;
    }

    @Override
    public void onShowImagePhoto(String photoUrl) {
        showPhotoWithinDialog(photoUrl);
    }

    private void initUI() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mGuest.getName());
        }

        rvMessages = findViewById(R.id.recyclerView_message_list);
        imgUploadImage = findViewById(R.id.imageView_chatChanel_upload_image);
        imageSend = findViewById(R.id.imageView_chatChanel_send);
        etInputMessage = findViewById(R.id.editText_chatChanel_message_content);

        imageSend.setOnClickListener(this);
        imgUploadImage.setOnClickListener(this);
    }


    private void sendTextMessage() {
        String messageContent = etInputMessage.getText().toString();
        if(messageContent.length() > 0) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            String senderId = currentUser.getUid();
            String senderName = currentUser.getDisplayName();
            Date date = new Date();
            TextMessage msg = new TextMessage(senderId, senderName, date, Message.MESSAGE_TYPE.TEXT, messageContent, false);
            RealTimeDataBaseUtil.getInstance().uploadMessageToFirebaseDatabase(msg, mGuest.getUid());
            etInputMessage.setText("");
        }

    }

    private void sendImageMessage() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            isChoosingImage = true;
            pickImageInGallery();
        } else {
            runRequestRuntimePermission(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_READ_EXTERNAL_STORAGE,
                    "App needs permssion to access your gallery");
        }
    }


    private void showPhotoWithinDialog(String photoUrl) {
        ViewingMessagePhotoDialogFrag dialogFrag = ViewingMessagePhotoDialogFrag.getInstance(photoUrl);
        dialogFrag.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_Fullscreen);
        dialogFrag.show(getSupportFragmentManager(), "viewing_photo_dialog");
    }





}
