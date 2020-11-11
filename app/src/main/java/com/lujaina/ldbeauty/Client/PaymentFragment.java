package com.lujaina.ldbeauty.Client;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.R;


public class PaymentFragment extends Fragment {

    FragmentTransaction ft;
    PayPalFragment frg1;
    VisaPaymentFragment frg2;

    RadioGroup rg;
    RadioButton rb_visa;
    RadioButton rb_paypal;

    private Context mContext;
    private MediatorInterface mMediatorCallback;

    FragmentTransaction transaction;
    public PaymentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        mMediatorCallback = (MediatorInterface) context;
    }

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_payment, container, false);
        frg1 = new PayPalFragment();
        frg2 = new VisaPaymentFragment();

        rg = parentView.findViewById(R.id.radioGroup);
        rb_visa = parentView.findViewById(R.id.rb_visa);
        rb_paypal = parentView.findViewById(R.id.rb_paypal);



        if(rg.getCheckedRadioButtonId() == -1){

        }else {
            onRadioButtonClicked();
        }




        /*getChildFragmentManager().beginTransaction().add(R.id.fl_payment, frg1).commit();

        // set listener
        ((RadioGroup) parentView.findViewById(R.id.radioGroup)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ft = getChildFragmentManager().beginTransaction();
                switch (checkedId) {
                    case R.id.rb_paypal:
                        ft.replace(R.id.fl_payment, frg1);
                        break;
                    case R.id.rb_visa:
                        ft.replace(R.id.fl_payment, frg2);
                        break;
                }
                ft.commit();
            }
        });*/
        return parentView;
    }


    public void onRadioButtonClicked() {

        if(rb_paypal.isChecked()){


        }else {

        }

    }
}

