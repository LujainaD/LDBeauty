package com.lujaina.ldbeauty.Client;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.ClientsAppointmentModel;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.ArrayList;


public class PaymentFragment extends Fragment {
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;

    private static final String CONFIG_CLIENT_ID = "credentials from developer.paypal.com";

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID);


    private Context mContext;
    private MediatorInterface mMediatorCallback;
    FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
     FirebaseDatabase database ;
    DatabaseReference myRef;
    FragmentTransaction transaction;
   private String totalPrice;
    private String clientId;
    String values;

    private ArrayList<ClientsAppointmentModel> serviceArray;
    private String ownerId;
    NavController navController;

    public PaymentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
       // mMediatorCallback = (MediatorInterface) context;
    }

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_payment, container, false);
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        totalPrice = getActivity().getIntent().getExtras().getString("total");
        ownerId = getActivity().getIntent().getExtras().getString("ownerId");

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mFirebaseUser = mAuth.getCurrentUser();
        Log.w("totalWithPrice",ownerId + totalPrice);
        myRef = database.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid()).child(Constants.Client_Cart);
        TextView total = parentView.findViewById(R.id.tv_total);
        ImageButton paypal = parentView.findViewById(R.id.btn_paypal);
        ImageButton visa = parentView.findViewById(R.id.btn_visa);
        total.setText(totalPrice);

            paypal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    beginPayment();
                }
            });


/*

        visa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                    if (mMediatorCallback != null) {
                        mMediatorCallback.changeFragmentTo(new VisaPaymentFragment(), VisaPaymentFragment.class.getSimpleName());
                    }


            }
        });
*/




        return parentView;
    }

   /* public void setTotalPrice(String price, ArrayList<ClientsAppointmentModel>serviceArray, String ownerId) {
        this.totalPrice = price;
        this.ownerId= ownerId;
        this.serviceArray = this.serviceArray;
    }*/

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
                   /* if(mMediatorCallback != null){
                        OrderFragment fragment = new OrderFragment();
                        fragment.setOwnerId(ownerId);
                        mMediatorCallback.changeFragmentTo(fragment, OrderFragment.class.getSimpleName());
                    }
*/
                    Bundle bundle= new Bundle();
                    bundle.putString("ownerId",ownerId);
                    navController.navigate(R.id.action_paymentFragment_to_orderFragment,bundle);
                } catch (JSONException e) {
                    Log.e("sampleapp", "an extremely unlikely failure occurred: ", e);
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            /*if(mMediatorCallback != null){
                mMediatorCallback.changeFragmentTo(new CancelOrderFragment() , CancelOrderFragment.class.getSimpleName());
            }*/
            navController.navigate(R.id.action_paymentFragment_to_cancelOrderFragment);

            Log.i("sampleapp", "The user canceled.");
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            /*if(mMediatorCallback != null){
                mMediatorCallback.changeFragmentTo(new CancelOrderFragment() , CancelOrderFragment.class.getSimpleName());
            }*/
            navController.navigate(R.id.action_paymentFragment_to_cancelOrderFragment);

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

