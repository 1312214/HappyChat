package com.duyhoang.happychatapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.models.ChattingUser;


import java.util.List;

public class ContactRecycleViewAdapter extends RecyclerView.Adapter<ContactRecycleViewAdapter.ContactItemViewHolder> {

    private List<ChattingUser> mContactList;
    private Context mContext;
    private int mSelectedUser;
    private ContactRecycleViewAdapterCallback mContactRecycleViewAdapterCallback;


    public ContactRecycleViewAdapter(Context context, List<ChattingUser> contactList){
        mContext = context;
        mContactList = contactList;
    }

    @Override
    public int getItemCount() {
        return mContactList.size();
    }


    @NonNull
    @Override
    public ContactItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.layout_contact_row_item, parent, false);
        final ContactItemViewHolder viewHolder = new ContactItemViewHolder(rootView);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectedUser = viewHolder.getAdapterPosition();
                if(mContactRecycleViewAdapterCallback != null)
                    mContactRecycleViewAdapterCallback.onContactItemSelected(mContactList.get(mSelectedUser));
            }
        });

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ContactItemViewHolder holder, int position) {
        ChattingUser user = mContactList.get(position);
        holder.txtDisplayName.setText(user.getName());
        RequestOptions requestOptions = new RequestOptions()
                .override(60)
                .placeholder(R.drawable.ic_account_circle_black_60dp)
                .centerCrop();
        Glide.with(mContext)
                .load(user.getPhotoUrl())
                .apply(requestOptions)
                .into(holder.imgAvatar);
    }

    public void setContactRecycleViewAdapterCallback(ContactRecycleViewAdapterCallback callback) {
        mContactRecycleViewAdapterCallback = callback;
    }

    class ContactItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView txtDisplayName;

        public ContactItemViewHolder(View item) {
            super(item);
            imgAvatar = item.findViewById(R.id.imageView_contact_row_item_avatar);
            txtDisplayName = item.findViewById(R.id.textView_contact_row_item_display_name);
        }

    }

    public interface ContactRecycleViewAdapterCallback {
        void onContactItemSelected(ChattingUser selectedContact);
    }

}
