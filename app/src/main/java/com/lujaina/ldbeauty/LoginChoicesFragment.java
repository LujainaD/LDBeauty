package com.lujaina.ldbeauty;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lujaina.ldbeauty.AppOwner.AOLoginFragment;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;


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
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav);
        navBar.setVisibility(View.GONE);
        Button client = parentView.findViewById(R.id.btn_client);
        Button salonOwner = parentView.findViewById(R.id.btn_salonOwner);
        Button appOwner = parentView.findViewById(R.id.tv_appOwner);
        TextView signup = parentView.findViewById(R.id.tv_SignUp);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediatorInterface != null){
                    SignUpFragment usertype= new SignUpFragment();
                    usertype.setViewPager("Client");
                    mMediatorInterface.changeFragmentTo(usertype, SignUpFragment.class.getSimpleName());
                }
            }
        });

        client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediatorInterface != null){
                    LoginFragment usertype= new LoginFragment();
                    usertype.setUserType("Client");
                    mMediatorInterface.changeFragmentTo(usertype, LoginFragment.class.getSimpleName());
                }
            }
        });

        salonOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediatorInterface != null){
                    LoginFragment usertype= new LoginFragment();
                    usertype.setUserType("Salon Owner");
                    mMediatorInterface.changeFragmentTo(usertype, LoginFragment.class.getSimpleName());
                }
            }
        });
        appOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediatorInterface != null){
                    LoginFragment usertype= new LoginFragment();
                    usertype.setUserType("App Owner");
                    mMediatorInterface.changeFragmentTo(usertype, LoginFragment.class.getSimpleName());
                }
            }
        });


        return parentView;
    }

}