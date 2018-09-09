package com.duyhoang.happychatapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

import com.duyhoang.happychatapp.R;
import com.duyhoang.happychatapp.models.ChattingUser;
import com.squareup.picasso.Picasso;

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
        Picasso.get()
                .load(user.getPhotoUrl())
                .placeholder(R.drawable.ic_account_circle_black_60dp)
                .resize(60,60)
                .centerCrop()
                .into(holder.imgAvatar);

    }

    class ContactItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView txtDisplayName;

        public ContactItemViewHolder(View item) {
            super(item);
            imgAvatar = item.findViewById(R.id.imageView_contact_row_item_avatar);
            txtDisplayName = item.findViewById(R.id.textView_contact_row_item_display_name);

            // set click event here.
        }

    }


    public interface ContactRecycleViewAdapterCallback {
        void onContactItemSelected(ChattingUser selectedContact);
    }

    public void setContactRecycleViewAdapterCallback(ContactRecycleViewAdapterCallback callback) {
        mContactRecycleViewAdapterCallback = callback;
    }




}
