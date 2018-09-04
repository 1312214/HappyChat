package com.duyhoang.happychatapp.fragments;

import android.content.Context;
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
import com.duyhoang.happychatapp.adapters.MessageListRecycleViewAdapter;
import com.duyhoang.happychatapp.models.ChattingUser;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {

    private Context mContext;
    private RecyclerView mRecycleViewList;
    private List<ChattingUser> mChattingUserList;
    private LinearLayoutManager mLinearLayoutManager;
    private MessageListRecycleViewAdapter mMessageListRecycleViewAdapter;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mChattingUserList = new ArrayList<>();
        mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        mMessageListRecycleViewAdapter = new MessageListRecycleViewAdapter(getContext(), CreateTestDataUtil.demodata());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contact, container, false);
        mRecycleViewList = root.findViewById(R.id.recycleView_contact_list);
        mRecycleViewList.setAdapter(mMessageListRecycleViewAdapter);
        mRecycleViewList.setLayoutManager(mLinearLayoutManager);
        return root;
    }
}
