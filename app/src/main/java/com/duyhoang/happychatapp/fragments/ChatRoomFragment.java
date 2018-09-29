package com.duyhoang.happychatapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.activities.HomeActivity;
import com.duyhoang.happychatapp.utils.RealTimeDataBaseUtil;
import com.duyhoang.happychatapp.adapters.ChatRoomRecycleViewAdapter;
import com.duyhoang.happychatapp.models.ChattingUser;

import java.util.ArrayList;

public class ChatRoomFragment extends Fragment implements RealTimeDataBaseUtil.ChatRoomUserQuantityChangedListener,
        RealTimeDataBaseUtil.MakingToastListener, ChatRoomRecycleViewAdapter.ChatRoomRecycleViewListener,
        RealTimeDataBaseUtil.InternetConnectionListener{

    public static final String TAG = "ChatRoomFragment";

    private RecyclerView rvChattingUserList;
    private ChatRoomRecycleViewAdapter mChatRoomAdapter;
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
        RealTimeDataBaseUtil.getInstance().setmInternetConnectionListener(this);
        setRetainInstance(true);
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_chat, container, false);
        initUI(view);

        RealTimeDataBaseUtil.getInstance().mContactIdList = new ArrayList<>();
        RealTimeDataBaseUtil.getInstance().mChatRoomUserList = new ArrayList<>();
        RealTimeDataBaseUtil.getInstance().downloadContactUserIdList();
        mChatRoomAdapter = new ChatRoomRecycleViewAdapter(mContext, RealTimeDataBaseUtil.getInstance().mChatRoomUserList);
        RealTimeDataBaseUtil.getInstance().downloadChattingUserVisibleListFromRoomChatTable();
        mChatRoomAdapter.setChatRoomRecycleViewListener(this);

        rvChattingUserList.setAdapter(mChatRoomAdapter);
        rvChattingUserList.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false));
        return view;
    }


    @Override
    public void onDetach() {
        if(mContext != null) mContext = null;
        rvChattingUserList.setAdapter(null);
        if(mChatRoomAdapter != null) mChatRoomAdapter = null;
        RealTimeDataBaseUtil.getInstance().removeMemberNodeChildEventListener();
        RealTimeDataBaseUtil.getInstance().mContactIdList = null;
        RealTimeDataBaseUtil.getInstance().mChatRoomUserList = null;
        super.onDetach();
    }



    @Override
    public void onNewChatUserInsertedAtPosition(int position) {
        mChatRoomAdapter.notifyItemInserted(position);
    }

    @Override
    public void onToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        refreshChatRoom();
    }



    @Override
    public void onChatRoomUserSelected(ChattingUser selectedUser) {
        if(mListener != null) mListener.onShowActionBarOptionsForSelectedUser(selectedUser);
    }

    @Override
    public void onHaveNoInternetConnection() {
        Snackbar.make(((HomeActivity)mContext).findViewById(android.R.id.content), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RealTimeDataBaseUtil.getInstance().downloadChattingUserVisibleListFromRoomChatTable();
                    }
                }).show();
    }

    public interface ChatRoomUserSelectedListener {
        void onShowActionBarOptionsForSelectedUser(ChattingUser selectedUser);
    }

    private void initUI(View view){
        rvChattingUserList = view.findViewById(R.id.recyclerView_chatRoom_user_list);
    }

    public void refreshChatRoom() {
        int n = RealTimeDataBaseUtil.getInstance().mContactIdList.size();
        for(int i = n-1; i >= 0 ; i--) {
            RealTimeDataBaseUtil.getInstance().mContactIdList.remove(i);
        }
        int m = RealTimeDataBaseUtil.getInstance().mChatRoomUserList.size();
        for(int j = m-1; j >= 0 ; j--) {
            RealTimeDataBaseUtil.getInstance().mChatRoomUserList.remove(j);
        }
        RealTimeDataBaseUtil.getInstance().downloadContactUserIdList();
        RealTimeDataBaseUtil.getInstance().downloadChattingUserVisibleListFromRoomChatTable();
        mChatRoomAdapter.notifyDataSetChanged();
    }
}
