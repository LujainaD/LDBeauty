package com.lujaina.ldbeauty.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.LoginChoicesFragment;
import com.lujaina.ldbeauty.LoginFragment;
import com.lujaina.ldbeauty.R;
import com.lujaina.ldbeauty.SignUpFragment;

public class NoLoginDialogFragment extends DialogFragment {
    private MediatorInterface mMediatorInterface;
    private Context mContext;
    private int i;


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
        View parentView = inflater.inflate(R.layout.fragment_no_login_dialog, container, false);
        Button login = parentView.findViewById(R.id.btn_login);
        Button signUp = parentView.findViewById(R.id.btn_signUp);
        TextView textView = parentView.findViewById(R.id.tv_text);

        if(i== 1){
            textView.setText("Please login or signup for accessing your profile");
        }else {

        }



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediatorInterface != null){
                    mMediatorInterface.changeFragmentTo(new LoginChoicesFragment(), LoginChoicesFragment.class.getSimpleName());
                    dismiss();
                }

            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediatorInterface != null){
                    SignUpFragment usertype= new SignUpFragment();
                    usertype.setViewPager("Client");
                    mMediatorInterface.changeFragmentTo(usertype, SignUpFragment.class.getSimpleName());
                    dismiss();
                }

            }
        });


        return parentView;
    }

    public void showText(int i) {
        this.i = i;
    }
}