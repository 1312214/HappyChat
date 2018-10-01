package com.duyhoang.happychatapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.activities.HomeActivity;
import com.duyhoang.happychatapp.utils.RealTimeDataBaseUtil;
import com.duyhoang.happychatapp.adapters.ChattyChanelListRecycleViewAdapter;

import java.util.ArrayList;

public class LatestMessageListFragment extends Fragment implements RealTimeDataBaseUtil.ChattyChanelListListener, RealTimeDataBaseUtil.UserChanelNodeOnStoreListener,
        RealTimeDataBaseUtil.InternetConnectionListener{

    public final static String TAG = "LatestMessageListFrag";

    private Context mContext;
    private RecyclerView rvChattyChanelList;
    private TextView txtStatus;
    private ChattyChanelListRecycleViewAdapter mChattyChanelListAdapter;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
//        Log.e(TAG, "onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RealTimeDataBaseUtil.getInstance().setUserChanelNodeInDatabaseListener(this);
        RealTimeDataBaseUtil.getInstance().setChattyChanelListListener(this);
        RealTimeDataBaseUtil.getInstance().setmInternetConnectionListener(this);
        setRetainInstance(true);
//        Log.e(TAG, "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_lastest_messages, container, false);
        initUI(root);
        RealTimeDataBaseUtil.getInstance().mChattyChanelList = new ArrayList<>();
        mChattyChanelListAdapter = new ChattyChanelListRecycleViewAdapter(mContext, RealTimeDataBaseUtil.getInstance().mChattyChanelList);
        rvChattyChanelList.setAdapter(mChattyChanelListAdapter);
        rvChattyChanelList.setLayoutManager(new LinearLayoutManager(mContext));
        RealTimeDataBaseUtil.getInstance().downloadChattyChanel();
//        Log.e(TAG, "onCreateView");
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
//        Log.e(TAG, "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
//        Log.e(TAG, "onStop");
    }

    @Override
    public void onDetach() {
        if(mContext != null) mContext = null;
        rvChattyChanelList.setAdapter(null);
        if(mChattyChanelListAdapter != null) mChattyChanelListAdapter = null;
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


    @Override
    public void onAppearChildNode() {
        RealTimeDataBaseUtil.getInstance().removeChildValueEventListenerForUserChanelNode();
        RealTimeDataBaseUtil.getInstance().downloadChattyChanel();
    }


    @Override
    public void onHaveNoInternetConnection() {
        Snackbar.make(((HomeActivity)mContext).findViewById(android.R.id.content), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RealTimeDataBaseUtil.getInstance().downloadChattyChanel();
                    }
                }).show();
        txtStatus.setText("You're offline");
        txtStatus.setVisibility(View.VISIBLE);
        rvChattyChanelList.setVisibility(View.INVISIBLE);

    }

    private void initUI(View root) {
        rvChattyChanelList = root.findViewById(R.id.recyclerView_latest_msg_list);
        txtStatus = root.findViewById(R.id.textView_latestMessage_status);
    }

    /*public void refreshChattyChanelList() {
        int size = RealTimeDataBaseUtil.getInstance().mChattyChanelList.size();
        for(int i = size - 1; i >= 0 ; i--) {
            RealTimeDataBaseUtil.getInstance().mChattyChanelList.remove(i);
        }
        mChattyChanelListAdapter.notifyDataSetChanged();
        RealTimeDataBaseUtil.getInstance().removeChildEventListenerForUserChanelId();
        RealTimeDataBaseUtil.getInstance().removeAllValueEventListenerAttachedToLatestMessageNode();
        RealTimeDataBaseUtil.getInstance().downloadChattyChanel();
    }*/

}
