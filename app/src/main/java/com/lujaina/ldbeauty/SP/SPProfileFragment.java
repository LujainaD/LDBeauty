package com.lujaina.ldbeauty.SP;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.lujaina.ldbeauty.Client.EditClientProfileFragment;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.LoginChoicesFragment;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class SPProfileFragment extends Fragment {
    private Context mContext;

    private ProgressDialog progressDialog;
    NavController navController;
    private Activity mActivity;

    public SPProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        mActivity = getActivity();

        /*if(context instanceof MediatorInterface) {
            mMediatorInterface = (MediatorInterface) context;
        }
        else{
            throw new RuntimeException(context.toString()+ "must implement MediatorInterface");
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_s_p_profile, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav);
        navBar.setVisibility(View.VISIBLE);
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
          navController = navHostFragment.getNavController();

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_bar);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        final CircleImageView profileImag = parentView.findViewById(R.id.civ_profile);
        TextView salonAppointment = parentView.findViewById(R.id.tv_appointment);
        TextView salonFeedback = parentView.findViewById(R.id.tv_feedback);
        TextView editSalonProfile = parentView.findViewById(R.id.tv_editSalonFile);
        TextView editOwnerProfile = parentView.findViewById(R.id.tv_editSPFile);
        TextView calendar = parentView.findViewById(R.id.tv_calendar);

        TextView salonPages = parentView.findViewById(R.id.tv_salonPages);
        final TextView ownerName = parentView.findViewById(R.id.tv_title);

         FirebaseAuth mAuth = FirebaseAuth.getInstance();
         final FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid());

        salonPages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   // mMediatorInterface.changeFragmentTo(new SalonProfileFragment(), SalonProfileFragment.class.getSimpleName());
                    navController.navigate(R.id.action_SPProfileFragment_to_salonProfileFragment);



            }
        });

        editOwnerProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_SPProfileFragment_to_editSpProfileFragment3);


            }
        });

        editSalonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_SPProfileFragment_to_editSalonProfileFragment);


            }
        });

        salonFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_SPProfileFragment_to_salonFeedbackFragment);


            }
        });

        salonAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                navController.navigate(R.id.action_SPProfileFragment_to_salonAppointmentFragment);

            }
        });

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_SPProfileFragment_to_spCalenderFragment);


            }
        });


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (mActivity == null) {
                    return;
                }
                SPRegistrationModel u = dataSnapshot.getValue(SPRegistrationModel.class);// this will convert json to java

                if (mFirebaseUser != null && u != null) {
                    ownerName.setText(u.getUserName());
                    Glide.with(mActivity).load(u.getOwnerImageURL()).into(profileImag);
                    progressDialog.dismiss();

                } else {
                    progressDialog.dismiss();

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