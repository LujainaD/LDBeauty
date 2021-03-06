package com.lujaina.ldbeauty.Client;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class ClientProfileFragment extends Fragment {

    private Context mContext;
    DatabaseReference myRef;
    ProgressDialog progressDialog;
    NavController navController;

    public ClientProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
       /* if (context instanceof MediatorInterface) {
            mMediatorInterface = (MediatorInterface) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement MediatorInterface");
        }*/
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_client_profile, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav);
        navBar.setVisibility(View.VISIBLE);
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        //navController.getBackStackEntry()
        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        final FirebaseUser mFirebaseUser = mAuth.getCurrentUser();

        myRef = FirebaseDatabase.getInstance().getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid());

        final TextView userName = parentView.findViewById(R.id.tv_title);
        CardView editProfile = parentView.findViewById(R.id.cv_profile);
        CardView appointment = parentView.findViewById(R.id.cv_appointment);
        CardView feedback = parentView.findViewById(R.id.cv_feedback);
        final CircleImageView profileImag = parentView.findViewById(R.id.civ_profile);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(mMediatorInterface != null){
                    mMediatorInterface.changeFragmentTo(new EditClientProfileFragment(), EditClientProfileFragment.class.getSimpleName());
                }*/
                navController.navigate(R.id.action_clientProfileFragment_to_editClientProfileFragment);

            }
        });

        appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(mMediatorInterface != null){
                    mMediatorInterface.changeFragmentTo(new ClientAppointmentFragment(), ClientAppointmentFragment.class.getSimpleName());
                }*/
                navController.navigate(R.id.action_clientProfileFragment_to_clientAppointmentFragment);

            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if(mMediatorInterface != null){
                    mMediatorInterface.changeFragmentTo(new ClientFeedbackFragment(), ClientFeedbackFragment.class.getSimpleName());
                }*/
                navController.navigate(R.id.action_clientProfileFragment_to_clientFeedbackFragment);

            }

        });

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_bar);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        myRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SPRegistrationModel u = dataSnapshot.getValue(SPRegistrationModel.class);// this will convert json to java

                if (mFirebaseUser != null && u != null) {
                    userName.setText(u.getUserName());
                    if(u.getOwnerImageURL() == null){
                        profileImag.setImageDrawable(mContext.getResources().getDrawable(R.drawable.profile));
                        progressDialog.dismiss();

                    }else {
                        if (isValidContextForGlide(mContext)){
                            // Load image via Glide lib using context
                            progressDialog.dismiss();
                            Glide.with(mContext).load(u.getOwnerImageURL()).into(profileImag);
                        }
                    }
                } else {
                    progressDialog.dismiss();
                    /*if(mMediatorInterface!= null){
                        mMediatorInterface.changeFragmentTo(new LoginChoicesFragment(),LoginChoicesFragment.class.getSimpleName());
                    }*/
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
        return parentView;
    }
    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }
}