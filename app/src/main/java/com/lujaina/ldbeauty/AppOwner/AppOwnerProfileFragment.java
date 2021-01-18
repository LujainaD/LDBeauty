package com.lujaina.ldbeauty.AppOwner;

import android.annotation.SuppressLint;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;


public class AppOwnerProfileFragment extends Fragment {

    DatabaseReference myRef;
    ProgressDialog progressDialog;

    private Context mContext;
    NavController navController;




    public AppOwnerProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_app_owner_profile, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav);
        navBar.setVisibility(View.VISIBLE);
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        TextView confirmPage = parentView.findViewById(R.id.tv_confirm);
        TextView editProfile = parentView.findViewById(R.id.tv_edit);
        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        final FirebaseUser mFirebaseUser = mAuth.getCurrentUser();

        myRef = FirebaseDatabase.getInstance().getReference(Constants.Users).child(Constants.All_Users).child(mFirebaseUser.getUid());

        final TextView userName = parentView.findViewById(R.id.tv_title);
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

                    userName.setText(u.getUserName());
                    progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if (mMediatorInterface !=null){
                    mMediatorInterface.changeFragmentTo(new EditAppOwnerProfileFragment(), EditAppOwnerProfileFragment.class.getSimpleName());
                }*/

                navController.navigate(R.id.action_appOwnerProfileFragment_to_editAppOwnerProfileFragment);

            }
        });

        confirmPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* if (mMediatorInterface !=null){
                    mMediatorInterface.changeFragmentTo(new AOConfirmSalonsFragment(), AOConfirmSalonsFragment.class.getSimpleName());
                }*/

                navController.navigate(R.id.action_appOwnerProfileFragment_to_AOConfirmSalonsFragment);


            }
        });


        return parentView;
    }
}