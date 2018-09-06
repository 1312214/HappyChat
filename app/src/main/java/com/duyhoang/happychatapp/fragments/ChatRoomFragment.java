package com.duyhoang.happychatapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.Utils.CreateTestDataUtil;
import com.duyhoang.happychatapp.Utils.RealTimeDataBaseUtil;
import com.duyhoang.happychatapp.adapters.ChatRoomRecycleViewAdapter;
import com.duyhoang.happychatapp.models.ChattingUser;

import java.util.List;

public class ChatRoomFragment extends Fragment implements RealTimeDataBaseUtil.ChatRoomUserQuantityChangedListener{

    private RecyclerView rvChattingUserList;
    private ChatRoomRecycleViewAdapter mChatRoomAdapter;
    private List<ChattingUser> mChatRoomUserList = null;
    private GridLayoutManager mGridLayoutManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Loading list of room chat user from realtime database into mChattingUserList;
        RealTimeDataBaseUtil.getInstance().downloadChattingUserVisibleListFromRoomChat();

        // Can't load any chatting user.
        mChatRoomAdapter = new ChatRoomRecycleViewAdapter(getContext(), mChatRoomUserList);
        mGridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_chat, container, false);
        rvChattingUserList = view.findViewById(R.id.recycleView_chatRoom_user_list);
        rvChattingUserList.setAdapter(mChatRoomAdapter);
        rvChattingUserList.setLayoutManager(mGridLayoutManager);
        RealTimeDataBaseUtil.getInstance().setChatRoomUserQuantityChangedListener(this);
        mChatRoomUserList = RealTimeDataBaseUtil.getInstance().getChatRoomUserList();
        return view;
    }

    @Override
    public void onNewChatUserInsertedAtPosition(int position) {
        mChatRoomAdapter.notifyItemInserted(position);
    }

}
