package com.lujaina.ldbeauty.Dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Models.GalleryModel;
import com.lujaina.ldbeauty.R;
import com.lujaina.ldbeauty.SP.FullScreenPictureFragment;


public class DeleteDialogFragment extends DialogFragment {

    private GalleryModel mGallery;
    private GalleryModel picture;

    public DeleteDialogFragment() {
        // Required empty public constructor
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
        View parentView = inflater.inflate(R.layout.fragment_delete_dialog, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button delete = parentView.findViewById(R.id.btn_delete);
        Button cancel = parentView.findViewById(R.id.btn_cancel);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser mFirebaseUser = mAuth.getCurrentUser();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                DatabaseReference myRef;
                myRef = mDatabase.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid()).child(Constants.Salon_Gallery).child(mGallery.getPictureId());
                myRef.removeValue();
                dismiss();


            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        dismiss();
    }
});
        return parentView;
    }

}