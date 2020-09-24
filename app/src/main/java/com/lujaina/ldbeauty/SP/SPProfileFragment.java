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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.LoginChoicesFragment;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;


public class SPProfileFragment extends Fragment {
    private Context mContext;
    private MediatorInterface mMediatorInterface;
    public FirebaseAuth mAuth;
    public FirebaseUser mFirebaseUser;
    DatabaseReference myRef;
    private SPRegistrationModel mCurrentUser;


    public SPProfileFragment() {
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
        View parentView = inflater.inflate(R.layout.fragment_s_p_profile, container, false);
        final ImageView profileImag = parentView.findViewById(R.id.civ_profile);
        TextView salonPages = parentView.findViewById(R.id.tv_salonPages);
        final TextView ownerName = parentView.findViewById(R.id.tv_title);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid());

        salonPages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediatorInterface !=null){
                    mMediatorInterface.changeFragmentTo(new SalonProfileFragment(), SalonProfileFragment.class.getSimpleName());
                }

            }
        });


            final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Welcome Back ");
        progressDialog.show();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SPRegistrationModel u = dataSnapshot.getValue(SPRegistrationModel.class);// this will convert json to java


                if (mFirebaseUser != null && u != null) {
                    ownerName.setText(u.getOwnerName());
                    Glide.with(mContext).load(u.getOwnerImageURL()).into(profileImag);
                    progressDialog.dismiss();

                } else {
                    progressDialog.dismiss();
                    Intent intent = new Intent(getActivity(), LoginChoicesFragment.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
        return parentView;
    }

    public void setOwnerName(SPRegistrationModel user) {
        mCurrentUser = user;
    }
}