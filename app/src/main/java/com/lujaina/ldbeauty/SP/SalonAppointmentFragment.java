package com.lujaina.ldbeauty.SP;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lujaina.ldbeauty.Adapters.ConfirmAdapter;
import com.lujaina.ldbeauty.Adapters.SalonAppointmentAdapter;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Dialogs.AppointmentDialog;
import com.lujaina.ldbeauty.Dialogs.ConfirmDialogFragment;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.AppointmentModel;
import com.lujaina.ldbeauty.Models.ClientsAppointmentModel;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;


public class SalonAppointmentFragment extends Fragment implements ConfirmDialogFragment.selectedButton{
    FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
    private FirebaseDatabase mDatabase;

    private Context mContext;
    ProgressDialog progressDialog;

    RecyclerView recyclerView;
    private SalonAppointmentAdapter mAdapter;
    ArrayList<ClientsAppointmentModel> salonAppointmentArray;
    TextView emptyAppointment;

    public SalonAppointmentFragment() {
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
        View parentView = inflater.inflate(R.layout.fragment_salon_appointment, container, false);
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        ImageButton back = parentView.findViewById(R.id.ib_back);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        emptyAppointment = parentView.findViewById(R.id.empty);
        recyclerView = parentView.findViewById(R.id.rv_confirm);
        salonAppointmentArray = new ArrayList<>();

        setupRecyclerView(recyclerView);
        readSalonAppointmentFromFireBaseDB();
        mAdapter = new SalonAppointmentAdapter(mContext);
        mAdapter.setupOnItemClickListener(new SalonAppointmentAdapter.onItemClickListener() {
            @Override
            public void onItemClick(ClientsAppointmentModel salonsDetails) {
                AppointmentDialog dialog = new AppointmentDialog();
                dialog.setInfo(salonsDetails);
                dialog.show(getChildFragmentManager(), AppointmentDialog.class.getSimpleName());
            }

            @Override
            public void onConfirm(ClientsAppointmentModel confirm) {
                ConfirmDialogFragment dialogFragment = new ConfirmDialogFragment(SalonAppointmentFragment.this);
                dialogFragment.sendInfo(confirm);
                dialogFragment.show(getChildFragmentManager(),ConfirmDialogFragment.class.getSimpleName() );
                //confirmAppointment(confirm);
            }

            @Override
            public void onDecline(ClientsAppointmentModel decline) {
                ConfirmDialogFragment dialogFragment = new ConfirmDialogFragment(SalonAppointmentFragment.this);
                dialogFragment.sendInfo(decline);
                dialogFragment.show(getChildFragmentManager(),ConfirmDialogFragment.class.getSimpleName() );
            }
        });
        recyclerView.setAdapter(mAdapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               navController.popBackStack();
            }
        });

        return parentView;
    }

    private void declineAppointment(ClientsAppointmentModel decline) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child(Constants.Users).child(Constants.Salon_Owner).child(decline.getOwnerId()).child(Constants.History_Order).child(decline.getAppointmentID());
        DatabaseReference clientRef = database.getReference().child(Constants.Users).child(Constants.Client).child(decline.getUserId()).child(Constants.History_Order).child(decline.getAppointmentID());

        if(decline.getServiceType()=="Service"){
            myRef.child("appointmentDate").setValue(decline.getAppointmentDate());
            myRef.child("appointmentID").setValue(decline.getAppointmentID());
            myRef.child("appointmentStatus").setValue("Decline");
            myRef.child("appointmentTime").setValue(decline.getAppointmentTime());
            myRef.child("clientName").setValue(decline.getClientName());
            myRef.child("clientPhone").setValue(decline.getClientPhone());
            myRef.child("orderDate").setValue(decline.getOrderDate());
            myRef.child("ownerId").setValue(decline.getOwnerId());
            myRef.child("price").setValue(decline.getPrice());
            myRef.child("salonName").setValue(decline.getSalonName());
            myRef.child("serviceId").setValue(decline.getServiceId());
            myRef.child("serviceTitle").setValue(decline.getServiceTitle());
            myRef.child("serviceType").setValue(decline.getServiceType());
            myRef.child("specialList").setValue(decline.getSpecialList());
            myRef.child("userId").setValue(decline.getUserId());

            clientRef.child("appointmentDate").setValue(decline.getAppointmentDate());
            clientRef.child("appointmentID").setValue(decline.getAppointmentID());
            clientRef.child("appointmentStatus").setValue("Decline");
            clientRef.child("appointmentTime").setValue(decline.getAppointmentTime());
            clientRef.child("clientName").setValue(decline.getClientName());
            clientRef.child("clientPhone").setValue(decline.getClientPhone());
            clientRef.child("orderDate").setValue(decline.getOrderDate());
            clientRef.child("ownerId").setValue(decline.getOwnerId());
            clientRef.child("price").setValue(decline.getPrice());
            clientRef.child("salonName").setValue(decline.getSalonName());
            clientRef.child("serviceId").setValue(decline.getServiceId());
            clientRef.child("serviceTitle").setValue(decline.getServiceTitle());
            clientRef.child("serviceType").setValue(decline.getServiceType());
            clientRef.child("specialList").setValue(decline.getSpecialList());
            clientRef.child("userId").setValue(decline.getUserId());
        }else {
            myRef.child("appointmentDate").setValue(decline.getAppointmentDate());
            myRef.child("appointmentID").setValue(decline.getAppointmentID());
            myRef.child("appointmentStatus").setValue("Decline");
            myRef.child("appointmentTime").setValue(decline.getAppointmentTime());
            myRef.child("clientName").setValue(decline.getClientName());
            myRef.child("clientPhone").setValue(decline.getClientPhone());
            myRef.child("orderDate").setValue(decline.getOrderDate());
            myRef.child("ownerId").setValue(decline.getOwnerId());
            myRef.child("price").setValue(decline.getPrice());
            myRef.child("salonName").setValue(decline.getSalonName());
            myRef.child("offerId").setValue(decline.getOfferId());
            myRef.child("offerServices").setValue(decline.getOfferServices());
            myRef.child("serviceType").setValue(decline.getServiceType());
            myRef.child("offerTitle").setValue(decline.getOfferTitle());
            myRef.child("userId").setValue(decline.getUserId());

            clientRef.child("appointmentDate").setValue(decline.getAppointmentDate());
            clientRef.child("appointmentID").setValue(decline.getAppointmentID());
            clientRef.child("appointmentStatus").setValue("Decline");
            clientRef.child("appointmentTime").setValue(decline.getAppointmentTime());
            clientRef.child("clientName").setValue(decline.getClientName());
            clientRef.child("clientPhone").setValue(decline.getClientPhone());
            clientRef.child("orderDate").setValue(decline.getOrderDate());
            clientRef.child("ownerId").setValue(decline.getOwnerId());
            clientRef.child("price").setValue(decline.getPrice());
            clientRef.child("salonName").setValue(decline.getSalonName());
            clientRef.child("offerId").setValue(decline.getOfferId());
            clientRef.child("offerServices").setValue(decline.getOfferServices());
            clientRef.child("serviceType").setValue(decline.getServiceType());
            clientRef.child("offerTitle").setValue(decline.getOfferTitle());
            clientRef.child("userId").setValue(decline.getUserId());
        }

    }

    private void confirmAppointment(ClientsAppointmentModel confirm) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child(Constants.Users).child(Constants.Salon_Owner).child(confirm.getOwnerId()).child(Constants.History_Order).child(confirm.getAppointmentID());
        DatabaseReference clientRef = database.getReference().child(Constants.Users).child(Constants.Client).child(confirm.getUserId()).child(Constants.History_Order).child(confirm.getAppointmentID());

        if(confirm.getServiceType()=="Service"){
            myRef.child("appointmentDate").setValue(confirm.getAppointmentDate());
            myRef.child("appointmentID").setValue(confirm.getAppointmentID());
            myRef.child("appointmentStatus").setValue("Confirm");
            myRef.child("appointmentTime").setValue(confirm.getAppointmentTime());
            myRef.child("clientName").setValue(confirm.getClientName());
            myRef.child("clientPhone").setValue(confirm.getClientPhone());
            myRef.child("orderDate").setValue(confirm.getOrderDate());
            myRef.child("ownerId").setValue(confirm.getOwnerId());
            myRef.child("price").setValue(confirm.getPrice());
            myRef.child("salonName").setValue(confirm.getSalonName());
            myRef.child("serviceId").setValue(confirm.getServiceId());
            myRef.child("serviceTitle").setValue(confirm.getServiceTitle());
            myRef.child("serviceType").setValue(confirm.getServiceType());
            myRef.child("specialList").setValue(confirm.getSpecialList());
            myRef.child("userId").setValue(confirm.getUserId());


            clientRef.child("appointmentDate").setValue(confirm.getAppointmentDate());
            clientRef.child("appointmentID").setValue(confirm.getAppointmentID());
            clientRef.child("appointmentStatus").setValue("Confirm");
            clientRef.child("appointmentTime").setValue(confirm.getAppointmentTime());
            clientRef.child("clientName").setValue(confirm.getClientName());
            clientRef.child("clientPhone").setValue(confirm.getClientPhone());
            clientRef.child("orderDate").setValue(confirm.getOrderDate());
            clientRef.child("ownerId").setValue(confirm.getOwnerId());
            clientRef.child("price").setValue(confirm.getPrice());
            clientRef.child("salonName").setValue(confirm.getSalonName());
            clientRef.child("serviceId").setValue(confirm.getServiceId());
            clientRef.child("serviceTitle").setValue(confirm.getServiceTitle());
            clientRef.child("serviceType").setValue(confirm.getServiceType());
            clientRef.child("specialList").setValue(confirm.getSpecialList());
            clientRef.child("userId").setValue(confirm.getUserId());

        }else {
            myRef.child("appointmentDate").setValue(confirm.getAppointmentDate());
            myRef.child("appointmentID").setValue(confirm.getAppointmentID());
            myRef.child("appointmentStatus").setValue("Confirm");
            myRef.child("appointmentTime").setValue(confirm.getAppointmentTime());
            myRef.child("clientName").setValue(confirm.getClientName());
            myRef.child("clientPhone").setValue(confirm.getClientPhone());
            myRef.child("orderDate").setValue(confirm.getOrderDate());
            myRef.child("ownerId").setValue(confirm.getOwnerId());
            myRef.child("price").setValue(confirm.getPrice());
            myRef.child("salonName").setValue(confirm.getSalonName());
            myRef.child("offerId").setValue(confirm.getOfferId());
            myRef.child("offerServices").setValue(confirm.getOfferServices());
            myRef.child("serviceType").setValue(confirm.getServiceType());
            myRef.child("offerTitle").setValue(confirm.getOfferTitle());
            myRef.child("userId").setValue(confirm.getUserId());

            clientRef.child("appointmentDate").setValue(confirm.getAppointmentDate());
            clientRef.child("appointmentID").setValue(confirm.getAppointmentID());
            clientRef.child("appointmentStatus").setValue("Confirm");
            clientRef.child("appointmentTime").setValue(confirm.getAppointmentTime());
            clientRef.child("clientName").setValue(confirm.getClientName());
            clientRef.child("clientPhone").setValue(confirm.getClientPhone());
            clientRef.child("orderDate").setValue(confirm.getOrderDate());
            clientRef.child("ownerId").setValue(confirm.getOwnerId());
            clientRef.child("price").setValue(confirm.getPrice());
            clientRef.child("salonName").setValue(confirm.getSalonName());
            clientRef.child("offerId").setValue(confirm.getOfferId());
            clientRef.child("offerServices").setValue(confirm.getOfferServices());
            clientRef.child("serviceType").setValue(confirm.getServiceType());
            clientRef.child("offerTitle").setValue(confirm.getOfferTitle());
            clientRef.child("userId").setValue(confirm.getUserId());

        }
    }

    private void readSalonAppointmentFromFireBaseDB() {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid()).child(Constants.History_Order);
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(true);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_bar);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                salonAppointmentArray = new ArrayList<>();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    ClientsAppointmentModel salon = d.getValue(ClientsAppointmentModel.class);
                    salonAppointmentArray.add(salon);

                    if(salonAppointmentArray== null){
                        recyclerView.setVisibility(View.GONE);
                        emptyAppointment.setVisibility(View.VISIBLE);
                    }else {
                        emptyAppointment.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                }
                progressDialog.dismiss();
                mAdapter.update(salonAppointmentArray);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();

            }
        });

    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }
    @Override
    public void onButtonSelected(int confirmOrDecline, ClientsAppointmentModel information) {
        if(confirmOrDecline == 1){
            confirmAppointment(information);

        }else {
            declineAppointment(information);
        }
    }
}