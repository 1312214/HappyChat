package com.duyhoang.happychatapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.Utils.RealTimeDataBaseUtil;
import com.duyhoang.happychatapp.models.ChattingUser;
import com.google.firebase.auth.FirebaseAuth;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRoomRecycleViewAdapter extends RecyclerView.Adapter<ChatRoomRecycleViewAdapter.RoomUserViewHolder> {

    private List<ChattingUser> mListRoomUser;
    private Context mContext;
    private ChatRoomRecycleViewListener mChatRoomRecycleViewListener;
    private int mSelectedUser;

    public ChatRoomRecycleViewAdapter(Context context, List<ChattingUser> listRoomUser) {
        mContext = context;
        mListRoomUser = listRoomUser;
    }


    @Override
    public int getItemCount() {
        if(mListRoomUser != null)
            return mListRoomUser.size();
        else
            return 0;
    }


    @NonNull
    @Override
    public RoomUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        final View rootView = LayoutInflater.from(mContext).inflate(R.layout.layout_chatroom_row_item, parent, false);
        final RoomUserViewHolder viewHolder = new RoomUserViewHolder(rootView);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectedUser = viewHolder.getAdapterPosition();
                if(mChatRoomRecycleViewListener != null)
                    mChatRoomRecycleViewListener.onChatRoomUserSelected(mListRoomUser.get(mSelectedUser));
            }
        });


        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull RoomUserViewHolder holder, int position) {
        ChattingUser user = mListRoomUser.get(position);
        holder.txtDisplayName.setText(user.getName());

        RequestOptions requestOptions = new RequestOptions()
                .override(60)
                .placeholder(R.drawable.ic_account_circle_grey_60dp)
                .centerCrop();
        Glide.with(mContext)
                .load(user.getPhotoUrl())
                .apply(requestOptions)
                .into(holder.imgAvatar);

        if(RealTimeDataBaseUtil.getInstance().mContactIdList.size() != 0){
            if(RealTimeDataBaseUtil.getInstance().mContactIdList.contains(user.getUid()) || user.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                ((CardView)holder.itemView).setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.blue_grey_100));
                holder.imgAvatar.setCircleBackgroundColor(ContextCompat.getColor(mContext, R.color.grey_200));
                holder.txtDisplayName.setTextColor(ContextCompat.getColor(mContext, R.color.grey_500));
                holder.itemView.setEnabled(false);
            }
        }


    }



    public void setChatRoomRecycleViewListener(ChatRoomRecycleViewListener chatRoomRecycleViewListener) {
        mChatRoomRecycleViewListener = chatRoomRecycleViewListener;
    }


//    public int getSelectedUser() {
//        return mSelectedUser;
//    }
//
//    public void updateRoomUserList() {
//        notifyItemRemoved(mSelectedUser);
//    }


    class RoomUserViewHolder extends RecyclerView.ViewHolder{
        CircleImageView imgAvatar;
        TextView txtDisplayName;

        public RoomUserViewHolder(View item) {
            super(item);
            imgAvatar = item.findViewById(R.id.imageView_chatroom_row_item_avatar);
            txtDisplayName = item.findViewById(R.id.textView_chatroom_row_item_display_name);
        }
    }

    public interface ChatRoomRecycleViewListener {
        void onChatRoomUserSelected(ChattingUser selectedUser);
    }

}
