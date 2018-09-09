package com.duyhoang.happychatapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.Utils.RealTimeDataBaseUtil;
import com.duyhoang.happychatapp.activities.ChatChanelActivity;
import com.duyhoang.happychatapp.adapters.ContactRecycleViewAdapter;
import com.duyhoang.happychatapp.models.ChattingUser;

import java.util.List;

public class ContactFragment extends Fragment implements RealTimeDataBaseUtil.ContactListChangedListener,
        ContactRecycleViewAdapter.ContactRecycleViewAdapterCallback{

    private RecyclerView rvContactList;
    private ContactRecycleViewAdapter mChatRoomAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private Context mContext;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RealTimeDataBaseUtil.getInstance().setContactListChangedListener(this);
        RealTimeDataBaseUtil.getInstance().downloadContactListFromContactTable();
        mChatRoomAdapter = new ContactRecycleViewAdapter(getContext(), RealTimeDataBaseUtil.getInstance().mContactList);
        mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mChatRoomAdapter.setContactRecycleViewAdapterCallback(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        rvContactList = view.findViewById(R.id.recycleView_contact_list);
        rvContactList.setAdapter(mChatRoomAdapter);
        rvContactList.setLayoutManager(mLinearLayoutManager);
        return view;
    }


    @Override
    public void onDetach() {
        if(mContext != null) mContext = null;
        super.onDetach();
    }

    @Override
    public void onChangeContactListSize(int position) {
        mChatRoomAdapter.notifyItemInserted(position);
    }

    @Override
    public void onContactItemSelected(ChattingUser selectedContact) {
        Intent intent = new Intent(mContext, ChatChanelActivity.class);
        intent.putExtra("selected_contact", selectedContact);
        startActivity(intent);
    }
}
