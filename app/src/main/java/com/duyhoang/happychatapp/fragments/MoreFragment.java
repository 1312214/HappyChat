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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.Utils.RealTimeDataBaseUtil;
import com.duyhoang.happychatapp.activities.HomeActivity;
import com.duyhoang.happychatapp.activities.LogInActivity;
import com.duyhoang.happychatapp.activities.ProfileActivity;
import com.duyhoang.happychatapp.models.ChattingUser;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MoreFragment extends Fragment implements View.OnClickListener {

    private LinearLayout lnlProfile, lnlLogout, lnlAccountSetting, lnlFeedback, lnlTerms;

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
        View view = inflater.inflate(R.layout.fragment_more, container, false);
        initUI(view);
        return view;
    }

    @Override
    public void onDetach() {
        if(mContext != null) mContext = null;
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linearLayout_myAccount_logout: logout();
                break;
            case R.id.linearLayout_myAccount_profile: openProfile();
                break;

        }
    }

    private void initUI(View view) {
        lnlProfile = view.findViewById(R.id.linearLayout_myAccount_profile);
        lnlAccountSetting = view.findViewById(R.id.linearLayout_myAccount_account_setting);
        lnlLogout = view.findViewById(R.id.linearLayout_myAccount_logout);
        lnlFeedback = view.findViewById(R.id.linearLayout_myAccount_feedback);
        lnlTerms = view.findViewById(R.id.linearLayout_myAccount_terms);

        lnlProfile.setOnClickListener(this);
        lnlAccountSetting.setOnClickListener(this);
        lnlLogout.setOnClickListener(this);
        lnlFeedback.setOnClickListener(this);
        lnlTerms.setOnClickListener(this);
    }


    private void logout() {
        //Perform remove the current user from Chatting room.
        ChattingUser user;
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            user = ChattingUser.valueOf(currentUser);
            RealTimeDataBaseUtil.getInstance().removeUserFromChatRoom(user);

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


    private void openProfile() {
        Intent intent = new Intent(mContext, ProfileActivity.class);
        startActivity(intent);
    }


}
