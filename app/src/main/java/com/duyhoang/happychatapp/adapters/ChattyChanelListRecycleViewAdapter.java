package com.duyhoang.happychatapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.activities.ChatChanelActivity;
import com.duyhoang.happychatapp.models.ChattingUser;
import com.duyhoang.happychatapp.models.ChattyChanel;
import com.duyhoang.happychatapp.models.Message.ImageMessage;
import com.duyhoang.happychatapp.models.Message.Message;
import com.duyhoang.happychatapp.models.Message.TextMessage;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChattyChanelListRecycleViewAdapter extends RecyclerView.Adapter<ChattyChanelListRecycleViewAdapter.ChattyChanelViewHolder> {


    private Context mContext;
    private List<ChattyChanel> mChattyChanelList;

    public ChattyChanelListRecycleViewAdapter(Context context, List<ChattyChanel> chattyChanelList){
        mContext = context;
        mChattyChanelList = chattyChanelList;
    }


    @NonNull
    @Override
    public ChattyChanelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_message_row_item, parent, false);
        final ChattyChanelViewHolder viewHolder = new ChattyChanelViewHolder(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChattingUser guestUser = mChattyChanelList.get(viewHolder.getAdapterPosition()).getGuestUser();
                Intent intent = new Intent(mContext, ChatChanelActivity.class);
                intent.putExtra("selected_contact", guestUser);
                mContext.startActivity(intent);
            }
        });

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ChattyChanelViewHolder holder, int position) {

        ChattyChanel chattyChanel = mChattyChanelList.get(position);
        holder.txtName.setText(chattyChanel.getGuestUser().getName());
        //loading image by using Picasso lib
        Picasso.get().load(chattyChanel.getGuestUser().getPhotoUrl())
                .placeholder(R.drawable.ic_account_circle_black_60dp)
                .resize(60, 60)
                .centerCrop()
                .into(holder.imgAvatar);
        String lastMessage = getShowingTextFromLastMessage(chattyChanel.getLastestMessage(), chattyChanel.getGuestUser().getUid());
        holder.txtLastMessage.setText(lastMessage);
        String lastTime = getLastTimeFromDateOfLastMessage(chattyChanel.getLastestMessage().getTime());
        holder.txtLastTime.setText(lastTime);
    }

    @Override
    public int getItemCount() {
        if(mChattyChanelList != null)
            return mChattyChanelList.size();
        return 0;
    }


    private String getShowingTextFromLastMessage(Message msg, String guestId) {
        String rs;
        if(msg instanceof TextMessage) {
            if(msg.getSenderId().equals(guestId)) {
                rs = ((TextMessage)msg).getContent();
            } else {
                rs = "You: " + ((TextMessage)msg).getContent();
            }
        } else {
            if(msg.getSenderId().equals(guestId)) {
                rs = ((TextMessage)msg).getContent();
            } else {
                rs = "You: " + ((TextMessage)msg).getContent();
            }
        }

        return rs;
    }


    private String getLastTimeFromDateOfLastMessage(Date lastDate) {
        Date currDate = new Date();
        Calendar lastCal = Calendar.getInstance();
        Calendar currCal = Calendar.getInstance();
        lastCal.setTime(lastDate);
        currCal.setTime(currDate);

        // same day case
        if(lastCal.get(Calendar.YEAR) == currCal.get(Calendar.YEAR) && lastCal.get(Calendar.DAY_OF_YEAR) == currCal.get(Calendar.DAY_OF_YEAR)) {
            return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(currDate);
            // same week case
        } else if(lastCal.get(Calendar.YEAR) == currCal.get(Calendar.YEAR) && lastCal.get(Calendar.WEEK_OF_YEAR) == currCal.get(Calendar.WEEK_OF_YEAR)) {
            return new SimpleDateFormat("EEE", Locale.getDefault()).format(currDate);
            // same year, different in month
        } else if(lastCal.get(Calendar.YEAR) == currCal.get(Calendar.YEAR) && lastCal.get(Calendar.MONTH) != currCal.get(Calendar.MONTH)) {
            return new SimpleDateFormat("MMM d", Locale.getDefault()).format(currDate);
        } else {
            return new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(currDate);
        }
    }

    class ChattyChanelViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView txtName, txtLastMessage, txtLastTime;

        public ChattyChanelViewHolder(View item) {
            super(item);
            imgAvatar = item.findViewById(R.id.imageView_latestMessage_avatar);
            txtName = item.findViewById(R.id.textView_latestMessage_name);
            txtLastMessage = item.findViewById(R.id.textView_last_message);
            txtLastTime = item.findViewById(R.id.textView_latestMessage_last_time);
        }
    }

}
