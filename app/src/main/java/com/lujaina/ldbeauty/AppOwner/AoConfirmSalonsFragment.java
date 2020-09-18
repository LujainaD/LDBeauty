package com.lujaina.ldbeauty.AppOwner;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lujaina.ldbeauty.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AoConfirmSalonsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AoConfirmSalonsFragment extends Fragment {


    public AoConfirmSalonsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView= inflater.inflate(R.layout.fragment_ao_confirm_salons, container, false);

        return parentView;
    }
}