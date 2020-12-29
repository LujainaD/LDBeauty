package com.lujaina.ldbeauty.Dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.CategoryModel;
import com.lujaina.ldbeauty.Models.OfferModel;
import com.lujaina.ldbeauty.Models.ServiceModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;


public class OffersDialogFragment extends DialogFragment {


    private FirebaseUser mFirebaseUser;
    private Context mContext;
    private MediatorInterface mMediatorInterface;
    private String salonName;

    public OffersDialogFragment() {
        // Required empty public constructor
    }

    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_offers_dialog, container, false);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        final EditText et_title = parentView.findViewById(R.id.ti_title);
        final EditText et_name = parentView.findViewById(R.id.ti_name);
        final EditText previousPrice = parentView.findViewById(R.id.ti_price);
        final EditText currentPrice = parentView.findViewById(R.id.ti_Currentprice);
        Button btnAdd = parentView.findViewById(R.id.btn_add);
        Button btnCancel = parentView.findViewById(R.id.btn_cancel);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = et_title.getText().toString();
                String service = et_name.getText().toString();
                String price = previousPrice.getText().toString();
                String current = currentPrice.getText().toString();

                if(title.isEmpty()) {
                    et_title.setError("you should write offer name");
                }else if (service.isEmpty()) {
                    et_name.setError("you should write services of the offer)");
                }else if (price.isEmpty()) {
                    previousPrice.setError("you should write the previous price)");
                }else if (current.isEmpty()) {
                    currentPrice.setError("you should write the current price)");
                }else if(Integer.parseInt(current) >= Integer.parseInt(price)) {
                    currentPrice.setError("new price should be less than previous price");
                }else {


                    OfferModel offers = new OfferModel();
                    offers.setTitle(title);
                    offers.setServices(service);
                    offers.setPreviousPrice(price);
                    offers.setCurrentPrice(current);
                    addToDB(offers);

                }
            }
        });
                    return parentView;
    }

    private void addToDB(OfferModel offers) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid()).child(Constants.Salon_Offers);
        String id = myRef.push().getKey();
        offers.setOfferId(id);
        offers.setSalonOwnerId(mFirebaseUser.getUid());
        offers.setSalonName(salonName);
        myRef.child(offers.getOfferId()).setValue(offers).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(), "Your offer is added successfully ", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "failed ", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });


    }

    public void setOffers(OfferModel offersList, String salonName) {
        this.salonName = salonName;
    }
}