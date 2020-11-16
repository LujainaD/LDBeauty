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
import android.widget.TextView;

import com.lujaina.ldbeauty.Models.ClientsAppointmentModel;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;


public class AppointmentDialog extends DialogFragment {
    private ClientsAppointmentModel mDetails;
    private SalonConfirmDialogFragment.statusConfirmed mListener;
    private ClientsAppointmentModel sprModelObj;
    private ClientsAppointmentModel appointmentInfo;


    public AppointmentDialog() {
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
        View parentView = inflater.inflate(R.layout.fragment_appointment_dialog, container, false);

        TextView clientName = parentView.findViewById(R.id.tv_clientName);
        TextView clientPhone = parentView.findViewById(R.id.tv_clientPhone);
        TextView date = parentView.findViewById(R.id.tv_date);
        TextView tv_spec = parentView.findViewById(R.id.tv4);
        TextView tv_ser = parentView.findViewById(R.id.tv5);

        TextView specialist = parentView.findViewById(R.id.tv_specialist);
        TextView service = parentView.findViewById(R.id.tv_service);
        TextView price = parentView.findViewById(R.id.tv_price);
        TextView status = parentView.findViewById(R.id.tv_status);


        if(appointmentInfo != null){
            if(appointmentInfo.getServiceType().equals("Service")){
                clientName.setText(appointmentInfo.getClientName());
                clientPhone.setText(appointmentInfo.getClientPhone());
                date.setText(appointmentInfo.getAppointmentDate());
                specialist.setVisibility(View.VISIBLE);
                specialist.setText(appointmentInfo.getSpecialList());
                service.setText(appointmentInfo.getServiceTitle());
                price.setText(appointmentInfo.getPrice());
                status.setText(appointmentInfo.getAppointmentStatus());
            }else{
                clientName.setText(appointmentInfo.getClientName());
                clientPhone.setText(appointmentInfo.getClientPhone());
                date.setText(appointmentInfo.getAppointmentDate());
                tv_spec.setText("Offer Title");
                specialist.setText(appointmentInfo.getOfferTitle());
                tv_ser.setText("Offer Services");
                service.setText(appointmentInfo.getOfferServices());
                price.setText(appointmentInfo.getPrice());
                status.setText(appointmentInfo.getAppointmentStatus());

            }


        }

        return parentView;
    }

    public void setInfo(ClientsAppointmentModel info) {
        appointmentInfo = info;
    }
}