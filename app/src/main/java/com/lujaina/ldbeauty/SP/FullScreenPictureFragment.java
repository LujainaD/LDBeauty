package com.lujaina.ldbeauty.SP;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.GalleryModel;
import com.lujaina.ldbeauty.R;


public class FullScreenPictureFragment extends DialogFragment {
    private FirebaseUser mFirebaseUser;

    private Context mContext;

    private GalleryModel mGallery;
    private int i;

    public FullScreenPictureFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        /*if (context instanceof MediatorInterface) {
            mMediatorInterface = (MediatorInterface) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement MediatorInterface");
        }*/
    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView= inflater.inflate(R.layout.fragment_full_screen_picture, container, false);
        ImageView picture = parentView.findViewById(R.id.img);
        ImageButton menuButton = parentView.findViewById(R.id.ib_menu);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mFirebaseUser= mAuth.getCurrentUser();
        menuButton.setVisibility(View.VISIBLE);

        if(i == 0){
            menuButton.setVisibility(View.GONE);
        }
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(mContext, v);
                popup.getMenuInflater().inflate(R.menu.picture_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.delete:{
                              showDeleteDialog();
                            }
                        }
                        return false;
                    }
                });
                popup.show();
        }
    });

        if(mGallery != null){
            Glide.with(mContext).load(mGallery.getPictureURL()).fitCenter().into(picture);
        }
        return parentView;
    }

    private void showDeleteDialog() {

         AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setMessage("Are you sure you want to delete this picture : ");
        alertDialog.setCancelable(true);

        //to create  function for Yes
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                delete();
                dialog.cancel();
                dismiss();
            }
        });
        // to create function for "No"
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        // to display dialog
        alertDialog.create().show();

    }

    private void delete() {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef;
        myRef = mDatabase.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid()).child(Constants.Salon_Gallery).child(mGallery.getPictureId());
        myRef.removeValue();
    }

    public void setImg(GalleryModel category, int i) {
        mGallery = category;
        this.i = i;
    }


}