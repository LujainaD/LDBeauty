package com.lujaina.ldbeauty;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;

public class SignUpFragment extends Fragment {
    private Context mContext;
    private MediatorInterface mMediatorInterface;

    public SignUpFragment() {
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
        View parentView =  inflater.inflate(R.layout.fragment_sign_up, container, false);
        Toolbar toolbar = parentView.findViewById(R.id.toolbar);
        ImageButton back = parentView.findViewById(R.id.ib_back);
        TabLayout tabs = parentView.findViewById(R.id.tab);
        ViewPager viewPager = parentView.findViewById(R.id.viewPager);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediatorInterface != null){
                    mMediatorInterface.onBackPressed();
                }
            }
        });

        SignUpViewAdapter adapter = new SignUpViewAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);

        tabs.setupWithViewPager(viewPager);


        return parentView;
    }
}