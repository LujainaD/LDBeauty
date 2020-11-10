package com.lujaina.ldbeauty.Client;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import com.lujaina.ldbeauty.Adapters.CartOffersAdapter;
import com.lujaina.ldbeauty.Adapters.CartServicesAdapter;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.ClientsAppointmentModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;
import java.util.Objects;


public class CartFragment extends Fragment {
    FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    private Context mContext;
    private MediatorInterface mMediatorInterface;
    ProgressDialog progressDialog;

    private CartOffersAdapter offerAdapter;
    private CartServicesAdapter serviceAdapter;

    ArrayList<ClientsAppointmentModel> offersArray;
    ArrayList<ClientsAppointmentModel> serviceArray;

    TextView tv_total;
    String sumOfService;
    String sumOfOffers;
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
        ImageButton ibBack 	= parentView.findViewById(R.id.ib_back);
        TextView pay = parentView.findViewById(R.id.tv_pay);
         emptyservice = parentView.findViewById(R.id.tv_emptyServices);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference(Constants.Users).child(Constants.Salon_Owner);
         tv_total = parentView.findViewById(R.id.tv_totalPrice);

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediatorInterface != null){
                    mMediatorInterface.onBackPressed();
                }
            }
        });


        RecyclerView offers_rv = parentView.findViewById(R.id.rv_cart_offers);
         service_rv = parentView.findViewById(R.id.rv_cart);


        offersArray = new ArrayList<>();
        serviceArray = new ArrayList<>();

        offerAdapter = new CartOffersAdapter(mContext);
        serviceAdapter = new CartServicesAdapter(mContext);

        offers_rv.setAdapter(offerAdapter);
       service_rv.setAdapter(serviceAdapter);

        setupRecyclerView(offers_rv);
        setupRecyclerView(service_rv);

        readClientOffersAppointmentDB();
        readClientServiceAppointmentDB();


        displayTotal(sumOfService, sumOfOffers);

        serviceAdapter.setonClickListener(new CartServicesAdapter.onDeleteListener() {
            @Override
            public void onDelete(ClientsAppointmentModel model) {
                deleteServiceAppointmentFromCart(model);

            }
        });

        offerAdapter.setonClickListener(new CartOffersAdapter.onDeleteListener() {
            @Override
            public void onDelete( ClientsAppointmentModel model) {
                deleteOfferAppointmentFromCart(model);
            }
        });


        return parentView;
    }

    private void displayTotal(String sumOfService, String sumOfOffers) {
        tv_total.setText(sumOfOffers+sumOfService);

    }

    private void deleteServiceAppointmentFromCart(ClientsAppointmentModel model) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid())
                .child(Constants.Clients_Service_Appointments).child(model.getAppointmentID());
        DatabaseReference salonRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(model.getOwnerId())
                .child(Constants.Clients_Service_Appointments).child(model.getAppointmentID());
        myRef.removeValue();
        salonRef.removeValue();
    }

    private void readClientOffersAppointmentDB() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid()).child(Constants.Clients_Offers_Appointments);
        // Read from the mDatabase
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                offersArray.clear();
                int sum = 0;

                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    ClientsAppointmentModel appointment = d.getValue(ClientsAppointmentModel.class);
                    offersArray.add(appointment);


                    int price = Integer.parseInt(appointment.getPrice());
                          sum +=price;
                    sumOfOffers = String.valueOf(sum);
                    Log.d("offers", sumOfOffers);

                }
                progressDialog.dismiss();
                offerAdapter.update(offersArray);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                progressDialog.dismiss();
            }
        });
    }

    private void  readClientServiceAppointmentDB(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid()).child(Constants.Clients_Service_Appointments);
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
                    sum +=price;
                     sumOfService = String.valueOf(sum);
                    if(serviceArray.isEmpty()){
                        emptyservice.setVisibility(View.VISIBLE);
                        service_rv.setVisibility(View.GONE);
                    }else {
                        service_rv.setVisibility(View.VISIBLE);
                        emptyservice.setVisibility(View.GONE);

                    }
                    Log.d("service", sumOfService);
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    private void deleteOfferAppointmentFromCart(ClientsAppointmentModel model) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid())
                .child(Constants.Clients_Offers_Appointments).child(model.getAppointmentID());
        DatabaseReference salonRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(model.getOwnerId())
                .child(Constants.Clients_Offers_Appointments).child(model.getAppointmentID());
        myRef.removeValue();
        salonRef.removeValue();


    }
}
