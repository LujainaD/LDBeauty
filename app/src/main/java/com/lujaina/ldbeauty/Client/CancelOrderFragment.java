package com.lujaina.ldbeauty.Client;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lujaina.ldbeauty.HomeActivity;
import com.lujaina.ldbeauty.R;


public class CancelOrderFragment extends Fragment {


    public CancelOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_cancel_order, container, false);
        Button btn_dismiss = parentView.findViewById(R.id.btn_dismiss);

        btn_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), HomeActivity.class);
                startActivity(i);
            }
        });
        return parentView;
    }
}