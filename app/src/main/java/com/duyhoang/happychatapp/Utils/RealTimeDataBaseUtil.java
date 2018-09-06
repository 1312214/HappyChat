package com.duyhoang.happychatapp.Utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.duyhoang.happychatapp.models.ChattingUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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
    public List<ChattingUser> mChatRoomUserList;
    private DatabaseReference mRefContacts;


    private static RealTimeDataBaseUtil realTimeDataBaseUtil;

    private RealTimeDataBaseUtil() {
        mRefUsers = FirebaseDatabase.getInstance().getReference("users");
        mRefChatRoom = FirebaseDatabase.getInstance().getReference("chat_room");
        mRefContacts = FirebaseDatabase.getInstance().getReference("contacts");
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

        mRefChatRoom.child("members").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String uidOfUser = dataSnapshot.getKey();
                getnsaveChattingUserFromUsers(uidOfUser);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    public void getnsaveChattingUserFromUsers(String uid) {

        mRefUsers.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            ChattingUser tempUser;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tempUser = dataSnapshot.getValue(ChattingUser.class);
                mChatRoomUserList.add(tempUser);

                // perform notifying to ChatRoomRecycleViewAdapter: notifyItemInserted here.
                if(mChatRoomUserQuantityChangedListener != null)
                    mChatRoomUserQuantityChangedListener.onNewChatUserInsertedAtPosition(mChatRoomUserList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void setChatRoomUserQuantityChangedListener(ChatRoomUserQuantityChangedListener listener) {
        mChatRoomUserQuantityChangedListener = listener;
    }


    public void addNewFriendToContact(final String friendID) {
        final String currUID = FirebaseAuth.getInstance().getUid();
        if(currUID != null)
            mRefContacts.child(currUID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(friendID)) {
                        mRefChatRoom.child(currUID).child(friendID).setValue(true);
                    } else {
                        if(mMakingToastListener != null)
                            mMakingToastListener.onToast("This User already added");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }


    private ChatRoomUserQuantityChangedListener mChatRoomUserQuantityChangedListener;
    public interface ChatRoomUserQuantityChangedListener {
        void onNewChatUserInsertedAtPosition(int position);
    }


    private MakingToastListener mMakingToastListener;
    public interface MakingToastListener {
        void onToast(String message);
    }


    public void setMakingToastListener(MakingToastListener makingToastListener) {
        mMakingToastListener = makingToastListener;
    }


}
