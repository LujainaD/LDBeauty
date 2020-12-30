package com.lujaina.ldbeauty.Dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lujaina.ldbeauty.Adapters.AppointmentAdapter;
import com.lujaina.ldbeauty.Models.ClientsAppointmentModel;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;
import com.lujaina.ldbeauty.SP.SalonAppointmentFragment;

public class ConfirmDialogFragment extends DialogFragment {

    private ConfirmDialogFragment.selectedButton mListener;
    private ClientsAppointmentModel clientInformation;

    public ConfirmDialogFragment(SalonAppointmentFragment salonAppointmentFragment) {
        // Required empty public constructor
        mListener = (selectedButton) salonAppointmentFragment;
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
        View parentView = inflater.inflate(R.layout.fragment_confirm_dialog, container, false);
        Button possitveBtn = parentView.findViewById(R.id.btn_yes);
        Button negativeBtn = parentView.findViewById(R.id.btn_cancel);



        possitveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener!= null){
                    mListener.onButtonSelected(1, clientInformation);
                dismiss();
                }
            }
        });

        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener!= null){
                    mListener.onButtonSelected(2, clientInformation);
              dismiss();
                }
            }
        });

        return parentView;
    }




    public void sendInfo(ClientsAppointmentModel confirm) {
        clientInformation = confirm;
    }
    public void setonClickListener(ConfirmDialogFragment.selectedButton listener){
        mListener = listener;
    }
    public interface selectedButton {
        void onButtonSelected(int confirmOrDecline , ClientsAppointmentModel information);
    }

}