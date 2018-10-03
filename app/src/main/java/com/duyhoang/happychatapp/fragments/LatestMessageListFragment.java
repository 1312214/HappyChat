package com.duyhoang.happychatapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.activities.HomeActivity;
import com.duyhoang.happychatapp.utils.RealTimeDataBaseUtil;
import com.duyhoang.happychatapp.adapters.ChattyChanelListRecycleViewAdapter;

import java.util.ArrayList;

public class LatestMessageListFragment extends BaseFragment implements RealTimeDataBaseUtil.ChattyChanelListListener, RealTimeDataBaseUtil.UserChanelNodeOnStoreListener,
        RealTimeDataBaseUtil.InternetConnectionListener, View.OnClickListener {

    public final static String TAG = "LatestMessageListFrag";

    private Context mContext;
    private RecyclerView rvChattyChanelList;
    private TextView txtStatus;
    private Button btnRetry;
    private ChattyChanelListRecycleViewAdapter mChattyChanelListAdapter;

    private LatestMessageListFragListener mLatestMessageListFragListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if(context instanceof LatestMessageListFragListener) {
            mLatestMessageListFragListener = ((LatestMessageListFragListener)context);
        } else {
            throw new ClassCastException("Error: you must implement LatestMessageListFragListener.onReloadDataOfAllFragment");
        }
        Log.e(TAG, "onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RealTimeDataBaseUtil.getInstance().setUserChanelNodeInDatabaseListener(this);
        RealTimeDataBaseUtil.getInstance().setChattyChanelListListener(this);
        RealTimeDataBaseUtil.getInstance().setmInternetConnectionMessageFragListener(this);
        setRetainInstance(true);
        Log.e(TAG, "onCreate");
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
        Log.e(TAG, "onCreateView");
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
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
        Log.e(TAG, "onDetach");
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
        btnRetry.setVisibility(View.INVISIBLE);
        rvChattyChanelList.setVisibility(View.VISIBLE);
    }


    @Override
    public void onAppearChildNode() {
        RealTimeDataBaseUtil.getInstance().removeChildValueEventListenerForUserChanelNode();
        RealTimeDataBaseUtil.getInstance().downloadChattyChanel();
    }


    @Override
    public void onHaveNoInternetConnection() {

        txtStatus.setText("No Internet Connection");
        txtStatus.setVisibility(View.VISIBLE);
        btnRetry.setVisibility(View.VISIBLE);
        rvChattyChanelList.setVisibility(View.INVISIBLE);
        Toast.makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
    }

    private void initUI(View root) {
        rvChattyChanelList = root.findViewById(R.id.recyclerView_latest_msg_list);
        txtStatus = root.findViewById(R.id.textView_latestMessage_status);
        btnRetry = root.findViewById(R.id.button_latestMessage_retry);
        btnRetry.setOnClickListener(this);
    }


    @Override
    public void reloadData() {
        RealTimeDataBaseUtil.getInstance().downloadChattyChanel();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button_latestMessage_retry) {
            if(mLatestMessageListFragListener != null) mLatestMessageListFragListener.onReloadDataOfAllFragment();
        }
    }

    public interface LatestMessageListFragListener {
        void onReloadDataOfAllFragment();
    }
}
