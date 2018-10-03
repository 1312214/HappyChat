package com.duyhoang.happychatapp.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.fragments.dialog.AlertDialogFragment;
import com.duyhoang.happychatapp.utils.ConnectionUtil;
import com.duyhoang.happychatapp.utils.RealTimeDataBaseUtil;
import com.duyhoang.happychatapp.fragments.ContactFragment;
import com.duyhoang.happychatapp.fragments.MoreFragment;
import com.duyhoang.happychatapp.fragments.LatestMessageListFragment;
import com.duyhoang.happychatapp.fragments.ChatRoomFragment;
import com.duyhoang.happychatapp.models.ChattingUser;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        ChatRoomFragment.ChatRoomUserSelectedListener,
        AlertDialogFragment.AlertDialogFragmentListener, MoreFragment.MoreFragmentListener,
        LatestMessageListFragment.LatestMessageListFragListener {

    public static final String TAG = "HomeActivity";

    private BottomNavigationView bottomNavigationView;
    private FragmentManager mFragmentManager;
    private ActionBar mActionBar;
    private ChattingUser mSelectedUser;

    private LatestMessageListFragment mLatestMessageListFragment = new LatestMessageListFragment();
    private ContactFragment mContactFragment = new ContactFragment();
    private ChatRoomFragment mChatRoomFragment = new ChatRoomFragment();
    private MoreFragment mMoreFragment = new MoreFragment();
    private Fragment mActiveFrag = mLatestMessageListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initUI(savedInstanceState);
    }

    private void initFrags() {
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction()
                .add(R.id.frameLayout_container, mLatestMessageListFragment, "latest_msg_list_frag")
                .hide(mLatestMessageListFragment)
                .commit();
        mFragmentManager.beginTransaction()
                .add(R.id.frameLayout_container, mContactFragment, "contact_frag")
                .hide(mContactFragment)
                .commit();
        mFragmentManager.beginTransaction()
                .add(R.id.frameLayout_container, mChatRoomFragment, "chat_room_frag")
                .hide(mChatRoomFragment)
                .commit();
        mFragmentManager.beginTransaction()
                .add(R.id.frameLayout_container, mMoreFragment, "more_frag")
                .hide(mMoreFragment)
                .commit();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.e(TAG, "onRestoreInstanceState");
    }




    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("config_changing", true);
        super.onSaveInstanceState(outState);
        Log.e(TAG, "onSaveInstanceState");
    }


    @Override
    public void onLoggingOut() {
        for (Fragment fragment : mFragmentManager.getFragments()) {
            mFragmentManager.beginTransaction().remove(fragment).commit();
        }
        // Sign out by the authentication.
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent(HomeActivity.this, LogInActivity.class));
                finish();
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_message:
                if (!(mActiveFrag instanceof LatestMessageListFragment)) {
                    mFragmentManager.beginTransaction()
                            .hide(mActiveFrag)
                            .show(mLatestMessageListFragment)
                            .commit();
                    mActiveFrag = mLatestMessageListFragment;
                    hideActionBarOptionsIfShowing();
                } else {
                    mFragmentManager.beginTransaction()
                            .show(mLatestMessageListFragment)
                            .commit();
                }

                return true;

            case R.id.action_my_contact:
                mFragmentManager.beginTransaction()
                        .hide(mActiveFrag)
                        .show(mContactFragment)
                        .commit();
                mActiveFrag = mContactFragment;
                hideActionBarOptionsIfShowing();
                return true;

            case R.id.action_chat_room:
                mFragmentManager.beginTransaction()
                        .hide(mActiveFrag)
                        .show(mChatRoomFragment)
                        .commit();
                mActiveFrag = mChatRoomFragment;
                return true;

            case R.id.action_more:
                mFragmentManager.beginTransaction()
                        .hide(mActiveFrag)
                        .show(mMoreFragment)
                        .commit();
                mActiveFrag = mMoreFragment;
                hideActionBarOptionsIfShowing();
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
            case R.id.menu_item_chatroom_add_friend:
                addNewFriend(mSelectedUser);
                return true;
            case R.id.menu_item_chatroom_see_profile:
                seeProfile(mSelectedUser);
                return true;
            case R.id.menu_item_chatroom_cancel:
                mActionBar.hide();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    @Override
    public void onShowActionBarOptionsForSelectedUser(ChattingUser selectedUser) {
        mActionBar.show();
        mActionBar.setTitle("Add \"" + selectedUser.getName() + "\" into Contact? ");
        mSelectedUser = selectedUser;
    }


    private void hideActionBarOptionsIfShowing() {
        if (mActionBar.isShowing()) {
            mActionBar.hide();
            mActionBar.setTitle("");
            mSelectedUser = null;
        }
    }


    @Override
    public void onPositiveButtonClicked() {
        Fragment frag = mFragmentManager.findFragmentByTag("more_frag");
        if (frag != null && frag.isAdded()) {
            ((MoreFragment) frag).logout();
        }
    }

    private void initUI(Bundle savedInstanceState) {
        mActionBar = getSupportActionBar();
        if (mActionBar != null) getSupportActionBar().hide();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        if (savedInstanceState == null) {
            initFrags();
            bottomNavigationView.setSelectedItemId(R.id.action_message);
        }

    }


    private void seeProfile(ChattingUser selectedUser) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("selected_user", selectedUser);
        startActivity(intent);
    }

    private void addNewFriend(ChattingUser selectedUser) {
        RealTimeDataBaseUtil.getInstance().addNewFriendToContact(selectedUser.getUid());
        mActionBar.hide();
    }

    @Override
    public void onReloadDataOfAllFragment() {
        mLatestMessageListFragment.reloadData();
        mContactFragment.reloadData();
        mChatRoomFragment.reloadData();
    }


}
