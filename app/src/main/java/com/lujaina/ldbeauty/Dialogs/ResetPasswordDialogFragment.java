package com.lujaina.ldbeauty.Dialogs;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.R;

public class ResetPasswordDialogFragment extends DialogFragment {
    private FirebaseAuth mAuth;
    private Context mContext;
    private MediatorInterface mMediatorInterface;


    public ResetPasswordDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if(context instanceof MediatorInterface) {
            mMediatorInterface = (MediatorInterface) context;
        }
        else{
            throw new RuntimeException(context.toString()+ "must implement MediatorInterface");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_reset_password_dialog, container, false);
        final TextInputEditText tv_email = parentView.findViewById(R.id.til_email);
        Button cancel = parentView.findViewById(R.id.btn_cancel);
        Button reset = parentView.findViewById(R.id.btn_rest);
        mAuth = FirebaseAuth.getInstance();


        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = tv_email.getText().toString();
                if (email.isEmpty()) {
                    tv_email.setError("Please write your email");
                }
                else{
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(mContext,"check your email to reset your password", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                                Log.d("mail_error", task.getException().toString());
                            }


                        }


                    });
                }
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