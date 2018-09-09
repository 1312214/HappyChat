package com.duyhoang.happychatapp.adapters;

import android.animation.LayoutTransition;
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

import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.Utils.RealTimeDataBaseUtil;
import com.duyhoang.happychatapp.models.ChattingUser;
import com.duyhoang.happychatapp.models.Message.ImageMessage;
import com.duyhoang.happychatapp.models.Message.Message;
import com.duyhoang.happychatapp.models.Message.TextMessage;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatChanelRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<Message> mMessageCatalog;
    private Context mContext;

    // the guest who is chatting with you.
    private ChattingUser guest;

    public ChatChanelRecycleViewAdapter(Context context, List<Message> messageCatalog, ChattingUser guest) {
        mContext = context;
        mMessageCatalog = messageCatalog;
        this.guest = guest;
    }


    @Override
    public int getItemViewType(int position) {
        Message msg = mMessageCatalog.get(position);
        if(msg instanceof TextMessage) {
            if(msg.getSenderId().equals(guest.getUid()) ) { // case: this TEXT message belongs to the guest
                return R.layout.layout_text_message_row_item_guest;
            } else { // case: this TEXT message belongs to the host (it's you)
                return R.layout.layout_text_message_row_item_host;
            }

        } else { // this is a IMAGE message
            return R.layout.layout_image_message_row_item;
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
            case R.layout.layout_image_message_row_item:
                itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_image_message_row_item, parent, false);
                viewHolder = new ImageMessageViewHolder(itemView);
                Message msg = mMessageCatalog.get(viewHolder.getAdapterPosition());
                if(!msg.getSenderId().equals(guest.getUid())) {
                    itemView.setBackgroundResource(R.drawable.rounded_light_green_rect);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.END );
                    itemView.setLayoutParams(params);
                }
                break;
            default:
                itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_image_message_row_item, parent, false);
                viewHolder = new ImageMessageViewHolder(itemView);

        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message msg = mMessageCatalog.get(position);
        if(holder instanceof GuestTextMessageViewHolder) {

            ((GuestTextMessageViewHolder) holder).txtContent.setText(((TextMessage)msg).getContent());
            Picasso.get().load(guest.getPhotoUrl())
                    .resize(40, 40)
                    .centerCrop()
                    .placeholder(R.drawable.ic_account_circle_grey_40dp)
                    .into(((GuestTextMessageViewHolder) holder).imgAvatar);

        } else if(holder instanceof HostTextMessageViewHolder) {

            ((HostTextMessageViewHolder) holder).txtContent.setText(((TextMessage)msg).getContent());

        } else if(holder instanceof ImageMessageViewHolder) {

            Picasso.get().load(((ImageMessage)msg).getPhotoUrl())
                    .placeholder(R.drawable.ic_image_grey_250dp)
                    .into(((ImageMessageViewHolder) holder).imgImage);
        }

    }


    @Override
    public int getItemCount() {
        if(mMessageCatalog != null)
            return mMessageCatalog.size();
        return 0;
    }




    public Message getMessageDataAtPostion(int position) {
        return mMessageCatalog.get(position);
    }

    public String getGuestId() {
        return guest.getUid();
    }



    class GuestTextMessageViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView txtContent;

        public GuestTextMessageViewHolder(View item) {
            super(item);

            imgAvatar = item.findViewById(R.id.imageView_chatChanel_avatar);
            txtContent = item.findViewById(R.id.textView_chatChanel_guest_content);
        }
    }


    class HostTextMessageViewHolder extends RecyclerView.ViewHolder {
        TextView txtContent;

        public HostTextMessageViewHolder(View item) {
            super(item);
            txtContent = item.findViewById(R.id.textView_chatChanel_host_content);
        }
    }

    class ImageMessageViewHolder extends RecyclerView.ViewHolder {
        ImageView imgImage;

        public ImageMessageViewHolder(View item) {
            super(item);
            imgImage = item.findViewById(R.id.imageView_chatChanel_image);
        }
    }

}
