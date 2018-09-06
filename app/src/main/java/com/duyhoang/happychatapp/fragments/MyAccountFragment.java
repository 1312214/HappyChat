package com.duyhoang.happychatapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.Utils.RealTimeDataBaseUtil;
import com.duyhoang.happychatapp.activities.HomeActivity;
import com.duyhoang.happychatapp.activities.LogInActivity;
import com.duyhoang.happychatapp.models.ChattingUser;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MyAccountFragment extends Fragment implements View.OnClickListener {

    private Button btnLogout;

    private FirebaseAuth mAuth;
    private AuthUI mAuthUI;
    private Context mContext;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mAuthUI = AuthUI.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        initUI(view);
        return view;
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_myAccount_log_out: logout();
                break;
        }
    }

    private void initUI(View view) {
        btnLogout = view.findViewById(R.id.button_myAccount_log_out);

        btnLogout.setOnClickListener(this);
    }


    private void logout() {
        //Perform remove the current user from Chatting room.
        ChattingUser user;
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            user = ChattingUser.valueOf(currentUser);
//            RealTimeDataBaseUtil.getInstance().removeUserFromChatRoom(user);

            // Sign out by the authentication.
            mAuthUI.signOut(mContext).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    startActivity(new Intent(mContext, LogInActivity.class));
                    ((HomeActivity)mContext).finish();
                }
            });
        }

    }

}
