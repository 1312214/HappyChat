package com.duyhoang.happychatapp.activities;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.Utils.RealTimeDataBaseUtil;
import com.duyhoang.happychatapp.fragments.ContactFragment;
import com.duyhoang.happychatapp.fragments.MyAccountFragment;
import com.duyhoang.happychatapp.fragments.MessageFragment;
import com.duyhoang.happychatapp.fragments.ChatRoomFragment;
import com.duyhoang.happychatapp.models.ChattingUser;
import com.firebase.ui.auth.AuthUI;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    private AuthUI mAuthUI;
    private BottomNavigationView bottomNavigationView;
    private FragmentManager mFragmentManager;
    private ActionBar mActionBar;
    private ChattingUser mSelectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuthUI = AuthUI.getInstance();
        mFragmentManager = getSupportFragmentManager();
        initUI();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_message:
                mFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout_container, new MessageFragment(), "messsage_frag")
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

            case R.id.action_my_acount:
                mFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout_container, new MyAccountFragment(), "my_account_frag")
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

    private void initUI() {
        mActionBar = getSupportActionBar();
        if(mActionBar != null) getSupportActionBar().hide();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.action_message);
    }


    public void showActionBarMenuForSelectedUser(ChattingUser selectedUser) {
        mActionBar.show();
        mActionBar.setTitle("Add \"" + selectedUser.getName() + "\" into Contact? ");
        mSelectedUser = selectedUser;
    }

    private void seeProfile(ChattingUser selectedUser) {

    }

    private void addNewFriend(ChattingUser selectedUser) {
        RealTimeDataBaseUtil.getInstance().addNewFriendToContact(selectedUser.getUid());
        mActionBar.hide();
        ChatRoomFragment fragment = (ChatRoomFragment) getSupportFragmentManager().findFragmentByTag("chat_room_frag");
        fragment.eliminateChattingRoomUserAddedSuccessfullyFromChatRoom();

    }

}
