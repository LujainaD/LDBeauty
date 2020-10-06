package com.lujaina.ldbeauty.SP;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.GalleryModel;
import com.lujaina.ldbeauty.R;


public class FullScreenPictureFragment extends DialogFragment {
    private MediatorInterface mMediatorInterface;
    private Context mContext;
    private GalleryModel mCategory;

    public FullScreenPictureFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof MediatorInterface) {
            mMediatorInterface = (MediatorInterface) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement MediatorInterface");
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView= inflater.inflate(R.layout.fragment_full_screen_picture, container, false);
        ImageView picture = parentView.findViewById(R.id.img);

        if(mCategory != null){
            Glide.with(mContext).load(mCategory.getPictureURL()).fitCenter().into(picture);
        }
        return parentView;
    }

    public void setImg(GalleryModel category) {
        mCategory = category;
    }
}