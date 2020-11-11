package com.lujaina.ldbeauty.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lujaina.ldbeauty.AppOwner.AoConfirmSalonsFragment;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class SalonConfirmDialogFragment extends DialogFragment {

    private SPRegistrationModel mDetails;
    private SalonConfirmDialogFragment.statusConfirmed mListener;
    private SPRegistrationModel sprModelObj;


    public SalonConfirmDialogFragment(AoConfirmSalonsFragment aoConfirmContext, SPRegistrationModel salonsDetails) {
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

