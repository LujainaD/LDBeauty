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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lujaina.ldbeauty.Adapters.UserServiceAdapter;
import com.lujaina.ldbeauty.Client.OfferAppointmentFragment;
import com.lujaina.ldbeauty.Client.ServiceAppointmentFragment;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Dialogs.NoLoginDialogFragment;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.CategoryModel;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.Models.ServiceModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;


public class ServicesFragment extends Fragment {
    private FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
    private DatabaseReference myRef;

    private CategoryModel serviceId;
    private MediatorInterface mMediatorCallback;
    private Context mContext;

    private ArrayList<ServiceModel> serviceList;
    private UserServiceAdapter mAdapter;


    private ProgressDialog progressDialog;

    private ServiceModel service;
    private CategoryModel category;
    String userRole;
    TextView empty;
    RecyclerView recyclerView;

    public ServicesFragment() {
        // Required empty public constructor
    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof MediatorInterface) {
            mMediatorCallback = (MediatorInterface) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement MediatorInterface");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_services, container, false);
        empty = parentView.findViewById(R.id.tv_empty);
        ImageView img = parentView.findViewById(R.id.iv_category);
        ImageButton back = parentView.findViewById(R.id.ib_back);
        TextView categoryTitle = parentView.findViewById(R.id.tv_categoryTitle);
         recyclerView = parentView.findViewById(R.id.recyclerView);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        if(mFirebaseUser!= null){
            checkUserRole(mFirebaseUser.getUid());
        }
        serviceList = new ArrayList<>();
        mAdapter = new UserServiceAdapter(mContext);
        recyclerView.setAdapter(mAdapter);
        setupRecyclerView(recyclerView);
        readSalonInfoFromFirebaseDB();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediatorCallback != null){
                    mMediatorCallback.onBackPressed();
                }
            }
        });

        if(serviceId != null){
            Glide.with(mContext).load(serviceId.getCategoryURL()).into(img);
            categoryTitle.setText(serviceId.getCategoryTitle());
        }

        mAdapter.setOnBookClickListener(new UserServiceAdapter.onBookClickListener() {
            @Override
            public void onBookClick(ServiceModel service) {
                if(!mFirebaseUser.isEmailVerified()) {
                    Toast.makeText(mContext, "Please verify your email address first", Toast.LENGTH_SHORT).show();
                }if(mFirebaseUser == null || userRole.equals("Salon Owner")){
                    NoLoginDialogFragment dialog = new NoLoginDialogFragment();
                    dialog.showText(2);
                    dialog.show(getChildFragmentManager(),NoLoginDialogFragment.class.getSimpleName());
                }else {
                    if(mMediatorCallback != null){
                        ServiceAppointmentFragment serviceAppointmentFragment = new ServiceAppointmentFragment();
                        serviceAppointmentFragment.setServiceID(service);
                        mMediatorCallback.changeFragmentTo(serviceAppointmentFragment, OfferAppointmentFragment.class.getSimpleName());
                    }
                }
            }
        });


        return parentView;
    }

    private void readSalonInfoFromFirebaseDB() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(serviceId.getOwnerId()).child(Constants.Salon_Category).child(serviceId.getCategoryId()).child(Constants.Salon_Service);
        // Read from the mDatabase
        progressDialog = new ProgressDialog(mContext);
        progressDialog.show();
        progressDialog.setCancelable(true);
        progressDialog.setContentView(R.layout.progress_bar);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        recyclerView.setVisibility(View.GONE);
        empty.setVisibility(View.VISIBLE);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                serviceList.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    service = d.getValue(ServiceModel.class);
                    serviceList.add(service);
                    recyclerView.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                }
                progressDialog.dismiss();
                mAdapter.update(serviceList);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                progressDialog.dismiss();
            }
        });
    }

    private void setupRecyclerView(RecyclerView recyclerView) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    public void setServiceID(CategoryModel category) {
        serviceId = category;
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