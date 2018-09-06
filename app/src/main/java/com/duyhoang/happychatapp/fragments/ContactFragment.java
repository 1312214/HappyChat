package com.duyhoang.happychatapp.fragments;

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
import com.duyhoang.happychatapp.Utils.CreateTestDataUtil;
import com.duyhoang.happychatapp.adapters.ContactRecycleViewAdapter;
import com.duyhoang.happychatapp.models.ChattingUser;

import java.util.List;

public class ContactFragment extends Fragment {

    private RecyclerView rvContactList;
    private ContactRecycleViewAdapter mChatRoomAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private List<ChattingUser> mContactList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Loading data for mContactList here.
//        mContactList = CreateTestDataUtil.demodata();
        if(mContactList != null)
            mChatRoomAdapter = new ContactRecycleViewAdapter(getContext(), mContactList);
        mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
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
}
