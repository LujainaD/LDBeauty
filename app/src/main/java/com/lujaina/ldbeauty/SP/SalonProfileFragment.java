package com.lujaina.ldbeauty.SP;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Dialogs.AddCategoriesDialogFragment;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.LoginChoicesFragment;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class SalonProfileFragment extends Fragment {
    private Context mContext;
    private MediatorInterface mMediatorInterface;

    DatabaseReference myRef;

    ProgressDialog progressDialog;

    public SalonProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if(context instanceof MediatorInterface) {
            mMediatorInterface = (MediatorInterface) context;
        }
        else{
            throw new RuntimeException(context.toString()+ "must implement MediatorInterface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_salon_profile, container, false);
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav);
        navBar.setVisibility(View.GONE);
        TextView aboutPage = parentView.findViewById(R.id.tv_AboutPage);
        final CircleImageView profileImag = parentView.findViewById(R.id.civ_profile);
        ImageButton back = parentView.findViewById(R.id.ib_back);
        final TextView salonName = parentView.findViewById(R.id.tv_title);
        TextView categoryPage = parentView.findViewById(R.id.tv_services);
        TextView galleryPage = parentView.findViewById(R.id.tv_salonGallery);
        TextView locationPage = parentView.findViewById(R.id.tv_location);
        TextView offerPage = parentView.findViewById(R.id.tv_offers);

        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        final FirebaseUser mFirebaseUser = mAuth.getCurrentUser();

        myRef = FirebaseDatabase.getInstance().getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediatorInterface != null){
                    mMediatorInterface.onBackPressed();
                }
            }
        });

    categoryPage.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mMediatorInterface !=null){
                mMediatorInterface.changeFragmentTo(new AddCategoriesFragment(), AddCategoriesFragment.class.getSimpleName());
            }
        }
    });
        aboutPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediatorInterface !=null){
                    mMediatorInterface.changeFragmentTo(new AddInfoFragment(), AddInfoFragment.class.getSimpleName());
                }
            }
        });

        galleryPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediatorInterface !=null){
                    mMediatorInterface.changeFragmentTo(new AddGalleryFragment(), AddGalleryFragment.class.getSimpleName());
                }
            }
        });

        locationPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediatorInterface !=null){
                    AddSalonLocationFragment location = new AddSalonLocationFragment();
                    mMediatorInterface.changeFragmentTo(new AddSalonLocationFragment(), AddSalonLocationFragment.class.getSimpleName());
                }
            }
        });
        offerPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediatorInterface !=null){
                    mMediatorInterface.changeFragmentTo(new AddSalonOffersFragment(), AddSalonOffersFragment.class.getSimpleName());
                }
            }
        });

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_bar);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SPRegistrationModel u = dataSnapshot.getValue(SPRegistrationModel.class);// this will convert json to java

                if (mFirebaseUser != null && u != null) {
                    salonName.setText(u.getSalonName());
                    Glide.with(mContext).load(u.getSalonImageURL()).into(profileImag);
                    progressDialog.dismiss();

                } else {
                    progressDialog.dismiss();
                    if(mMediatorInterface!= null){
                        mMediatorInterface.changeFragmentTo(new LoginChoicesFragment(),LoginChoicesFragment.class.getSimpleName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
        return parentView;
    }
}