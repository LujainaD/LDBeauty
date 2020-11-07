package com.lujaina.ldbeauty.Dialogs;

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

import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.LoginChoicesFragment;
import com.lujaina.ldbeauty.R;
import com.lujaina.ldbeauty.SignUpFragment;

public class NoLoginDialogFragment extends DialogFragment {
    private MediatorInterface mMediatorInterface;
    private Context mContext;


    public NoLoginDialogFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_no_login_dialog, container, false);
        Button login = parentView.findViewById(R.id.btn_login);
        Button signUp = parentView.findViewById(R.id.btn_signUp);



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediatorInterface != null){
                    mMediatorInterface.changeFragmentTo(new LoginChoicesFragment(), LoginChoicesFragment.class.getSimpleName());
                }

            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediatorInterface != null){
                    mMediatorInterface.changeFragmentTo(new SignUpFragment(), SignUpFragment.class.getSimpleName());
                }

            }
        });


        return parentView;
    }
}