package com.duyhoang.happychatapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.duyhoang.happychatapp.R;

public class ViewingMessagePhotoDialogFrag extends DialogFragment implements View.OnClickListener {

    private ImageView imgPhoto;
    private ImageButton btnClose;
    private Context mContext;
    private boolean isCloseButtonShowing;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public ViewingMessagePhotoDialogFrag(){}


    public static ViewingMessagePhotoDialogFrag getInstance(String photoUrl){
        ViewingMessagePhotoDialogFrag dialogFrag = new ViewingMessagePhotoDialogFrag();
        Bundle bundle = new Bundle();
        bundle.putString("photo_url", photoUrl);
        dialogFrag.setArguments(bundle);
        return dialogFrag;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_viewing_message_photo, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgPhoto = view.findViewById(R.id.imageView_viewPhotoDialog_photo);
        btnClose = view.findViewById(R.id.imageButton_viewPhotoDialog_close);

        RequestOptions requestOptions = new RequestOptions().fitCenter()
                .placeholder(R.drawable.ic_image_grey_250dp);
        Glide.with(mContext)
                .load(getArguments().getString("photo_url"))
                .apply(requestOptions)
                .into(imgPhoto);

        btnClose.setOnClickListener(this);
        imgPhoto.setOnClickListener(this);

    }


    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);
        super.onResume();

    }

    @Override
    public void onDetach() {
        mContext = null;
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageView_viewPhotoDialog_photo:
                if(!isCloseButtonShowing){
                    btnClose.setVisibility(View.VISIBLE);
                    isCloseButtonShowing = true;
                } else {
                    btnClose.setVisibility(View.INVISIBLE);
                    isCloseButtonShowing = false;
                }
                break;
            case R.id.imageButton_viewPhotoDialog_close:
                dismiss();
                break;
        }
    }
}
