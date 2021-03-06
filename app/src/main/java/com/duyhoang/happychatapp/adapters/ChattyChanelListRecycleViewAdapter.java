package com.duyhoang.happychatapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.activities.ChatChanelActivity;
import com.duyhoang.happychatapp.models.ChattingUser;
import com.duyhoang.happychatapp.models.ChattyChanel;
import com.duyhoang.happychatapp.models.message.Message;
import com.duyhoang.happychatapp.models.message.TextMessage;


import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
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
        return new ChattyChanelViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final ChattyChanelViewHolder holder, int position) {

        final ChattyChanel chattyChanel = mChattyChanelList.get(position);
        holder.txtName.setText(chattyChanel.getGuestUser().getName());

        RequestOptions requestOptions = new RequestOptions()
                .override(60)
                .placeholder(R.drawable.ic_account_circle_black_60dp)
                .centerCrop();
        Glide.with(mContext)
                .load(chattyChanel.getGuestUser().getPhotoUrl())
                .apply(requestOptions)
                .into(holder.imgAvatar);

        String lastMessage = getShowingTextFromLastMessage(chattyChanel.getLastestMessage(), chattyChanel.getGuestUser().getUid(), chattyChanel.getGuestUser().getName());
        holder.txtLastMessage.setText(lastMessage);
        String lastTime = getLastTimeFromDateOfLastMessage(chattyChanel.getLastestMessage().getTime());
        holder.txtLastTime.setText(lastTime);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!chattyChanel.getLastestMessage().getIsRead()){
                    if(chattyChanel.getLastestMessage().getSenderId().equals(chattyChanel.getGuestUser().getUid())){
                        holder.txtName.setTypeface(Typeface.create(holder.txtName.getTypeface(), Typeface.NORMAL), Typeface.NORMAL);
                        holder.txtLastMessage.setTextColor(ActivityCompat.getColor(mContext, R.color.grey_600));
                        holder.txtLastTime.setTextColor(ActivityCompat.getColor(mContext, R.color.grey_600));
                    }
                }

                ChattingUser guestUser = mChattyChanelList.get(holder.getAdapterPosition()).getGuestUser();
                Intent intent = new Intent(mContext, ChatChanelActivity.class);
                intent.putExtra("selected_contact", guestUser);
                mContext.startActivity(intent);
            }
        });

        if(!chattyChanel.getLastestMessage().getIsRead()){
            if(chattyChanel.getLastestMessage().getSenderId().equals(chattyChanel.getGuestUser().getUid())){
                holder.txtName.setTypeface(holder.txtName.getTypeface(), Typeface.BOLD);
                holder.txtLastMessage.setTextColor(ActivityCompat.getColor(mContext, R.color.black));
                holder.txtLastTime.setTextColor(ActivityCompat.getColor(mContext, R.color.green_A400));
            }
        }

    }

    @Override
    public int getItemCount() {
        if(mChattyChanelList != null)
            return mChattyChanelList.size();
        return 0;
    }


    private String getShowingTextFromLastMessage(Message latestMsg, String guestId, String guestName) {
        String rs;
        if(latestMsg instanceof TextMessage) {
            if(latestMsg.getSenderId().equals(guestId)) {
                rs = ((TextMessage)latestMsg).getContent();
            } else {
                rs = "You: " + ((TextMessage)latestMsg).getContent();
            }
        } else {
            if(latestMsg.getSenderId().equals(guestId)) {
                if(guestName.contains(" "))
                    rs = guestName.substring(0, guestName.indexOf(" ")) + " sent a photo";
                else
                    rs = guestName + " sent a photo";
            } else {
                rs = "You: sent a photo";
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
            return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(lastDate);
            // same week case
        } else if(lastCal.get(Calendar.YEAR) == currCal.get(Calendar.YEAR) && lastCal.get(Calendar.WEEK_OF_YEAR) == currCal.get(Calendar.WEEK_OF_YEAR)) {
            return new SimpleDateFormat("EEE", Locale.getDefault()).format(lastDate);
            // same year, different in month
        } else if(lastCal.get(Calendar.YEAR) == currCal.get(Calendar.YEAR) && lastCal.get(Calendar.MONTH) != currCal.get(Calendar.MONTH)) {
            return new SimpleDateFormat("MMM d", Locale.getDefault()).format(lastDate);
        } else {
            return new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(lastDate);
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
