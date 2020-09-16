package com.lujaina.ldbeauty.SP;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.lujaina.ldbeauty.Models.ColorModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;


public class ColorSpinnerFragment extends DialogFragment {
    private Context mContext;
    private String mColor;
    private BackgroundListener mListener;


    public ColorSpinnerFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_color_spinner, container, false);
        final Spinner spinner = parentView.findViewById(R.id.colorSpinner);
        Button btnAdd = parentView.findViewById(R.id.btn_add);
        Button btnCancel = parentView.findViewById(R.id.btn_cancel);

        spinner.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));

        ArrayList<String> backgroundArrayList = new ArrayList<>();
        backgroundArrayList.add("#FFFFFF");
        backgroundArrayList.add("#FDF8F8");
        backgroundArrayList.add("#FFC5CB");
        backgroundArrayList.add("#E6E7E8");
        backgroundArrayList.add("#FFCCEC");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, backgroundArrayList);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        spinner.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                        mColor = "#FFFFFF";
                        break;

                    case 1:
                        spinner.setBackgroundColor(mContext.getResources().getColor(R.color.beige));

                        mColor = "#FDF8F8";
                        break;

                    case 2:
                        spinner.setBackgroundColor(mContext.getResources().getColor(R.color.lightOrange));

                        mColor = "#FFC5CB";
                        break;

                    case 3:
                        spinner.setBackgroundColor(mContext.getResources().getColor(R.color.lightGray));

                        mColor = "#E6E7E8";
                        break;

                    case 4:
                        spinner.setBackgroundColor(mContext.getResources().getColor(R.color.lightPink));

                        mColor = "#FFCCEC";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onEditClick(spinner.getSelectedItem().toString(), mColor);
                dismiss();
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 dismiss();
                }


        });



        return parentView;
    }











    public void setBackgroundListener(BackgroundListener listener){
        mListener = listener;

    }

    public interface BackgroundListener{
        void onEditClick(String s, String color);
    }
}