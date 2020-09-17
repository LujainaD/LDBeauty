package com.lujaina.ldbeauty.SP;

import android.app.Dialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.lujaina.ldbeauty.Adapters.ColorAdapter;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;


public class AddInfoDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener{

private MediatorInterface mMediatorInterface;
private Context mContext;
    int colors[] = {R.color.white, R.color.beige, R.color.lightOrange,R.color.lightGray,R.color.lightPink};

    public AddInfoDialogFragment() {
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
            int width = ViewGroup.LayoutParams.WRAP_CONTENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View parentView= inflater.inflate(R.layout.fragment_add_info_dialog, container, false);
        final Spinner spinner = parentView.findViewById(R.id.colorSpinner);
        final TextInputEditText etTitle = parentView.findViewById(R.id.title);
        final TextInputEditText etBody = parentView.findViewById(R.id.body);

        Button btnAdd = parentView.findViewById(R.id.btn_add);
        Button btnCancel = parentView.findViewById(R.id.btn_cancel);
        spinner.setOnItemSelectedListener(this);

        spinner.setBackgroundColor(getContext().getResources().getColor(R.color.white));
      /*  ArrayList<String> backgroundArrayList = new ArrayList<>();
        backgroundArrayList.add("color");
        backgroundArrayList.add("white");
        backgroundArrayList.add("#FDF8F8");
        backgroundArrayList.add("#FFC5CB");
        backgroundArrayList.add("#E6E7E8");
        backgroundArrayList.add("#FFCCEC");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, backgroundArrayList);*/
        ColorAdapter colorAdapter = new ColorAdapter(mContext, colors);
        spinner.setAdapter(colorAdapter);
/*
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 1:
                        spinner.setBackgroundColor(mContext.getResources().getColor(R.color.white));
*/
/*
                        mColor = "#FFFFFF";
*//*

                        break;

                    case 2:
                        spinner.setBackgroundColor(mContext.getResources().getColor(R.color.beige));

*/
/*
                        mColor = "#FDF8F8";
*//*

                        break;

                    case 3:
                        spinner.setBackgroundColor(mContext.getResources().getColor(R.color.lightOrange));

*/
/*
                        mColor = "#FFC5CB";
*//*

                        break;

                    case 4:
                        spinner.setBackgroundColor(mContext.getResources().getColor(R.color.lightGray));

*/
/*
                        mColor = "#E6E7E8";
*//*

                        break;

                    case 5:
                        spinner.setBackgroundColor(mContext.getResources().getColor(R.color.lightPink));

*/
/*
                        mColor = "#FFCCEC";
*//*

                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
*/


        /*colorBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorSpinnerFragment colorDialog = new ColorSpinnerFragment();
                colorDialog.show(getChildFragmentManager(), ColorSpinnerFragment.class.getSimpleName());

            }
        });*/

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString();
                String body = etBody.getText().toString();
                if (title.isEmpty()) {
                    etTitle.setError("Please write Info Title");
                } else if (body.isEmpty()) {
                    etBody.setError("Please write Information describe the title");
                }
            }
        });


        return parentView;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}