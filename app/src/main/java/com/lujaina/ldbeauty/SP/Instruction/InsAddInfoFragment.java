package com.lujaina.ldbeauty.SP.Instruction;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.lujaina.ldbeauty.Dialogs.AddCategoriesDialogFragment;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.R;
import com.lujaina.ldbeauty.SP.FullScreenPictureFragment;

public class InsAddInfoFragment extends DialogFragment {

    public InsAddInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_ins_add_info, container, false);
        ImageButton cancel = parentView.findViewById(R.id.btn_cancel);
        ImageView dialogImg = parentView.findViewById(R.id.img);
        ImageView swipeImg = parentView.findViewById(R.id.swipe_ins);
        ImageView exampleImg = parentView.findViewById(R.id.imageView31);

        dialogImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FullScreenInsFragment dialogFragment = new FullScreenInsFragment();
                dialogFragment.showPicture(1);
                dialogFragment.show(getChildFragmentManager(), FullScreenInsFragment.class.getSimpleName());
            }

        });

        swipeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FullScreenInsFragment dialogFragment = new FullScreenInsFragment();
                dialogFragment.showPicture(2);
                dialogFragment.show(getChildFragmentManager(), FullScreenInsFragment.class.getSimpleName());
            }

        });

        exampleImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FullScreenInsFragment dialogFragment = new FullScreenInsFragment();
                dialogFragment.showPicture(3);
                dialogFragment.show(getChildFragmentManager(), FullScreenInsFragment.class.getSimpleName());
            }

        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return parentView;
    }
}