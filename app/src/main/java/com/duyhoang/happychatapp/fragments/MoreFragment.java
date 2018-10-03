package com.duyhoang.happychatapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.fragments.dialog.AlertDialogFragment;
import com.duyhoang.happychatapp.utils.RealTimeDataBaseUtil;
import com.duyhoang.happychatapp.activities.HomeActivity;
import com.duyhoang.happychatapp.activities.LogInActivity;
import com.duyhoang.happychatapp.activities.ProfileActivity;
import com.duyhoang.happychatapp.models.ChattingUser;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MoreFragment extends BaseFragment implements View.OnClickListener, AlertDialogFragment.AlertDialogFragmentListener {

    LinearLayout lnlProfile, lnlLogout, lnlAccountSetting, lnlFeedback, lnlTerms;

    private FirebaseAuth mAuth;
    private Context mContext;
    private MoreFragmentListener mMoreFragmentListener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if(context instanceof MoreFragmentListener) {
            mMoreFragmentListener = (MoreFragmentListener)context;
        } else {
            throw new ClassCastException("ClassCastException: you must implement MoreFragmentListener.onLoggingOut");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setRetainInstance(true);
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
            case R.id.linearLayout_myAccount_logout: LogoutAskingForSure();
                break;
            case R.id.linearLayout_myAccount_profile: openProfile();
                break;

        }
    }


    @Override
    public void onPositiveButtonClicked() {
        logout();
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


    public void logout() {

        //Perform remove the current user from Chatting room.
        ChattingUser user;
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            user = ChattingUser.valueOf(currentUser);
            RealTimeDataBaseUtil.getInstance().removeUserFromChatRoom(user);
            if(mMoreFragmentListener != null) mMoreFragmentListener.onLoggingOut();
        }
    }


    private void openProfile() {
        Intent intent = new Intent(mContext, ProfileActivity.class);
        startActivity(intent);
    }


    public void LogoutAskingForSure() {
        String msg = "Do you want to logout?";
        FragmentManager fm = ((HomeActivity)mContext).getSupportFragmentManager();

        AlertDialogFragment dialogFragment = AlertDialogFragment.getInstance(null, msg);
        dialogFragment.show(fm, "logout_dialog");
    }

    public interface MoreFragmentListener {
        void onLoggingOut();
    }

}
