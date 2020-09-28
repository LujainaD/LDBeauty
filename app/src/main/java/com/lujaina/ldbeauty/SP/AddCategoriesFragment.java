package com.lujaina.ldbeauty.SP;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lujaina.ldbeauty.Dialogs.AddCategoriesDialogFragment;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.R;


public class AddCategoriesFragment extends Fragment {

    private MediatorInterface mMediatorInterface;
    private Context mContext;
    public AddCategoriesFragment() {
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
        View  parentView = inflater.inflate(R.layout.fragment_add_categories, container, false);
        FloatingActionButton add = parentView.findViewById(R.id.add_button);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCategoriesDialogFragment dialogFragment = new AddCategoriesDialogFragment();
                dialogFragment.show(getChildFragmentManager(), AddCategoriesDialogFragment.class.getSimpleName());
            }
        });

        return  parentView;
    }
}