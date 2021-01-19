package com.lujaina.ldbeauty.Client;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
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
import com.lujaina.ldbeauty.Models.ClientsAppointmentModel;
import com.lujaina.ldbeauty.PayActivity;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;


public class CartFragment extends Fragment {
    FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
    ProgressDialog progressDialog;

    private Context mContext;

    private CartServicesAdapter serviceAdapter;
    ArrayList<ClientsAppointmentModel> serviceArray;
    TextView tv_total;
    TextView emptyservice;
    RecyclerView service_rv;
    String ownerId;
    NavController navController;

    public CartFragment() {
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
        View parentView = inflater.inflate(R.layout.fragment_cart, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav);
        if(navBar!= null){
            navBar.setVisibility(View.GONE);

        }


        progressDialog = new ProgressDialog(mContext);
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
                navController.popBackStack();
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
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View v) {
                    if (tv_total.getText().toString().equals("0")) {

                        TextView title = new TextView(mContext);
                        title.setText("Cart Warning");
                        title.setBackgroundColor(Color.parseColor("#DA6EA4"));
                       // title.setPadding(10, 15, 15, 10);
                        title.setGravity(Gravity.CENTER);
                        title.setTextColor(Color.WHITE);
                        title.setTextSize(22);

                        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                        alertDialog.setCustomTitle(title);
                        alertDialog.setMessage("Your cart is empty");
                        alertDialog.show();

                    }else {
                        /*if (mMediatorInterface != null) {
                            PaymentFragment paymentFragment = new PaymentFragment();
                            paymentFragment.setTotalPrice(tv_total.getText().toString(), serviceArray, ownerId);
                            mMediatorInterface.changeFragmentTo(paymentFragment, PayPalFragment.class.getSimpleName());
                        }*/

                        Intent intent = new Intent(getContext(), PayActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("total", tv_total.getText().toString());
                        bundle.putString("ownerId",ownerId);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        getActivity().finish();
                        // navController.navigate(R.id.action_selectedSalonFragment2_to_categoriesFragment, bundle);
                    }
                }
            });

        return parentView;
    }


    private void deleteServiceAppointmentFromCart(ClientsAppointmentModel model) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid())
                .child(Constants.Client_Cart).child(model.getAppointmentID());
        myRef.removeValue();
    }

    private void readClientServiceAppointmentDB() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid()).child(Constants.Client_Cart);
        // Read from the mDatabase

        progressDialog.show();
            emptyservice.setVisibility(View.VISIBLE);
            service_rv.setVisibility(View.GONE);

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
                     ownerId = appointment.getOwnerId();

                        service_rv.setVisibility(View.VISIBLE);
                        emptyservice.setVisibility(View.GONE);

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
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

}
