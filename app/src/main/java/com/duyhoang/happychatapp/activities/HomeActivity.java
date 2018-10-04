package com.duyhoang.happychatapp.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
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
import com.duyhoang.happychatapp.adapters.ViewPagerAdapter;
import com.duyhoang.happychatapp.fragments.BaseFragment;
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
        LatestMessageListFragment.LatestMessageListFragListener, ViewPager.OnPageChangeListener {

    public static final String TAG = "HomeActivity";

    BottomNavigationView bottomNavigationView;
    ViewPager viewPager;

    private ActionBar mActionBar;
    private ChattingUser mSelectedUser;
    private ViewPagerAdapter mViewPagerAdapter;
    private MenuItem mPrevSelectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initUI();
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.action_message);
        }
        setUpViewPagerAdapter();
    }



    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        Log.e(TAG, "onRestoreInstanceState");
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("config_changing", true);
        super.onSaveInstanceState(outState);
//        Log.e(TAG, "onSaveInstanceState");
    }


    @Override
    public void onLoggingOut() {
        mViewPagerAdapter.removeAllFragment();

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
                viewPager.setCurrentItem(0);
                hideActionBarOptionsIfShowing();
                return true;

            case R.id.action_my_contact:
                viewPager.setCurrentItem(1);
                hideActionBarOptionsIfShowing();
                return true;

            case R.id.action_chat_room:
                viewPager.setCurrentItem(2);
                hideActionBarOptionsIfShowing();
                return true;

            case R.id.action_more:
                viewPager.setCurrentItem(3);
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


    @Override
    public void onPositiveButtonClicked() {
        ((MoreFragment)mViewPagerAdapter.getItem(3)).logout();
    }



    @Override
    public void onReloadDataOfAllFragment() {
        BaseFragment msgFrag = (BaseFragment) mViewPagerAdapter.getItem(0);
        msgFrag.reloadData();
        BaseFragment contactFrag = (BaseFragment)mViewPagerAdapter.getItem(1);
        contactFrag.reloadData();

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(mPrevSelectedItem != null) {
            mPrevSelectedItem.setChecked(false);
        } else {
            bottomNavigationView.getMenu().getItem(0).setChecked(false);
        }
        Log.e("page", "onPageSelected: "+position);
        bottomNavigationView.getMenu().getItem(position).setChecked(true);
        mPrevSelectedItem = bottomNavigationView.getMenu().getItem(position);

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void hideActionBarOptionsIfShowing() {
        if (mActionBar.isShowing()) {
            mActionBar.hide();
            mActionBar.setTitle("");
            mSelectedUser = null;
        }
    }

    private void initUI() {
        mActionBar = getSupportActionBar();
        if (mActionBar != null) getSupportActionBar().hide();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        viewPager = findViewById(R.id.viewPager_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        viewPager.addOnPageChangeListener(this);
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

    private void setUpViewPagerAdapter() {
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter.addFragment(new LatestMessageListFragment());
        mViewPagerAdapter.addFragment(new ContactFragment());
        mViewPagerAdapter.addFragment(new ChatRoomFragment());
        mViewPagerAdapter.addFragment(new MoreFragment());
        viewPager.setAdapter(mViewPagerAdapter);
    }

}
