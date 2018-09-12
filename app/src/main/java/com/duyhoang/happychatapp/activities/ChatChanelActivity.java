package com.duyhoang.happychatapp.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.Utils.RealTimeDataBaseUtil;
import com.duyhoang.happychatapp.adapters.ChatChanelRecycleViewAdapter;
import com.duyhoang.happychatapp.models.ChattingUser;
import com.duyhoang.happychatapp.models.Message.Message;
import com.duyhoang.happychatapp.models.Message.TextMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

public class ChatChanelActivity extends AppCompatActivity implements View.OnClickListener, RealTimeDataBaseUtil.ChattyChanelMessageListListener, View.OnLayoutChangeListener {

    private RecyclerView rvMessages;
    private ChatChanelRecycleViewAdapter mChatChanelAdapter;
    private ImageView imgUploadImage;
    private EditText etInputMessage;
    private ImageView imageSend;
    private ChattingUser mGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_chanel);
        mGuest = (ChattingUser) getIntent().getSerializableExtra("selected_contact");
        initUI();
        RealTimeDataBaseUtil.getInstance().setChattyChanelMessageListListener(this);
        RealTimeDataBaseUtil.getInstance().downloadMessageChanelWithSelectedContact(mGuest.getUid());
        mChatChanelAdapter = new ChatChanelRecycleViewAdapter(this, RealTimeDataBaseUtil.getInstance().mChattyChanelMessageList, mGuest);
        rvMessages.setAdapter(mChatChanelAdapter);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.addOnLayoutChangeListener(this);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_chanel_activity_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_chatchanel_profile: startActivity(new Intent(this, ProfileActivity.class));
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
    public void onNewMessageInserted(int postion) {
        mChatChanelAdapter.notifyItemInserted(postion);
        rvMessages.scrollToPosition(postion);
    }

    @Override
    protected void onStop() {
        RealTimeDataBaseUtil.getInstance().removeChildEventListenerOnCurrentChanelMessageId();
        super.onStop();
    }

    @Override
    public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
        rvMessages.scrollToPosition(mChatChanelAdapter.getItemCount() - 1);
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


    // implement these and load messages from database.
    private void sendTextMessage() {
        String messageContent = etInputMessage.getText().toString();
        if(messageContent.length() > 0) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            String senderId = currentUser.getUid();
            String senderName = currentUser.getDisplayName();
            Date date = new Date();
            TextMessage msg = new TextMessage(senderId, senderName, date, Message.MESSAGE_TYPE.TEXT, messageContent);
            RealTimeDataBaseUtil.getInstance().uploadMessageToFirebaseDatabase(msg, mChatChanelAdapter.getGuestId());
            etInputMessage.setText("");
        }

    }

    private void sendImageMessage() {
    }


}
