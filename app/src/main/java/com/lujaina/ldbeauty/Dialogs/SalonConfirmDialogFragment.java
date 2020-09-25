package com.lujaina.ldbeauty.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class SalonConfirmDialogFragment extends DialogFragment {
    private static final int CAPTURE_IMAGE = 1;
    private FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    SPRegistrationModel status;
    private SPRegistrationModel mDetails;
    private Context mContext;
    private MediatorInterface mMediatorInterface;
    private SalonConfirmDialogFragment.statusConfirmed mListener;
    private SPRegistrationModel sprModelObj;
String mImagePath;
    Uri imgUri;
    public SalonConfirmDialogFragment(AoConfirmSalonsFragment aoConfirmContext, SPRegistrationModel salonsDetails) {
        mListener = (statusConfirmed) aoConfirmContext;
		sprModelObj = salonsDetails;
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
        View parentView = inflater.inflate(R.layout.fragment_salon_confirm_dialog, container, false);
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference(Constants.Users).child(Constants.Salon_Owner);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        TextView ownerName = parentView.findViewById(R.id.tv_ownerName);
        TextView ownerPhone = parentView.findViewById(R.id.tv_ownerPhoner);
        TextView salonName = parentView.findViewById(R.id.tv_salonName);
        TextView salonLocation = parentView.findViewById(R.id.tv_salonLocat);
        TextView date = parentView.findViewById(R.id.tv_regisDate);
        final Button confirm = parentView.findViewById(R.id.btn_confirm);
        Button cancel = parentView.findViewById(R.id.btn_cancel);


        if (mDetails != null) {
            ownerName.setText(mDetails.getOwnerName());
            ownerPhone.setText(mDetails.getPhoneNumber());
            salonName.setText(mDetails.getSalonName());
            salonLocation.setText(mDetails.getSalonCity());
            date.setText(mDetails.getRegistrationDate());
        }


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               String word = "Confirm";
                SPRegistrationModel confirm = new SPRegistrationModel();
                confirm.setStatusType(word);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mDetails.getOwnerId());

                myRef.child("statusType").setValue(confirm.getStatusType()).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                        	Toast.makeText(mContext, "success", Toast.LENGTH_SHORT).show();
                            dismiss();
                        } else {
                            Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                if(mListener != null){
                    mListener.onStatusSelected(1,sprModelObj);
                }


            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = "Cancel";
                SPRegistrationModel confirm = new SPRegistrationModel();
                confirm.setStatusType(word);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mDetails.getOwnerId());
                Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.confirm);

                myRef.child("statusType").setValue(confirm.getStatusType())
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(mContext, "success", Toast.LENGTH_SHORT).show();
                            dismiss();
                        } else {
                            Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

                if(mListener != null){
                    mListener.onStatusSelected(2,sprModelObj);

                }


            }
        });

        return parentView;
    }

//    public void setStatusListener(status statusListener){
//        mListener = statusListener;
//    }

    public interface statusConfirmed {
        void onStatusSelected(int confirmOrDecline , SPRegistrationModel sprObj);
    }

    public void setSalonObj(SPRegistrationModel salonsDetails) {
        mDetails = salonsDetails;
    }


}

