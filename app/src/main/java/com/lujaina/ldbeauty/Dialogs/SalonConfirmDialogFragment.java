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
import android.widget.TextView;

import com.lujaina.ldbeauty.AppOwner.AOConfirmSalonsFragment;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;

public class SalonConfirmDialogFragment extends DialogFragment {

    private SPRegistrationModel mDetails;
    private SalonConfirmDialogFragment.statusConfirmed mListener;
    private SPRegistrationModel sprModelObj;


    public SalonConfirmDialogFragment(AOConfirmSalonsFragment aoConfirmContext, SPRegistrationModel salonsDetails) {
        mListener = (statusConfirmed) aoConfirmContext;
		sprModelObj = salonsDetails;
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
        View parentView = inflater.inflate(R.layout.fragment_salon_confirm_dialog, container, false);

        TextView ownerName = parentView.findViewById(R.id.tv_ownerName);
        TextView ownerPhone = parentView.findViewById(R.id.tv_ownerPhoner);
        TextView salonName = parentView.findViewById(R.id.tv_salonName);
        TextView salonLocation = parentView.findViewById(R.id.tv_salonLocat);
        TextView date = parentView.findViewById(R.id.tv_regisDate);
        final Button confirm = parentView.findViewById(R.id.btn_confirm);
        Button cancel = parentView.findViewById(R.id.btn_cancel);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        if (mDetails != null) {
            ownerName.setText(mDetails.getUserName());
            ownerPhone.setText(mDetails.getPhoneNumber());
            salonName.setText(mDetails.getSalonName());
            salonLocation.setText(mDetails.getSalonCity());
            date.setText(mDetails.getRegistrationDate());
        }


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               String word = "Confirm";
                sprModelObj.setStatusType(word);
              dismiss();
                if(mListener != null){
                    mListener.onStatusSelected(1,sprModelObj);
                }


            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = "Cancel";
                sprModelObj.setStatusType(word);
                dismiss();
                if(mListener != null){
                    mListener.onStatusSelected(2,sprModelObj);
                }
            }
        });

        return parentView;
    }

    public interface statusConfirmed {
        void onStatusSelected(int confirmOrDecline , SPRegistrationModel sprObj);
    }

    public void setSalonObj(SPRegistrationModel salonsDetails) {
        mDetails = salonsDetails;
    }

}

