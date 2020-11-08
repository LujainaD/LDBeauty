package com.lujaina.ldbeauty.Client;

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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

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

        ImageButton ibBack 	= parentView.findViewById(R.id.ib_back);
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

        TextView pay = parentView.findViewById(R.id.tv_pay);

        RecyclerView recyclerView = parentView.findViewById(R.id.rv_cart_offers);
        RecyclerView cart_rv = parentView.findViewById(R.id.rv_cart);


        offersArray = new ArrayList<>();
        serviceArray = new ArrayList<>();

        offerAdapter = new CartOffersAdapter(mContext);
        serviceAdapter = new CartServicesAdapter(mContext);

        recyclerView.setAdapter(offerAdapter);
        cart_rv.setAdapter(serviceAdapter);

        setupRecyclerView(recyclerView);
        setupRecyclerView(cart_rv);

        readClientOffersAppointmentDB();
        readClientServiceAppointmentDB();




        offerAdapter.setonClickListener(new CartOffersAdapter.onDeleteListener() {
            @Override
            public void onDelete( ClientsAppointmentModel model) {
                deleteOfferAppointmentFromCart(model);
            }
        });


        return parentView;
    }



    private void readClientOffersAppointmentDB() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid()).child(Constants.Clients_Offers_Appointments);
        // Read from the mDatabase
        progressDialog = new ProgressDialog(mContext);
        progressDialog.show();
        progressDialog.setCancelable(true);
        progressDialog.setContentView(R.layout.progress_bar);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                offersArray.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    ClientsAppointmentModel appointment = d.getValue(ClientsAppointmentModel.class);
                    offersArray.add(appointment);

                }
                progressDialog.dismiss();
                offerAdapter.update(offersArray);
/*
                displayTotal(offersArray);
*/

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                progressDialog.dismiss();
            }
        });
    }

    private void displayTotal(ArrayList<ClientsAppointmentModel> array) {
        int total = 0;
        for(ClientsAppointmentModel model : array){
            total += Integer.parseInt(model.getPrice());
        }
        tv_total.setText(total);
    }

    private void  readClientServiceAppointmentDB(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid()).child(Constants.Clients_Service_Appointments);
        // Read from the mDatabase
        progressDialog = new ProgressDialog(mContext);
        progressDialog.show();
        progressDialog.setCancelable(true);
        progressDialog.setContentView(R.layout.progress_bar);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                serviceArray.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    ClientsAppointmentModel appointment = d.getValue(ClientsAppointmentModel.class);
                    serviceArray.add(appointment);

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
