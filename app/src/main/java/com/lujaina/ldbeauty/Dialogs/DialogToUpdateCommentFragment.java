package com.lujaina.ldbeauty.Dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lujaina.ldbeauty.Models.GalleryModel;
import com.lujaina.ldbeauty.R;
import com.lujaina.ldbeauty.User.RatingFragment;


public class DialogToUpdateCommentFragment extends DialogFragment {

    private GalleryModel mGallery;
    private GalleryModel picture;
    private DialogToUpdateCommentFragment.setUpdateDialog mListener;

    public DialogToUpdateCommentFragment(RatingFragment ratingFragment) {
        // Required empty public constructor
        mListener = (setUpdateDialog)ratingFragment;
    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.WRAP_CONTENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
  
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_chose_update_dialog, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button update = parentView.findViewById(R.id.btn_update);
        ImageButton dissmissDialog = parentView.findViewById(R.id.ib_cancel);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser mFirebaseUser = mAuth.getCurrentUser();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(mListener != null){
                    dismiss();
                    mListener.onUpdateClickDialog();
                }

            }
        });


        dissmissDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }


        });
        return parentView;
    }



    public interface setUpdateDialog{
        void onUpdateClickDialog();
    }


}