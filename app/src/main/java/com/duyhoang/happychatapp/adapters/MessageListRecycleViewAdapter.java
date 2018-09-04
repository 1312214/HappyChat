package com.duyhoang.happychatapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.models.ChattingUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageListRecycleViewAdapter extends RecyclerView.Adapter<MessageListRecycleViewAdapter.ContactPersonViewHolder> {


    private Context mContext;
    private List<ChattingUser> mChattingUserList;

    public MessageListRecycleViewAdapter(Context context, List<ChattingUser> chattingUserList){
        mContext = context;
        mChattingUserList = chattingUserList;
    }

    @NonNull
    @Override
    public ContactPersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_message_row_item, parent, false);
        return new ContactPersonViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull ContactPersonViewHolder holder, int position) {
        ChattingUser chattingUser = mChattingUserList.get(position);

        holder.txtName.setText(chattingUser.getName());
        holder.txtLastMessage.setText(chattingUser.getLastMessage());
        //loading image by using Picasso lib
        Picasso.get().load(chattingUser.getPhotoUrl())
                .placeholder(R.drawable.ic_account_circle_black_60dp)
                .resize(60, 60)
                .centerCrop()
                .into(holder.imgAvatar);
    }

    @Override
    public int getItemCount() {
        return mChattingUserList.size();
    }

    class ContactPersonViewHolder extends RecyclerView.ViewHolder {

        ImageView imgAvatar;
        TextView txtName, txtLastMessage;

        public ContactPersonViewHolder(View item) {
            super(item);
            imgAvatar = item.findViewById(R.id.imageView_row_item_avatar);
            txtName = item.findViewById(R.id.textView_name);
            txtLastMessage = item.findViewById(R.id.textView_last_message);

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext,"Item clicked: " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}
