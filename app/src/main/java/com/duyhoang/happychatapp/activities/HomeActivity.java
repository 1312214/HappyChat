package com.duyhoang.happychatapp.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.utils.RealTimeDataBaseUtil;
import com.duyhoang.happychatapp.fragments.ContactFragment;
import com.duyhoang.happychatapp.fragments.MoreFragment;
import com.duyhoang.happychatapp.fragments.LatestMessageListFragment;
import com.duyhoang.happychatapp.fragments.ChatRoomFragment;
import com.duyhoang.happychatapp.models.ChattingUser;
import com.firebase.ui.auth.AuthUI;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        ChatRoomFragment.ChatRoomUserSelectedListener{

    public static final String TAG = "HomeActivity";

    private BottomNavigationView bottomNavigationView;
    private FragmentManager mFragmentManager;
    private ActionBar mActionBar;
    private ChattingUser mSelectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mFragmentManager = getSupportFragmentManager();
        initUI();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Fragment currFragment = mFragmentManager.findFragmentById(R.id.frameLayout_container);
        if(currFragment instanceof ChatRoomFragment) {
            mFragmentManager.beginTransaction().remove(currFragment).commit();
            mFragmentManager.beginTransaction().add(R.id.frameLayout_container, new ChatRoomFragment(), "chat_room_frag").commit();
        } else if(currFragment instanceof LatestMessageListFragment){
            mFragmentManager.beginTransaction().remove(currFragment).commit();
            mFragmentManager.beginTransaction().add(R.id.frameLayout_container, new LatestMessageListFragment(), "latest_msg_list_frag").commit();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_message:
                mFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout_container, new LatestMessageListFragment(), "latest_msg_list_frag")
                        .commit();
                return true;

            case R.id.action_my_contact:
                mFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout_container, new ContactFragment(), "contact_frag")
                        .commit();
                return true;

            case R.id.action_chat_room:
                mFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout_container, new ChatRoomFragment(), "chat_room_frag")
                        .commit();
                return true;

            case R.id.action_more:
                mFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout_container, new MoreFragment(), "more_frag")
                        .commit();
                return true;

        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_room_item_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_chatroom_add_friend: addNewFriend(mSelectedUser);
                return true;
            case R.id.menu_item_chatroom_see_profile: seeProfile(mSelectedUser);
                return true;
            case R.id.menu_item_chatroom_cancel: mActionBar.hide();
                return true;
            default: return super.onOptionsItemSelected(item);
        }

    }


    @Override
    public void onShowActionBarOptionsForSelectedUser(ChattingUser selectedUser) {
        mActionBar.show();
        mActionBar.setTitle("Add \"" + selectedUser.getName() + "\" into Contact? ");
        mSelectedUser = selectedUser;
    }

    @Override
    public void onHideActionBarOptions() {
        mActionBar.hide();
        mActionBar.setTitle("");
        mSelectedUser = null;
    }

    private void initUI() {
        mActionBar = getSupportActionBar();
        if(mActionBar != null) getSupportActionBar().hide();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.action_message);
    }


    private void seeProfile(ChattingUser selectedUser) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("selected_user", selectedUser);
        startActivity(intent);
    }

    private void addNewFriend(ChattingUser selectedUser) {
        RealTimeDataBaseUtil.getInstance().addNewFriendToContact(selectedUser.getUid());
        mActionBar.hide();
//        ChatRoomFragment fragment = (ChatRoomFragment) getSupportFragmentManager().findFragmentByTag("chatty_chanel_frag");
//        fragment.eliminateChattingRoomUserAddedSuccessfullyFromChatRoom();

    }

}
