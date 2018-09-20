package com.duyhoang.happychatapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.models.ChattingUser;
import com.duyhoang.happychatapp.models.message.ImageMessage;
import com.duyhoang.happychatapp.models.message.Message;
import com.duyhoang.happychatapp.models.message.TextMessage;
import com.duyhoang.happychatapp.utils.RealTimeDataBaseUtil;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ChatChanelRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int CODE_LAYOUT_IMAGE_MESSAGE_HOST = 101;
    private final int CODE_LAYOUT_IMAGE_MESSAGE_GUEST = 102;

    private List<Message> mMessageCatalog;
    private Context mContext;
    private SeeingPhotoListener mSeeingPhotoListener;

    // the guest who is chatting with you.
    private ChattingUser guest;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm - MMM dd, yyyy", Locale.getDefault());


    public ChatChanelRecycleViewAdapter(Context context, List<Message> messageCatalog, ChattingUser guest) {
        mContext = context;
        mMessageCatalog = messageCatalog;
        this.guest = guest;
    }


    @Override
    public int getItemViewType(int position) {
        Message msg = mMessageCatalog.get(position);
        if(msg instanceof TextMessage) {
            if(msg.getSenderId().equals(guest.getUid()) ) // case: this TEXT message belongs to the guest
                return R.layout.layout_text_message_row_item_guest;
            else  // case: this TEXT message belongs to the host (it's you)
                return R.layout.layout_text_message_row_item_host;

        } else { // this is a IMAGE message
            if(msg.getSenderId().equals(guest.getUid()))
                return CODE_LAYOUT_IMAGE_MESSAGE_GUEST;
            else
                return CODE_LAYOUT_IMAGE_MESSAGE_HOST;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View itemView;

        switch (viewType) {
            case R.layout.layout_text_message_row_item_guest:
                itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_text_message_row_item_guest, parent, false);
                viewHolder = new GuestTextMessageViewHolder(itemView);
                break;

            case R.layout.layout_text_message_row_item_host:
                itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_text_message_row_item_host, parent, false);
                viewHolder = new HostTextMessageViewHolder(itemView);
                break;

            case CODE_LAYOUT_IMAGE_MESSAGE_GUEST:
                itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_image_message_row_item, parent, false);
                viewHolder = new ImageMessageViewHolder(itemView);
                break;

            case CODE_LAYOUT_IMAGE_MESSAGE_HOST:

                itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_image_message_row_item, parent, false);
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) itemView.findViewById(R.id.relativeLayout_chatChanel_imageMsg).getLayoutParams();
                params.gravity = Gravity.END;
                itemView.findViewById(R.id.relativeLayout_chatChanel_imageMsg).setLayoutParams(params);
                viewHolder = new ImageMessageViewHolder(itemView);
                break;

                default: viewHolder = new ImageMessageViewHolder(null);
        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final Message msg = mMessageCatalog.get(position);
        if(!msg.isRead() && msg.getSenderId().equals(guest.getUid())){
            RealTimeDataBaseUtil.getInstance().markAsReadForMessage(msg.getMsgId());
        }

        if(holder instanceof GuestTextMessageViewHolder) {

            ((GuestTextMessageViewHolder) holder).txtContent.setText(((TextMessage)msg).getContent());

            RequestOptions requestOptions = new RequestOptions()
                    .override(30)
                    .centerCrop()
                    .placeholder(R.drawable.ic_account_circle_grey_40dp);
            Glide.with(mContext)
                    .load(guest.getPhotoUrl())
                    .apply(requestOptions)
                    .into(((GuestTextMessageViewHolder) holder).imgAvatar);

            ((GuestTextMessageViewHolder) holder).txtContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!((GuestTextMessageViewHolder) holder).visibleTimeFlag) {
                        ((GuestTextMessageViewHolder) holder).txtTime.setVisibility(View.VISIBLE);
                        ((GuestTextMessageViewHolder) holder).visibleTimeFlag = true;
                    } else {
                        ((GuestTextMessageViewHolder) holder).txtTime.setVisibility(View.GONE);
                        ((GuestTextMessageViewHolder) holder).visibleTimeFlag = false;
                    }
                }
            });
            ((GuestTextMessageViewHolder) holder).txtTime.setText(simpleDateFormat.format(msg.getTime()));

        } else if(holder instanceof HostTextMessageViewHolder) {

            ((HostTextMessageViewHolder) holder).txtContent.setText(((TextMessage)msg).getContent());
            ((HostTextMessageViewHolder) holder).txtContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!((HostTextMessageViewHolder) holder).visibleTimeFlag) {
                        ((HostTextMessageViewHolder) holder).txtTime.setVisibility(View.VISIBLE);
                        ((HostTextMessageViewHolder) holder).visibleTimeFlag = true;
                    } else {
                        ((HostTextMessageViewHolder) holder).txtTime.setVisibility(View.GONE);
                        ((HostTextMessageViewHolder) holder).visibleTimeFlag = false;
                    }
                }
            });
            ((HostTextMessageViewHolder) holder).txtTime.setText(simpleDateFormat.format(msg.getTime()));

        } else if(holder instanceof ImageMessageViewHolder) {

            Glide.with(mContext).load(((ImageMessage)msg).getPhotoUrl())
                    .apply(new RequestOptions().transform(new RoundedCorners(20)).placeholder(R.drawable.ic_image_grey_250dp))
                    .into(((ImageMessageViewHolder) holder).imgImage);

            ((ImageMessageViewHolder) holder).imgImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(!((ImageMessageViewHolder) holder).visibleTimeFlag) {
                        ((ImageMessageViewHolder) holder).txtTime.setVisibility(View.VISIBLE);
                        ((ImageMessageViewHolder) holder).visibleTimeFlag = true;
                        return true;
                    } else {
                        ((ImageMessageViewHolder) holder).txtTime.setVisibility(View.GONE);
                        ((ImageMessageViewHolder) holder).visibleTimeFlag = false;
                        return true;
                    }
                }
            });

            ((ImageMessageViewHolder) holder).imgImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mSeeingPhotoListener != null)
                        mSeeingPhotoListener.onShowImagePhoto(((ImageMessage)msg).getPhotoUrl());
                }
            });

            ((ImageMessageViewHolder) holder).txtTime.setText(simpleDateFormat.format(msg.getTime()));
        }

    }


    @Override
    public int getItemCount() {
        if(mMessageCatalog != null)
            return mMessageCatalog.size();
        return 0;
    }


    public void setSeeingPhotoListener(SeeingPhotoListener listener){
        mSeeingPhotoListener = listener;
    }

    class GuestTextMessageViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView txtContent, txtTime;
        boolean visibleTimeFlag;

        public GuestTextMessageViewHolder(View item) {
            super(item);
            imgAvatar = item.findViewById(R.id.imageView_chatChanel_avatar);
            txtContent = item.findViewById(R.id.textView_chatChanel_guest_content);
            txtTime = item.findViewById(R.id.textView_chatChanel_guest_time);
        }
    }


    class HostTextMessageViewHolder extends RecyclerView.ViewHolder {
        TextView txtContent, txtTime;
        boolean visibleTimeFlag;

        public HostTextMessageViewHolder(View item) {
            super(item);
            txtContent = item.findViewById(R.id.textView_chatChanel_host_content);
            txtTime = item.findViewById(R.id.textView_chatChanel_host_time);
        }
    }

    class ImageMessageViewHolder extends RecyclerView.ViewHolder {
        ImageView imgImage;
        TextView txtTime;
        boolean visibleTimeFlag;

        public ImageMessageViewHolder(View item) {
            super(item);
            imgImage = item.findViewById(R.id.imageView_chatChanel_imageMsg_image);
            txtTime = item.findViewById(R.id.textView_chatChanel_imageMsg_time);
        }
    }



    public interface SeeingPhotoListener{
        void onShowImagePhoto(String photoUrl);
    }


}
