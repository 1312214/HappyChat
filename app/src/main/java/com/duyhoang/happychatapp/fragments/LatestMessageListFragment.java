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
import android.widget.TextView;

import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.Utils.RealTimeDataBaseUtil;
import com.duyhoang.happychatapp.adapters.ChattyChanelListRecycleViewAdapter;

public class LatestMessageListFragment extends Fragment implements RealTimeDataBaseUtil.ChattyChanelListListener{

    private Context mContext;
    private RecyclerView rvChattyChanelList;
    private TextView txtStatus;
    private ChattyChanelListRecycleViewAdapter mChattyChanelListAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RealTimeDataBaseUtil.getInstance().downloadChattyChanel();
        RealTimeDataBaseUtil.getInstance().setChattyChanelListListener(this);
        mChattyChanelListAdapter = new ChattyChanelListRecycleViewAdapter(mContext, RealTimeDataBaseUtil.getInstance().mChattyChanelList);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_lastest_messages, container, false);
        initUI(root);

        rvChattyChanelList.setAdapter(mChattyChanelListAdapter);
        rvChattyChanelList.setLayoutManager(new LinearLayoutManager(mContext));
        return root;
    }


    @Override
    public void onDetach() {
        if(mContext != null) mContext = null;
        RealTimeDataBaseUtil.getInstance().mChattyChanelList = null;
        RealTimeDataBaseUtil.getInstance().removeChildEventListenerForUserChanelId();
        RealTimeDataBaseUtil.getInstance().removeAllValueEventListenerAttachedToLatestMessageNode();
        super.onDetach();
    }


    @Override
    public void onChattyChanelListChanged() {
        mChattyChanelListAdapter.notifyDataSetChanged();
    }


    @Override
    public void onHaveNoChattyChanel(String message) {
        txtStatus.setText(message);
        txtStatus.setVisibility(View.VISIBLE);
        rvChattyChanelList.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onHaveChattyChanel() {
        txtStatus.setVisibility(View.INVISIBLE);
        rvChattyChanelList.setVisibility(View.VISIBLE);
    }

    private void initUI(View root) {
        rvChattyChanelList = root.findViewById(R.id.recyclerView_latest_msg_list);
        txtStatus = root.findViewById(R.id.textView_latestMessage_status);
    }

}