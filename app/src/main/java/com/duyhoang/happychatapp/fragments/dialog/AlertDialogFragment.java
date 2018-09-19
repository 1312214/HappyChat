package com.duyhoang.happychatapp.fragments.dialog;



import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class AlertDialogFragment extends DialogFragment {


    private AlertDialogFragmentListener mListener;



    public AlertDialogFragment() {}


    public static AlertDialogFragment getInstance(String title) {
        AlertDialogFragment dialogFragment = new AlertDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof AlertDialogFragmentListener) {
            mListener = (AlertDialogFragmentListener) context;
        } else {
            throw new ClassCastException(context.toString() +
                    "did not implement AlertDialogFragmentListener.onPositiveButtonClicked");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage("Want to save this profile editing?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(dialogInterface != null)
                            dismiss();

                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(mListener != null) mListener.onPositiveButtonClicked();
                    }
                });

        return builder.create();
    }





    public interface AlertDialogFragmentListener {
        void onPositiveButtonClicked();
    }


}
