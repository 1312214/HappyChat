package com.duyhoang.happychatapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.Utils.RealTimeDataBaseUtil;
import com.duyhoang.happychatapp.models.ChattingUser;
import com.squareup.picasso.Picasso;

import java.util.List;

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

        View rootView = LayoutInflater.from(mContext).inflate(R.layout.layout_chatroom_row_item, parent, false);
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
        Picasso.get().load(user.getPhotoUrl())
                .placeholder(R.drawable.ic_account_circle_black_60dp)
                .resize(60,60)
                .centerCrop()
                .into(holder.imgAvatar);

    }



    class RoomUserViewHolder extends RecyclerView.ViewHolder{

        ImageView imgAvatar;
        TextView txtDisplayName;

        public RoomUserViewHolder(View item) {
            super(item);
            imgAvatar = item.findViewById(R.id.imageView_chatroom_row_item_avatar);
            txtDisplayName = item.findViewById(R.id.textView_chatroom_row_item_display_name);

        }




    }

    public void setChatRoomRecycleViewListener(ChatRoomRecycleViewListener chatRoomRecycleViewListener) {
        mChatRoomRecycleViewListener = chatRoomRecycleViewListener;
    }

    public interface ChatRoomRecycleViewListener {
        void onChatRoomUserSelected(ChattingUser selectedUser);
    }

    public int getSelectedUser() {
        return mSelectedUser;
    }

    public void updateRoomUserList() {
        mListRoomUser.remove(mSelectedUser);
        notifyItemRemoved(mSelectedUser);
    }


}
