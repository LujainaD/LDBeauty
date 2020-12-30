package com.lujaina.ldbeauty.SP.Instruction;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.lujaina.ldbeauty.R;


public class FullScreenInsFragment extends DialogFragment {

    private int i;

    public FullScreenInsFragment() {
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
        View parentView = inflater.inflate(R.layout.fragment_full_screen_ins, container, false);
        ImageView dialogImg = parentView.findViewById(R.id.img);
        ImageButton cancel = parentView.findViewById(R.id.btn_cancel);

        if(i == 1){
            dialogImg.setImageDrawable(getContext().getResources().getDrawable(R.drawable.dialog_info));

        }else if(i==2){
            dialogImg.setImageDrawable(getContext().getResources().getDrawable(R.drawable.swipe_info));

        }else if (i==3){
            dialogImg.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ex_info));

        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return parentView;
    }

    public void showPicture(int i) {
        this.i= i;

    }
}