package com.duyhoang.happychatapp.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.Utils.RealTimeDataBaseUtil;
import com.duyhoang.happychatapp.fragments.ContactFragment;
import com.duyhoang.happychatapp.fragments.MyAccountFragment;
import com.duyhoang.happychatapp.fragments.MessageFragment;
import com.duyhoang.happychatapp.fragments.ChatRoomFragment;
import com.duyhoang.happychatapp.models.ChattingUser;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    private AuthUI mAuthUI;
    private BottomNavigationView bottomNavigationView;
    private FragmentManager mFragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        mAuthUI = AuthUI.getInstance();
        mFragmentManager = getSupportFragmentManager();
        initUI();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_message:
                mFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout_container, new MessageFragment())
                        .commit();
                return true;

            case R.id.action_my_contact:
                mFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout_container, new ContactFragment())
                        .commit();
                return true;

            case R.id.action_chat_room:
                mFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout_container, new ChatRoomFragment())
                        .commit();
                return true;

            case R.id.action_my_acount:
                mFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout_container, new MyAccountFragment())
                        .commit();
                return true;

        }
        return false;
    }


    private void initUI() {
        if(getSupportActionBar() != null) getSupportActionBar().hide();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.action_message);
    }


}
