package com.duyhoang.happychatapp.Utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.duyhoang.happychatapp.models.ChattingUser;
import com.duyhoang.happychatapp.models.Message.ImageMessage;
import com.duyhoang.happychatapp.models.Message.Message;
import com.duyhoang.happychatapp.models.Message.TextMessage;
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
    private DatabaseReference mRefContacts;
    private DatabaseReference mRefChanels;
    private DatabaseReference mRefUserChanel;
    private DatabaseReference mRefChanelMessage;
    private DatabaseReference mRefMessages;

    public List<ChattingUser> mChatRoomUserList;
    public List<ChattingUser> mContactList;
    public List<Message> mChattyChanelMessageList;

    public String chanelId;
    public String chanelMessageId;



    private ChatRoomUserQuantityChangedListener mChatRoomUserQuantityChangedListener;
    private MakingToastListener mMakingToastListener;
    private ContactListChangedListener mContactListChangedListener;
    private ChattyChanelMessageListListener mChattyChanelMessageListListener;

    private static RealTimeDataBaseUtil realTimeDataBaseUtil;

    private RealTimeDataBaseUtil() {
        mRefUsers = FirebaseDatabase.getInstance().getReference("users");
        mRefChatRoom = FirebaseDatabase.getInstance().getReference("chat_room");
        mRefContacts = FirebaseDatabase.getInstance().getReference("contacts");
        mRefChanels = FirebaseDatabase.getInstance().getReference("chanels");
        mRefUserChanel = FirebaseDatabase.getInstance().getReference("user_chanel");
        mRefChanelMessage = FirebaseDatabase.getInstance().getReference("chanel_message");
        mRefMessages = FirebaseDatabase.getInstance().getReference("messages");

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



    public void downloadChattingUserVisibleListFromRoomChatTable() {

        mChatRoomUserList = null;
        mChatRoomUserList = new ArrayList<ChattingUser>();

        mRefChatRoom.child("members").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String uidOfUser = dataSnapshot.getKey();
                getnsaveChattingUserFromUsersTableIntoChatRoomUserList(uidOfUser);
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


    private void getnsaveChattingUserFromUsersTableIntoChatRoomUserList(String uid) {

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
                    if(!dataSnapshot.hasChild(friendID)) {
                        mRefContacts.child(currUID).child(friendID).setValue(true);
                        if(mMakingToastListener != null)
                            mMakingToastListener.onToast("Added successfully");
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



    public interface ChatRoomUserQuantityChangedListener {
        void onNewChatUserInsertedAtPosition(int position);
    }



    public interface MakingToastListener {
        void onToast(String message);
    }


    public void setMakingToastListener(MakingToastListener makingToastListener) {
        mMakingToastListener = makingToastListener;
    }


    public void downloadContactListFromContactTable() {
        mContactList = null;
        mContactList = new ArrayList<>();
        String uid = FirebaseAuth.getInstance().getUid();
        if(uid != null) {
            mRefContacts.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChildren()) {
                        for(DataSnapshot dss : dataSnapshot.getChildren()) {
                            getnsaveChattingUserFromUsersTableIntoContactList(dss.getKey());
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }


    private void getnsaveChattingUserFromUsersTableIntoContactList(String userId) {
        mRefUsers.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ChattingUser user = dataSnapshot.getValue(ChattingUser.class);
                mContactList.add(user);
                if(mContactListChangedListener != null) {
                    mContactListChangedListener.onChangeContactListSize(mContactList.size() -1);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setContactListChangedListener(ContactListChangedListener contactListChangedListener) {
        mContactListChangedListener = contactListChangedListener;
    }
    public interface ContactListChangedListener {
        void onChangeContactListSize(int position);
    }


    public void downloadMessageChanelWithSelectedContact(final String selectedUserId) {

        mChattyChanelMessageList = null;
        mChattyChanelMessageList = new ArrayList<>();

        final String currUid = FirebaseAuth.getInstance().getUid();
        if(currUid != null)
        mRefUserChanel.child(currUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(selectedUserId)) {
                    attemptLoadingMessageByChanelId(dataSnapshot.child(selectedUserId).getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createNewChanel(String currUid, String selectedUid, String messageId) {

        String chanelMessageId = mRefChanelMessage.push().getKey();
        mRefChanelMessage.child(chanelMessageId).child(messageId).setValue(true);

        String chanelId = mRefChanels.push().getKey();
        mRefChanels.child(chanelId).child("user1").setValue(currUid);
        mRefChanels.child(chanelId).child("user2").setValue(selectedUid);
        mRefChanels.child(chanelId).child("chanel_message_id").setValue(chanelMessageId);

        mRefUserChanel.child(currUid).child(selectedUid).setValue(chanelId);
        mRefUserChanel.child(selectedUid).child(currUid).setValue(chanelId);
    }



    private String currChanelMessageId;

    public void removeChildEventListenerOnCurrentChanelMessageId() {
        mRefChanelMessage.child(currChanelMessageId).removeEventListener(childEventListenerAtCurrentChanelMessageId);
    }

    private ChildEventListener childEventListenerAtCurrentChanelMessageId = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            attemptLoadingMessageById(dataSnapshot.getKey());
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
    };

    private void attemptLoadingMessageByChanelId(String chanelId) {

        mRefChanels.child(chanelId).child("chanel_message_id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    currChanelMessageId = dataSnapshot.getValue().toString();
                    if(currChanelMessageId != null)
                        mRefChanelMessage.child(currChanelMessageId).addChildEventListener(childEventListenerAtCurrentChanelMessageId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }



    private void attemptLoadingMessageById(String messageId) {
        mRefMessages.child(messageId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Message tempMsg;
                if(dataSnapshot.exists()) {
                    String v = null;
                    try {
                        v = dataSnapshot.child("type").getValue().toString();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    if(v.equals("TEXT")) {
                        tempMsg = dataSnapshot.getValue(TextMessage.class);
                        mChattyChanelMessageList.add(tempMsg);
                        if(mChattyChanelMessageListListener != null) mChattyChanelMessageListListener.onNewMessageInserted(mChattyChanelMessageList.size() - 1);
                    } else {
                        tempMsg = dataSnapshot.getValue(ImageMessage.class);
                        mChattyChanelMessageList.add(tempMsg);
                        if(mChattyChanelMessageListListener != null) mChattyChanelMessageListListener.onNewMessageInserted(mChattyChanelMessageList.size() - 1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public interface ChattyChanelMessageListListener {
        void onNewMessageInserted(int postion);
    }

    public void setChattyChanelMessageListListener(ChattyChanelMessageListListener listener) {
        mChattyChanelMessageListListener = listener;
    }


    public void uploadTextMessageToFirebaseDatabase(final TextMessage message, final String selectedUid) {
        // upload new message to the messages table
        final String messageId = mRefMessages.push().getKey();
        mRefMessages.child(messageId).setValue(message);

        // mark id of new message into the chanel_message talble.
        final String currUid = FirebaseAuth.getInstance().getUid();
        if(currUid != null)
        mRefUserChanel.child(currUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(selectedUid)) {
                    String chanelId = dataSnapshot.child(selectedUid).getValue().toString();
                    getChanelMessageIdByChanelId(chanelId, messageId);
                } else {
                    createNewChanel(currUid, selectedUid, messageId);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getChanelMessageIdByChanelId(String chanelId, final String messageId) {
        mRefChanels.child(chanelId).child("chanel_message_id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String chanelMessgeId = dataSnapshot.getValue().toString();
                    mRefChanelMessage.child(chanelMessgeId).child(messageId).setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }




}
