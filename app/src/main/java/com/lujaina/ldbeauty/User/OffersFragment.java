package com.lujaina.ldbeauty.User;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lujaina.ldbeauty.Adapters.UserOfferAdapter;
import com.lujaina.ldbeauty.Client.CartFragment;
import com.lujaina.ldbeauty.Client.OfferAppointmentFragment;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Dialogs.NoLoginDialogFragment;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.OfferModel;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;


public class OffersFragment extends Fragment {

    private FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
    private DatabaseReference myRef;

    private ArrayList<OfferModel> offersList;
    private UserOfferAdapter mAdapter;

    private MediatorInterface mMediatorInterface;
    private Context mContext;
    private ProgressDialog progressDialog;

    private OfferModel offer;
    private SPRegistrationModel ownerId;

    String userRole;

    public OffersFragment() {
        // Required empty public constructor
    }

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
        View parentView = inflater.inflate(R.layout.fragment_add_salon_offers, container, false);
        mAuth = FirebaseAuth.getInstance();
        FloatingActionButton add = parentView.findViewById(R.id.add_button);
        TextView cart = parentView.findViewById(R.id.tv_cart);
        cart.setVisibility(View.VISIBLE);
        add.setVisibility(View.INVISIBLE);
        ImageButton back = parentView.findViewById(R.id.ib_back);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        if(mFirebaseUser!= null){
            checkUserRole(mFirebaseUser.getUid());
        }
        RecyclerView recyclerView = parentView.findViewById(R.id.recyclerView);
        offersList = new ArrayList<>();
        mAdapter = new UserOfferAdapter(mContext);
        recyclerView.setAdapter(mAdapter);
        setupRecyclerView(recyclerView);
        readSalonOffersFromFirebaseDB();

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFirebaseUser != null && !userRole.equals("Salon Owner")){
                    if(mMediatorInterface != null){
                        mMediatorInterface.changeFragmentTo(new CartFragment(), CartFragment.class.getSimpleName());
                    }
                }else{
                    NoLoginDialogFragment dialog = new NoLoginDialogFragment();
                    dialog.show(getChildFragmentManager(),NoLoginDialogFragment.class.getSimpleName());
                }


            }
        });

        mAdapter.setonClickListener(new UserOfferAdapter.onClickListener() {
            @Override
            public void onClick(OfferModel offerModel) {
                if(mFirebaseUser == null || userRole.equals("Salon Owner")){
                    NoLoginDialogFragment dialog = new NoLoginDialogFragment();
                    dialog.show(getChildFragmentManager(),NoLoginDialogFragment.class.getSimpleName());
                }else{
                    if(mMediatorInterface != null){
                        OfferAppointmentFragment offerAppointmentFragment = new OfferAppointmentFragment();
                        offerAppointmentFragment.setOfferId(offerModel);
                        mMediatorInterface.changeFragmentTo(offerAppointmentFragment, OfferAppointmentFragment.class.getSimpleName());
                    }
                }

            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediatorInterface.onBackPressed();
            }
        });

        return parentView;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    private void readSalonOffersFromFirebaseDB() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(ownerId.getUserId()).child(Constants.Salon_Offers);
        // Read from the mDatabase
        progressDialog = new ProgressDialog(mContext);
        progressDialog.show();
        progressDialog.setCancelable(true);
        progressDialog.setContentView(R.layout.progress_bar);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                offersList.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    offer = d.getValue(OfferModel.class);
                    offersList.add(offer);

                }
                progressDialog.dismiss();
                mAdapter.update(offersList);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                progressDialog.dismiss();
            }
        });
    }
    public void setSalonOffers(SPRegistrationModel section) {
        ownerId = section;
    }

    private void checkUserRole(String uid) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef;
        myRef = (DatabaseReference) database.getReference(Constants.Users).child(Constants.All_Users).child(uid);

        myRef.orderByChild(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                SPRegistrationModel model = snapshot.getValue(SPRegistrationModel.class);
                model.getUserType();
                mAuth.getCurrentUser();
                if(mAuth!=null) {
                    //Get Currrent User info from Firebase Database
                    SPRegistrationModel currentUser = SPRegistrationModel.getInstance();
                    currentUser.setUserName(model.getUserName());
                    currentUser.setUserEmail(model.getUserEmail());
                    currentUser.setUserId(model.getUserId());
                    currentUser.setUserType(model.getUserType());
                     userRole = model.getUserType();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}