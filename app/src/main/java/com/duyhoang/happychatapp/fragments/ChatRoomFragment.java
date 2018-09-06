package com.duyhoang.happychatapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.Utils.RealTimeDataBaseUtil;
import com.duyhoang.happychatapp.adapters.ChatRoomRecycleViewAdapter;

public class ChatRoomFragment extends Fragment implements RealTimeDataBaseUtil.ChatRoomUserQuantityChangedListener,
        RealTimeDataBaseUtil.MakingToastListener{

    private RecyclerView rvChattingUserList;
    private ChatRoomRecycleViewAdapter mChatRoomAdapter;
    private GridLayoutManager mGridLayoutManager;
    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RealTimeDataBaseUtil.getInstance().setChatRoomUserQuantityChangedListener(this);
        RealTimeDataBaseUtil.getInstance().setMakingToastListener(this);

        // Loading list of room chat user from realtime database into mChattingUserList;
        RealTimeDataBaseUtil.getInstance().downloadChattingUserVisibleListFromRoomChat();
        mChatRoomAdapter = new ChatRoomRecycleViewAdapter(getContext(), RealTimeDataBaseUtil.getInstance().mChatRoomUserList);
        mGridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_room_chat, container, false);
        rvChattingUserList = view.findViewById(R.id.recycleView_chatRoom_user_list);
        rvChattingUserList.setAdapter(mChatRoomAdapter);
        rvChattingUserList.setLayoutManager(mGridLayoutManager);
        return view;
    }

    @Override
    public void onNewChatUserInsertedAtPosition(int position) {
        mChatRoomAdapter.notifyItemInserted(position);
    }

    @Override
    public void onToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }
}
