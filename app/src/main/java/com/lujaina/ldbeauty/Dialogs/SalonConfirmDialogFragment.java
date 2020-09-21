package com.lujaina.ldbeauty.Dialogs;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lujaina.ldbeauty.R;

public class SalonConfirmDialogFragment extends DialogFragment {



    public SalonConfirmDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_salon_confirm_dialog, container, false);

        return parentView;
    }
}