package com.duyhoang.happychatapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.activities.HomeActivity;
import com.duyhoang.happychatapp.utils.RealTimeDataBaseUtil;
import com.duyhoang.happychatapp.activities.ChatChanelActivity;
import com.duyhoang.happychatapp.adapters.ContactRecycleViewAdapter;
import com.duyhoang.happychatapp.models.ChattingUser;

import java.util.ArrayList;

public class ContactFragment extends Fragment implements RealTimeDataBaseUtil.ContactListListener,
        ContactRecycleViewAdapter.ContactRecycleViewAdapterCallback, RealTimeDataBaseUtil.InternetConnectionListener{

    private RecyclerView rvContactList;
    private ContactRecycleViewAdapter mChatRoomAdapter;

    private Context mContext;
    private TextView txtStatus;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RealTimeDataBaseUtil.getInstance().setContactListChangedListener(this);
        RealTimeDataBaseUtil.getInstance().setmInternetConnectionListener(this);
        setRetainInstance(true);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        initUI(view);
        RealTimeDataBaseUtil.getInstance().mContactList = new ArrayList<>();
        mChatRoomAdapter = new ContactRecycleViewAdapter(mContext, RealTimeDataBaseUtil.getInstance().mContactList);
        RealTimeDataBaseUtil.getInstance().downloadContactListFromContactTable();
        rvContactList.setAdapter(mChatRoomAdapter);
        rvContactList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mChatRoomAdapter.setContactRecycleViewAdapterCallback(this);
        return view;
    }


    @Override
    public void onDetach() {
        if(mContext != null) mContext = null;
        rvContactList.setAdapter(null);
        if(mChatRoomAdapter != null) mChatRoomAdapter = null;
        RealTimeDataBaseUtil.getInstance().mContactList = null;
        RealTimeDataBaseUtil.getInstance().removeChildEventListenerContactOfCurrAccount();
        super.onDetach();
    }

    private void initUI(View view) {
        rvContactList = view.findViewById(R.id.recyclerView_contact_list);
        txtStatus = view.findViewById(R.id.textView_contact_status);
    }

    @Override
    public void onAddNewContactIntoContactList(int position) {
        mChatRoomAdapter.notifyItemInserted(position);
    }

    @Override
    public void onHaveNoContact() {
        txtStatus.setVisibility(View.VISIBLE);
        rvContactList.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onExistContact() {
        txtStatus.setVisibility(View.INVISIBLE);
        rvContactList.setVisibility(View.VISIBLE);
    }

    @Override
    public void onContactItemSelected(ChattingUser selectedContact) {
        Intent intent = new Intent(mContext, ChatChanelActivity.class);
        intent.putExtra("selected_contact", selectedContact);
        startActivity(intent);
    }


    @Override
    public void onHaveNoInternetConnection() {
        Snackbar.make(((HomeActivity)mContext).findViewById(android.R.id.content), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RealTimeDataBaseUtil.getInstance().downloadContactListFromContactTable();
                    }
                }).show();
        txtStatus.setText("You're offline");
        txtStatus.setVisibility(View.VISIBLE);
        rvContactList.setVisibility(View.INVISIBLE);
    }
}
