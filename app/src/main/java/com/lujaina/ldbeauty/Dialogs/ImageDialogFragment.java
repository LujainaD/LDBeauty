package com.lujaina.ldbeauty.Dialogs;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lujaina.ldbeauty.R;


public class ImageDialogFragment extends DialogFragment {
    private ChooseDialogInterface mListener;


    public ImageDialogFragment() {
        // Required empty public constructor
    }
    public void setChooseDialogListener(ChooseDialogInterface listener) {
        mListener = listener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_image_dialog, container, false);

        Button btnOpenCamera = parentView.findViewById(R.id.btn_camera);
        Button btnOpenGallery = parentView.findViewById(R.id.btn_gallery);

        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onCameraButtonClick();
                }
                dismiss();
            }
        });

        btnOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.openGallery();
                }
                dismiss();
            }
        });

        return parentView;
    }

    public interface ChooseDialogInterface {

        void openGallery();

        void onCameraButtonClick();

    }
}