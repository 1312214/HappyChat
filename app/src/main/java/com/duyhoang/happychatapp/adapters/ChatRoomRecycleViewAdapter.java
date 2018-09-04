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
import com.duyhoang.happychatapp.models.ChattingUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatRoomRecycleViewAdapter extends RecyclerView.Adapter<ChatRoomRecycleViewAdapter.RoomUserViewHolder>{

    private List<ChattingUser> mListRoomUser;
    private Context mContext;


    public ChatRoomRecycleViewAdapter(Context context, List<ChattingUser> listRoomUser) {
        mContext = context;
        mListRoomUser = listRoomUser;
    }


    @Override
    public int getItemCount() {
        return mListRoomUser.size();
    }

    @NonNull
    @Override
    public RoomUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.layout_chatroom_row_item, parent, false);
        return new RoomUserViewHolder(rootView);
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


}
