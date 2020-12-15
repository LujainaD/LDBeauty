package com.lujaina.ldbeauty.User;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lujaina.ldbeauty.R;

public class AboutAppFragment extends Fragment {


    public AboutAppFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_about_app, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        final TextView emailText = parentView.findViewById(R.id.tv_email);
        emailText.setPaintFlags(emailText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        emailText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setClassName("com.google.android.gm", "com.google.android.gm.ConversationListActivityGmail");
                intent.putExtra(Intent.EXTRA_EMAIL, emailText.getText().toString().trim());
                startActivity(intent);

                try {
                    startActivity(Intent.createChooser(intent,"Choose app to send email"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return parentView;
    }
}