package com.lujaina.ldbeauty.Dialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lujaina.ldbeauty.R;


public class GreetingDialogFragment extends DialogFragment {
    private int status = 0;
    Handler handler = new Handler();
    private int i;

    public GreetingDialogFragment() {
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
        View parentView = inflater.inflate(R.layout.fragment_greeting_dialog, container, false);
        TextView greetingText = parentView.findViewById(R.id.tv_greetingText);

        if(i==1){
            greetingText.setText("Thank you for your comment");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (status < 200) {

                        status += 1;

                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                if (status == 100) {
                                /*if(mListener != null){
                                    mListener.closeDialog(1);
                                }*/
                                }
                            }
                        });
                    }
                }
            }).start();
        }else if (i==2){
            greetingText.setText("Your Changes are saved successfully");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (status < 200) {

                        status += 1;

                        try {
                            Thread.sleep(23);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                if (status == 100) {
                                /*if(mListener != null){
                                    mListener.closeDialog(1);
                                }*/
                                }
                            }
                        });
                    }
                }
            }).start();
        }else {
            greetingText.setText("your password is changed successfully");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (status < 200) {

                        status += 1;

                        try {
                            Thread.sleep(23);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                if (status == 100) {
                                /*if(mListener != null){
                                    mListener.closeDialog(1);
                                }*/
                                }
                            }
                        });
                    }
                }
            }).start();
        }


        return parentView;
    }

    public void getDialogText(int i) {
        this.i= i;
    }
}