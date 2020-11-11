package com.lujaina.ldbeauty.Client;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lujaina.ldbeauty.Adapters.CartServicesAdapter;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.HomeActivity;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.ClientsAppointmentModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;


public class CartFragment extends Fragment {
    FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
    ProgressDialog progressDialog;

    private Context mContext;
    private MediatorInterface mMediatorInterface;

    private CartServicesAdapter serviceAdapter;
    ArrayList<ClientsAppointmentModel> serviceArray;
    TextView tv_total;
    TextView emptyservice;
    RecyclerView service_rv;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
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
        View parentView = inflater.inflate(R.layout.fragment_cart, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav);
        navBar.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(mContext);
        progressDialog.show();
        progressDialog.setCancelable(true);
        progressDialog.setContentView(R.layout.progress_bar);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ImageButton ibBack = parentView.findViewById(R.id.ib_back);
        TextView pay = parentView.findViewById(R.id.tv_pay);
        emptyservice = parentView.findViewById(R.id.tv_emptyServices);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();


        tv_total = parentView.findViewById(R.id.tv_totalPrice);

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediatorInterface != null) {
                    mMediatorInterface.onBackPressed();
                }
            }
        });

        service_rv = parentView.findViewById(R.id.rv_cart);
        serviceArray = new ArrayList<>();
        serviceAdapter = new CartServicesAdapter(mContext);
        service_rv.setAdapter(serviceAdapter);
        setupRecyclerView(service_rv);

        readClientServiceAppointmentDB();


        serviceAdapter.setonClickListener(new CartServicesAdapter.onDeleteListener() {
            @Override
            public void onDelete(ClientsAppointmentModel model) {
                deleteServiceAppointmentFromCart(model);

            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediatorInterface != null){
                    mMediatorInterface.changeFragmentTo(new PaymentFragment(), PayPalFragment.class.getSimpleName());
                }

            }
        });


        return parentView;
    }


    private void deleteServiceAppointmentFromCart(ClientsAppointmentModel model) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid())
                .child(Constants.Clients_Appointments).child(model.getAppointmentID());
        DatabaseReference salonRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(model.getOwnerId())
                .child(Constants.Clients_Appointments).child(model.getAppointmentID());
        myRef.removeValue();
        salonRef.removeValue();
    }


    private void readClientServiceAppointmentDB() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid()).child(Constants.Clients_Appointments);
        // Read from the mDatabase

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                serviceArray.clear();
                int sum = 0;
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    ClientsAppointmentModel appointment = d.getValue(ClientsAppointmentModel.class);
                    serviceArray.add(appointment);
                    int price = Integer.parseInt(appointment.getPrice());
                    sum += price;
                    String sumOfService = String.valueOf(sum);
                    if (serviceArray.isEmpty()) {
                        emptyservice.setVisibility(View.VISIBLE);
                        service_rv.setVisibility(View.GONE);


                    } else {
                        service_rv.setVisibility(View.VISIBLE);
                        emptyservice.setVisibility(View.GONE);

                    }

                    tv_total.setText("Total: " + sumOfService + " R.O");


                }
                progressDialog.dismiss();
                serviceAdapter.update(serviceArray);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                progressDialog.dismiss();
            }
        });
    }


    private void setupRecyclerView(RecyclerView recyclerView) {
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 2);
        //LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

}
