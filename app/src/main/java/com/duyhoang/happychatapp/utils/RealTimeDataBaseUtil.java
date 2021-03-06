package com.duyhoang.happychatapp.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.duyhoang.happychatapp.AppConfig;
import com.duyhoang.happychatapp.activities.HomeActivity;
import com.duyhoang.happychatapp.models.ChattingUser;
import com.duyhoang.happychatapp.models.ChattyChanel;
import com.duyhoang.happychatapp.models.message.ImageMessage;
import com.duyhoang.happychatapp.models.message.Message;
import com.duyhoang.happychatapp.models.message.TextMessage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class RealTimeDataBaseUtil {

    private static final String TAG = "RealTimeDataBaseUtil";

    // Database references
    private DatabaseReference mRefUsers;
    private DatabaseReference mRefChatRoom;
    private DatabaseReference mRefContacts;
    private DatabaseReference mRefChanels;
    private DatabaseReference mRefUserChanel;
    private DatabaseReference mRefChanelMessage;
    private DatabaseReference mRefMessages;

    // Data list for recycler view
    public List<ChattingUser> mChatRoomUserList;
    public List<ChattingUser> mContactList;
    public List<Message> mChattyChanelMessageList;
    public List<ChattyChanel> mChattyChanelList;
    public List<String> mContactIdList;

    // Listener
    private ChatRoomUserQuantityChangedListener mChatRoomUserQuantityChangedListener;
    private MakingToastListener mMakingToastListener;
    private ContactListListener mContactListListener;
    private ChattyChanelMessageListListener mChattyChanelMessageListListener;
    private ChattyChanelListListener mChattyChanelListListener;
    private UserChanelNodeOnStoreListener mUserChanelNodeInDatabaseListener;
    private UpdatingCompletionProfileListener mUpdatingCompletionProfileListener;
    private DownloadCurrentUserInfoListener mDownloadCurrentUserInfoListener;
    private CheckingContactExistenceListener mCheckingContactExistenceListener;
    private InternetConnectionListener mInternetConnectionMessageFragListener;
    private InternetConnectionListener mInternetConnectionContactFragListener;
    private InternetConnectionListener mInternetConnectionChatRoomFragListener;

    // Temp variables
    private String currChanelMessageId;

    private Handler mHandler;
    private WeakReference<Context> mContext;

    private ChattyChanelDownloadTask mChattyChanelDownloadTask;


    private static RealTimeDataBaseUtil realTimeDataBaseUtil;

    private RealTimeDataBaseUtil() {
        mContext = new WeakReference<>(AppConfig.getAppContext());
        mHandler = new Handler(Looper.getMainLooper());
        mRefUsers = FirebaseDatabase.getInstance().getReference("users");
        mRefChatRoom = FirebaseDatabase.getInstance().getReference("chat_room");
        mRefContacts = FirebaseDatabase.getInstance().getReference("contacts");
        mRefChanels = FirebaseDatabase.getInstance().getReference("chanels");
        mRefUserChanel = FirebaseDatabase.getInstance().getReference("user_chanel");
        mRefChanelMessage = FirebaseDatabase.getInstance().getReference("chanel_message");
        mRefMessages = FirebaseDatabase.getInstance().getReference("messages");

    }



    public static RealTimeDataBaseUtil getInstance() {
        if (realTimeDataBaseUtil == null) {
            realTimeDataBaseUtil = new RealTimeDataBaseUtil();
        }
        return realTimeDataBaseUtil;
    }

    // This method add a new account (user) to User table when the account is registered successfully.
    public void addNewUsertoUsers(final ChattingUser user) {

        mRefUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(user.getUid())) {
                    mRefUsers.child(user.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                Log.i(TAG, "User- " + user.getName() + " was written into database");
                            else
                                Log.e(TAG, "Error: User-" + user.getName() + "failed to be written into database");
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
                if (!dataSnapshot.hasChild(user.getUid()))
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
        if (ConnectionUtil.isAppOnline(mContext.get())) {
            mRefChatRoom.child("members").addChildEventListener(mMemberNodeChildEventListener);
        } else {
            if (mInternetConnectionChatRoomFragListener != null)
                mInternetConnectionChatRoomFragListener.onHaveNoInternetConnection();
        }
    }


    private ChildEventListener mMemberNodeChildEventListener = new ChildEventListener() {
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
    };


    public void removeMemberNodeChildEventListener() {
        mRefChatRoom.child("members").removeEventListener(mMemberNodeChildEventListener);
    }


    private void getnsaveChattingUserFromUsersTableIntoChatRoomUserList(String uid) {

        mRefUsers.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            ChattingUser tempUser;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tempUser = dataSnapshot.getValue(ChattingUser.class);
                mChatRoomUserList.add(tempUser);

                // perform notifying to ChatRoomRecycleViewAdapter: notifyItemInserted here.
                if (mChatRoomUserQuantityChangedListener != null)
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
        if (currUID != null)
            mRefContacts.child(currUID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(friendID)) {
                        mRefContacts.child(currUID).child(friendID).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (mMakingToastListener != null)
                                    mMakingToastListener.onToast("Added to Contact", friendID);
                            }
                        });
                        if(mContactList.size() == 0) {
                            downloadContactListFromContactTable();
                        }

                    } else {
                        if (mMakingToastListener != null)
                            mMakingToastListener.onToast("This User already added", null);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

    }


    public void setMakingToastListener(MakingToastListener makingToastListener) {
        mMakingToastListener = makingToastListener;
    }

    private ChildEventListener mChildEventListenerContactOfCurrAccount = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            getnsaveChattingUserFromUsersTableIntoContactList(dataSnapshot.getKey());
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


    public void downloadContactListFromContactTable() {

        if (ConnectionUtil.isAppOnline(mContext.get())) {
            final String uid = FirebaseAuth.getInstance().getUid();
            if (uid != null) {
                mRefContacts.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            if (mContactListListener != null) mContactListListener.onExistContact();
                            mRefContacts.child(uid).addChildEventListener(mChildEventListenerContactOfCurrAccount);
                        } else {
                            if (mContactListListener != null)
                                mContactListListener.onHaveNoContact();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

        } else {
            if (mInternetConnectionContactFragListener != null)
                mInternetConnectionContactFragListener.onHaveNoInternetConnection();
        }

    }

    public void removeChildEventListenerContactOfCurrAccount() {
        mRefContacts.child(FirebaseAuth.getInstance().getUid()).removeEventListener(mChildEventListenerContactOfCurrAccount);
    }


    private void getnsaveChattingUserFromUsersTableIntoContactList(String userId) {
        mRefUsers.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ChattingUser user = dataSnapshot.getValue(ChattingUser.class);
                if(mContactList != null) {
                    mContactList.add(user);
                    if (mContactListListener != null) {
                        mContactListListener.onAddNewContactIntoContactList(mContactList.size() - 1);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void setContactListChangedListener(ContactListListener contactListListener) {
        mContactListListener = contactListListener;
    }


    public void redownloadMessageChanelWithSelectedContact(final String selectedUserId) {

        String currUid = FirebaseAuth.getInstance().getUid();
        if (currUid != null)
            mRefUserChanel.child(currUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(selectedUserId)) {
                        attemptLoadingMessageByChanelId(dataSnapshot.child(selectedUserId).getValue().toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }


    public void downloadMessageChanelWithSelectedContact(final String selectedUserId) {

        String currUid = FirebaseAuth.getInstance().getUid();
        if (currUid != null)
            mRefUserChanel.child(currUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(selectedUserId)) {
                        attemptLoadingMessageByChanelId(dataSnapshot.child(selectedUserId).getValue().toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }


    // When creating a new Chanel, you have to do the following jobs:
    // save Message into the Message table
    // creating a new chanel id then save the messageId into the ChanelMessage table
    // save a new chanel object into the Chanels table including: user1, user2, chanel_message_id, latest_message
    // save the chanel Id into the UserChanel table for both the current user and the selected contact user.
    private void createNewChanelAndSaveCurrentMessage(String currUid, String selectedUid, Message message) {

        String chanelMessageId = mRefChanelMessage.push().getKey();
        mRefChanelMessage.child(chanelMessageId).child(message.getMsgId()).setValue(true);

        String chanelId = mRefChanels.push().getKey();
        mRefChanels.child(chanelId).child("user1").setValue(currUid);
        mRefChanels.child(chanelId).child("user2").setValue(selectedUid);
        mRefChanels.child(chanelId).child("chanel_message_id").setValue(chanelMessageId);
        mRefChanels.child(chanelId).child("latest_message").setValue(message.getMsgId());

        message.setChanelId(chanelId);
        mRefMessages.child(message.getMsgId()).setValue(message);

        mRefUserChanel.child(currUid).child(selectedUid).setValue(chanelId);
        mRefUserChanel.child(selectedUid).child(currUid).setValue(chanelId);

        RealTimeDataBaseUtil.getInstance().redownloadMessageChanelWithSelectedContact(selectedUid);
    }


    public void removeChildEventListenerOnCurrentChanelMessageId() {
        if (currChanelMessageId != null)
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
                if (dataSnapshot.exists()) {
                    currChanelMessageId = dataSnapshot.getValue().toString();
                    if (currChanelMessageId != null)
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
                if (dataSnapshot.exists()) {
                    String v = null;
                    try {
                        v = dataSnapshot.child("type").getValue().toString();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    if (v.equals("TEXT")) {
                        tempMsg = dataSnapshot.getValue(TextMessage.class);
                        mChattyChanelMessageList.add(tempMsg);
                        if (mChattyChanelMessageListListener != null)
                            mChattyChanelMessageListListener.onNewMessageInserted(mChattyChanelMessageList.size() - 1);
                    } else {
                        tempMsg = dataSnapshot.getValue(ImageMessage.class);
                        mChattyChanelMessageList.add(tempMsg);
                        if (mChattyChanelMessageListListener != null)
                            mChattyChanelMessageListListener.onNewMessageInserted(mChattyChanelMessageList.size() - 1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void setChattyChanelMessageListListener(ChattyChanelMessageListListener listener) {
        mChattyChanelMessageListListener = listener;
    }


    public void uploadMessageToFirebaseDatabase(final Message message, final String receiverUserId) {
        // upload new message to the messages table
        // mark id of new message into the chanel_message table.
        String currUid = FirebaseAuth.getInstance().getUid();
        if (currUid != null)
            mRefUserChanel.child(currUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String messageId = mRefMessages.push().getKey();
                    message.setMsgId(messageId);
                    if (dataSnapshot.hasChild(receiverUserId)) {
                        String chanelId = dataSnapshot.child(receiverUserId).getValue().toString();
                        getnsaveChanelMessageIdByChanelIdThenSaveMessageIdIntoChanelMsgTable(chanelId, messageId);
                        message.setChanelId(chanelId);
                        mRefMessages.child(messageId).setValue(message);
                        mRefChanels.child(chanelId).child("latest_message").setValue(messageId);
                    } else {
                        createNewChanelAndSaveCurrentMessage(FirebaseAuth.getInstance().getUid(), receiverUserId, message);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }


    private void getnsaveChanelMessageIdByChanelIdThenSaveMessageIdIntoChanelMsgTable(String chanelId, final String messageId) {
        mRefChanels.child(chanelId).child("chanel_message_id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String chanelMessgeId = dataSnapshot.getValue().toString();
                    mRefChanelMessage.child(chanelMessgeId).child(messageId).setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void downloadChattyChanel() {

        if (ConnectionUtil.isAppOnline(mContext.get())) {
            String currUid = FirebaseAuth.getInstance().getUid();
            if (currUid != null) {
                mRefUserChanel.child(currUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (mChattyChanelListListener != null)
                                mChattyChanelListListener.onHaveChattyChanel();
                            mRefUserChanel.child(FirebaseAuth.getInstance().getUid()).addChildEventListener(mChildEventListenerForUserChanelId);
                        } else {
                            if (mChattyChanelListListener != null)
                                mChattyChanelListListener.onHaveNoChattyChanel("You have no message");
                            mRefUserChanel.child("xxxxxxxxsxxxss").child("00xx00").setValue("00ss00");
                            mRefUserChanel.addChildEventListener(mChildValueEventListenerForUserChanelNode);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

        } else {
            if (mInternetConnectionMessageFragListener != null)
                mInternetConnectionMessageFragListener.onHaveNoInternetConnection();
        }

    }


    private boolean justOnceFlag;
    private ChildEventListener mChildValueEventListenerForUserChanelNode = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            if (!dataSnapshot.getKey().equals("xxxxxxxxsxxxss") && !justOnceFlag) {
                justOnceFlag = true;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mUserChanelNodeInDatabaseListener != null)
                            mUserChanelNodeInDatabaseListener.onAppearChildNode();
                    }
                });

            }

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


    public void removeChildValueEventListenerForUserChanelNode() {
        mRefUserChanel.removeEventListener(mChildValueEventListenerForUserChanelNode);
    }


    private ChildEventListener mChildEventListenerForUserChanelId = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            mChattyChanelDownloadTask = new ChattyChanelDownloadTask(dataSnapshot);
            mChattyChanelDownloadTask.performDownloading();
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


    public void removeChildEventListenerForUserChanelId() {
        String currUid = FirebaseAuth.getInstance().getUid();
        if (mChildEventListenerForUserChanelId != null)
            mRefUserChanel.child(currUid).removeEventListener(mChildEventListenerForUserChanelId);
    }


    public void removeAllValueEventListenerAttachedToLatestMessageNode() {
        String currUid = FirebaseAuth.getInstance().getUid();
        mRefUserChanel.child(currUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    if (mChattyChanelDownloadTask != null) {
                        for (DataSnapshot dss : dataSnapshot.getChildren()) {
                            String removedChanelId = dss.getValue().toString();
                            mRefChanels.child(removedChanelId).child("latest_message").removeEventListener(mChattyChanelDownloadTask.getLatestMessageListener());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void setChattyChanelListListener(ChattyChanelListListener listener) {
        mChattyChanelListListener = listener;
    }


    public void downloadCurrentUser() {
        mRefUsers.child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ChattingUser currUser = dataSnapshot.getValue(ChattingUser.class);
                    if (mDownloadCurrentUserInfoListener != null)
                        mDownloadCurrentUserInfoListener.onFinishDownloadingCurrentUser(currUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void downloadContactUserIdList() {

        mRefContacts.child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot dss : dataSnapshot.getChildren()) {
                        mContactIdList.add(dss.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void checkSelectedContactAlreadyAdded(String selectedContactId) {

        mRefContacts.child(FirebaseAuth.getInstance().getUid()).child(selectedContactId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (mCheckingContactExistenceListener != null)
                        mCheckingContactExistenceListener.onCompleteCheckingContactExistence(true);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void updateProfile(final ChattingUser updatedUser) {
        mRefUsers.child(updatedUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mRefUsers.child(updatedUser.getUid()).setValue(updatedUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (mUpdatingCompletionProfileListener != null)
                                mUpdatingCompletionProfileListener.onCompleteUpdatingUser();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void setCheckingContactExistenceListener(CheckingContactExistenceListener listener) {
        mCheckingContactExistenceListener = listener;
    }


    public void setUpdatingCompletionListener(UpdatingCompletionProfileListener listener) {
        mUpdatingCompletionProfileListener = listener;
    }


    public void setDownloadCurrentUserInfoListener(DownloadCurrentUserInfoListener listener) {
        mDownloadCurrentUserInfoListener = listener;
    }


    public void markAsReadForMessage(String messageId) {
        mRefMessages.child(messageId).child("isRead").setValue(true);
    }


    public class ChattyChanelDownloadTask implements Runnable {

        private DataSnapshot dataSnapshot;
        private ChattyChanel tempChattyChanel;
        private ValueEventListener latestMessageListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    attemptLoadingLatestMessageById(dataSnapshot.getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        public ValueEventListener getLatestMessageListener() {
            return latestMessageListener;
        }

        public ChattyChanelDownloadTask(DataSnapshot dataSnapshot) {
            this.dataSnapshot = dataSnapshot;
        }

        public void performDownloading() {
            new Thread(this).start();
        }


        @Override
        public void run() {
            tempChattyChanel = new ChattyChanel();
            getnsaveGuestUserFromUsersTableById(dataSnapshot.getKey());
            getnsaveLatestMessageByChanelId(dataSnapshot.getValue().toString());

        }

        private void getnsaveGuestUserFromUsersTableById(String guestId) {
            mRefUsers.child(guestId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        ChattingUser user = dataSnapshot.getValue(ChattingUser.class);
                        tempChattyChanel.setGuestUser(user);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        private void getnsaveLatestMessageByChanelId(String chanelId) {
            mRefChanels.child(chanelId).child("latest_message").addValueEventListener(latestMessageListener);
        }


        private void attemptLoadingLatestMessageById(String latestMsgId) {
            mRefMessages.child(latestMsgId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Message tempMsg;
                    if (dataSnapshot.exists()) {
                        String v = null;
                        try {
                            v = dataSnapshot.child("type").getValue().toString();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        if (v.equals("TEXT")) {
                            tempMsg = dataSnapshot.getValue(TextMessage.class);
                            if (tempChattyChanel == null) {
                                // in case, just latest_message is changed
                                updateLatestMessageChangedForChattyChanelListByChanelId(tempMsg);
                            } else {
                                tempChattyChanel.setLastestMessage(tempMsg);
                                tempChattyChanel.setChanelId(tempMsg.getChanelId());
                                ifTempChattyChanelFullFieldsThenAdding(tempChattyChanel);
                            }

                        } else {
                            tempMsg = dataSnapshot.getValue(ImageMessage.class);
                            if (tempChattyChanel == null) {
                                // in case, just latest_message is changed
                                updateLatestMessageChangedForChattyChanelListByChanelId(tempMsg);
                            } else {
                                tempChattyChanel.setLastestMessage(tempMsg);
                                tempChattyChanel.setChanelId(tempMsg.getChanelId());
                                ifTempChattyChanelFullFieldsThenAdding(tempChattyChanel);
                            }

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        private void updateLatestMessageChangedForChattyChanelListByChanelId(Message msg) {

            for (int i = 0; i < mChattyChanelList.size(); i++) {
                if (mChattyChanelList.get(i).getChanelId().equals(msg.getChanelId())) {
                    mChattyChanelList.get(i).setLastestMessage(msg);
                    sortChattyChanelListInDescendingOrderByLatestMessageDate();
                    break;
                }
            }

        }


        private void ifTempChattyChanelFullFieldsThenAdding(ChattyChanel chattyChanel) {
            if (chattyChanel.getGuestUser() != null && chattyChanel.getLastestMessage() != null) {
                mChattyChanelList.add(tempChattyChanel);
                tempChattyChanel = null;
                sortChattyChanelListInDescendingOrderByLatestMessageDate();
            }
        }


        private synchronized void sortChattyChanelListInDescendingOrderByLatestMessageDate() {
            // Sorting ChattyChanelList in descending order of date
            Collections.sort(mChattyChanelList, new Comparator<ChattyChanel>() {
                @Override
                public int compare(ChattyChanel t1, ChattyChanel t2) {
                    Date d1 = t1.getLastestMessage().getTime();
                    Date d2 = t2.getLastestMessage().getTime();
                    return (d1.compareTo(d2)) * (-1);
                }
            });

            if (mChattyChanelListListener != null)
                mChattyChanelListListener.onChattyChanelListChanged();
        }

    }


    public void setmInternetConnectionMessageFragListener(InternetConnectionListener listener) {
        mInternetConnectionMessageFragListener = listener;
    }

    public void setmInternetConnectionContactFragListener(InternetConnectionListener listener) {
        mInternetConnectionContactFragListener = listener;
    }

    public void setmInternetConnectionChatRoomFragListener(InternetConnectionListener listener) {
        mInternetConnectionChatRoomFragListener = listener;
    }

    public void setUserChanelNodeInDatabaseListener(UserChanelNodeOnStoreListener listener) {
        mUserChanelNodeInDatabaseListener = listener;
    }


    public interface InternetConnectionListener {
        void onHaveNoInternetConnection();
    }

    public interface CheckingContactExistenceListener {
        void onCompleteCheckingContactExistence(boolean isExistent);
    }

    public interface UpdatingCompletionProfileListener {
        void onCompleteUpdatingUser();
    }

    public interface UserChanelNodeOnStoreListener {
        void onAppearChildNode();
    }

    public interface DownloadCurrentUserInfoListener {
        void onFinishDownloadingCurrentUser(ChattingUser user);
    }

    public interface ChattyChanelMessageListListener {
        void onNewMessageInserted(int postion);
    }

    public interface ContactListListener {
        void onAddNewContactIntoContactList(int position);

        void onHaveNoContact();

        void onExistContact();
    }

    public interface ChatRoomUserQuantityChangedListener {
        void onNewChatUserInsertedAtPosition(int position);
    }

    public interface MakingToastListener {
        void onToast(String message, String addedFriendId);
    }

    public interface ChattyChanelListListener {
        void onChattyChanelListChanged();

        void onHaveNoChattyChanel(String message);

        void onHaveChattyChanel();
    }


}
