package com.duyhoang.happychatapp.Utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.duyhoang.happychatapp.models.ChattingUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RealTimeDataBaseUtil {

    public static final String TAG = "RealTimeDataBaseUtil";

    private DatabaseReference mRefUsers;
    private DatabaseReference mRefChatRoom;
    private List<ChattingUser> mChatRoomUserList;


    private static RealTimeDataBaseUtil realTimeDataBaseUtil;

    private RealTimeDataBaseUtil() {
        mRefUsers = FirebaseDatabase.getInstance().getReference("users");
        mRefChatRoom = FirebaseDatabase.getInstance().getReference("chat_room");
    }

    public static RealTimeDataBaseUtil getInstance() {
        if(realTimeDataBaseUtil == null) {
            realTimeDataBaseUtil = new RealTimeDataBaseUtil();
        }
        return realTimeDataBaseUtil;
    }


    // This method add a new account (user) to User table when the account is registered successfully.
    public void addNewUsertoUsers(final ChattingUser user) {

        mRefUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(user.getUid())) {
                    mRefUsers.child(user.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) Log.i(TAG, "User- " + user.getName() + " was written into database");
                            else Log.e(TAG, "Error: User-" + user.getName() + "failed to be written into database" );
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    // This method makes user visible in the chat room when user logins successfully
    public void addUserToChatRoom(final ChattingUser user) {

        mRefChatRoom.child("members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(user.getUid()))
                    mRefChatRoom.child("members").child(user.getUid()).setValue(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void removeUserFromChatRoom(ChattingUser user) {
        mRefChatRoom.child("members").child(user.getUid()).removeValue();
    }



    public void downloadChattingUserVisibleListFromRoomChat() {

        mChatRoomUserList = null;
        mChatRoomUserList = new ArrayList<ChattingUser>();

        mRefChatRoom.child("members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dss : dataSnapshot.getChildren()) {
                    getChattingUserFromUsers(dss.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    public void getChattingUserFromUsers(String uid) {

        mRefUsers.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            ChattingUser tempUser;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tempUser = dataSnapshot.getValue(ChattingUser.class);
                mChatRoomUserList.add(tempUser);

                // perform notifying to ChatRoomRecycleViewAdapter: notifyItemInserted here.
                if(mChatRoomUserQuantityChangedListener != null)
                    mChatRoomUserQuantityChangedListener.onNewChatUserInsertedAtPosition(mChatRoomUserList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public List<ChattingUser> getChatRoomUserList() {
        return mChatRoomUserList;
    }



    private ChatRoomUserQuantityChangedListener mChatRoomUserQuantityChangedListener;
    public interface ChatRoomUserQuantityChangedListener {
        void onNewChatUserInsertedAtPosition(int position);
    }

    public void setmChatRoomUserQuantityChangedListener(ChatRoomUserQuantityChangedListener listener) {
        mChatRoomUserQuantityChangedListener = listener;
    }




}
