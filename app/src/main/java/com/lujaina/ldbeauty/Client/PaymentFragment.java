package com.lujaina.ldbeauty.Client;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.R;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;


public class PaymentFragment extends Fragment {
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;

    private static final String CONFIG_CLIENT_ID = "credentials from developer.paypal.com";

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID);


    private Context mContext;
    private MediatorInterface mMediatorCallback;

    FragmentTransaction transaction;
    private String totalPrice;

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
        TextView total = parentView.findViewById(R.id.tv_total);

        ImageButton paypal = parentView.findViewById(R.id.btn_paypal);
        ImageButton visa = parentView.findViewById(R.id.btn_visa);

        total.setText(totalPrice);

        paypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(mMediatorCallback != null){
                    mMediatorCallback.changeFragmentTo(payPalFragment, PayPalFragment.class.getSimpleName());
                }*/
                beginPayment();

            }
        });


        visa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediatorCallback != null){
                    mMediatorCallback.changeFragmentTo(new VisaPaymentFragment(), VisaPaymentFragment.class.getSimpleName());
                }
            }
        });




        return parentView;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void beginPayment(){
        StringBuffer b = new StringBuffer(totalPrice);
        b.delete(0,7);
        b.reverse();
        b.delete(0,4);
        b.reverse();
        String total = b.toString();
        Intent serviceConfig = new Intent(getContext(), PayPalService.class);
        serviceConfig.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        getActivity().startService(serviceConfig);

        PayPalPayment payment = new PayPalPayment(new BigDecimal(total), "USD", "Total:", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent paymentConfig = new Intent(getContext(), PaymentActivity.class);
        paymentConfig.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config); //send the same configuration for restart resiliency
        paymentConfig.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(paymentConfig, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    Log.i("sampleapp", confirm.toJSONObject().toString(4));

                    // TODO: send 'confirm' to your server for verification.
                    // see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                    // for more details.

                } catch (JSONException e) {
                    Log.e("sampleapp", "an extremely unlikely failure occurred: ", e);
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("sampleapp", "The user canceled.");
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("sampleapp", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
        }
    }



    @Override
    public void onDestroy() {
        // Stop service when done
        getActivity().stopService(new Intent(getContext(), PayPalService.class));
        super.onDestroy();
    }
}

