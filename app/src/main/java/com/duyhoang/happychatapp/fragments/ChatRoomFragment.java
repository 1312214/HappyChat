package com.duyhoang.happychatapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.utils.RealTimeDataBaseUtil;
import com.duyhoang.happychatapp.adapters.ChatRoomRecycleViewAdapter;
import com.duyhoang.happychatapp.models.ChattingUser;

public class ChatRoomFragment extends Fragment implements RealTimeDataBaseUtil.ChatRoomUserQuantityChangedListener,
        RealTimeDataBaseUtil.MakingToastListener, ChatRoomRecycleViewAdapter.ChatRoomRecycleViewListener {
    public static final String TAG = "ChatRoomFragment";

    private RecyclerView rvChattingUserList;
    private ChatRoomRecycleViewAdapter mChatRoomAdapter;
    private GridLayoutManager mGridLayoutManager;
    private Context mContext;

    private ChatRoomUserSelectedListener mListener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if(context instanceof ChatRoomUserSelectedListener) {
            mListener = (ChatRoomUserSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString() +
                    "must implement ChatRoomFragment.onShowActionBarOptionsForSelectedUser");
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RealTimeDataBaseUtil.getInstance().setChatRoomUserQuantityChangedListener(this);
        RealTimeDataBaseUtil.getInstance().setMakingToastListener(this);

        RealTimeDataBaseUtil.getInstance().downloadContactUserIdList();
        RealTimeDataBaseUtil.getInstance().downloadChattingUserVisibleListFromRoomChatTable();
        mChatRoomAdapter = new ChatRoomRecycleViewAdapter(getContext(), RealTimeDataBaseUtil.getInstance().mChatRoomUserList);
        mGridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        mChatRoomAdapter.setChatRoomRecycleViewListener(this);
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_chat, container, false);
        rvChattingUserList = view.findViewById(R.id.recyclerView_chatRoom_user_list);
        rvChattingUserList.setAdapter(mChatRoomAdapter);
        rvChattingUserList.setLayoutManager(mGridLayoutManager);
        return view;
    }


    @Override
    public void onDetach() {
        if(mContext != null) mContext = null;
        if(mListener != null) mListener.onHideActionBarOptions();
        RealTimeDataBaseUtil.getInstance().removeMemberNodeChildEventListener();
        super.onDetach();
    }

    @Override
    public void onNewChatUserInsertedAtPosition(int position) {
        mChatRoomAdapter.notifyItemInserted(position);
    }

    @Override
    public void onToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        if(mListener != null) mListener.onRefreshChatRoom();
    }

    @Override
    public void onChatRoomUserSelected(ChattingUser selectedUser) {
        if(mListener != null) mListener.onShowActionBarOptionsForSelectedUser(selectedUser);
    }



    public interface ChatRoomUserSelectedListener {
        void onShowActionBarOptionsForSelectedUser(ChattingUser selectedUser);
        void onHideActionBarOptions();
        void onRefreshChatRoom();
    }
}
