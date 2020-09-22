package com.lujaina.ldbeauty;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lujaina.ldbeauty.AppOwner.AOLoginFragment;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.SP.SPLoginFragment;


public class LoginChoicesFragment extends Fragment {

    private Context mContext;
    private MediatorInterface mMediatorInterface;

    public LoginChoicesFragment() {
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
        View parentView = inflater.inflate(R.layout.fragment_login_choices, container, false);
        Button client = parentView.findViewById(R.id.btn_client);
        Button salonOwner = parentView.findViewById(R.id.btn_salonOwner);
        TextView appOwner = parentView.findViewById(R.id.tv_appOwner);


        salonOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediatorInterface != null){
                    mMediatorInterface.changeFragmentTo(new SPLoginFragment(), SPLoginFragment.class.getSimpleName());
                }
            }
        });
        appOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediatorInterface != null){
                    mMediatorInterface.changeFragmentTo(new AOLoginFragment(), AOLoginFragment.class.getSimpleName());
                }
            }
        });


        return parentView;
    }

}