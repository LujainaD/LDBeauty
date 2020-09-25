package com.lujaina.ldbeauty;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.lujaina.ldbeauty.Dialogs.AddInfoDialogFragment;

public class ProgressDialogFragment extends DialogFragment {

    public ProgressDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_progress_dialog, container, false);
        ProgressBar progressDialog = parentView.findViewById(R.id.progressBar);

        progressDialog = new ProgressBar(getContext());

        return parentView;

    }
}