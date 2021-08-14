package com.lujaina.ldbeauty.Client;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
        if (navBar != null) {
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
                    title.setText(R.string.Cart_Warning);
                    title.setBackgroundColor(Color.parseColor("#DA6EA4"));
                    // title.setPadding(10, 15, 15, 10);
                    title.setGravity(Gravity.CENTER);
                    title.setTextColor(Color.WHITE);
                    title.setTextSize(22);

                    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                    alertDialog.setCustomTitle(title);
                    alertDialog.setMessage(getString(R.string.empty_cart));
                    alertDialog.show();
                } else {
                    if (isAllSlotsAreAvailable()) {
                        Intent intent = new Intent(getContext(), PayActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("total", tv_total.getText().toString());
                        bundle.putString("ownerId", ownerId);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        TextView textView = new TextView(mContext);
                        textView.setText(R.string.booked);
                        textView.setBackgroundColor(Color.parseColor("#DA6EA4"));
                        // title.setPadding(10, 15, 15, 10);
                        textView.setGravity(Gravity.CENTER);
                        textView.setTextColor(Color.WHITE);
                        textView.setTextSize(22);
                        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                        alertDialog.setCustomTitle(textView);
                        alertDialog.setMessage(getString(R.string.booked_appointment));
                        alertDialog.show();
                    }
                }
            }
        });

        return parentView;
    }

    private boolean isAllSlotsAreAvailable() {
        for (ClientsAppointmentModel model : serviceArray) {
            if (model.getSoldStatus()!=null && model.getSoldStatus().equals("booked")) {
                return false;
            }
        }
        return true;
    }


    private void deleteServiceAppointmentFromCart(ClientsAppointmentModel model) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid())
                .child(Constants.Client_Cart).child(model.getAppointmentID());
        myRef.removeValue();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commit();
    }

    private void readClientServiceAppointmentDB() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid()).child(Constants.Client_Cart);
        // Read from the mDatabase

        progressDialog.show();
        emptyservice.setVisibility(View.VISIBLE);
        service_rv.setVisibility(View.GONE);

        myRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
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

                    tv_total.setText(getString(R.string.Total) + sumOfService + getString(R.string.R_O));

                    progressDialog.dismiss();
                    if (appointment.getOwnerId() != null) {
                        checkServiceAlreadybooked(appointment.getOwnerId());

                    }
                }

                //serviceAdapter.update(serviceArray);

            }


            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                progressDialog.dismiss();
            }
        });
    }

    private void checkServiceAlreadybooked(String allOwnerId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(allOwnerId).child(Constants.History_Order);


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    ClientsAppointmentModel appointment = d.getValue(ClientsAppointmentModel.class);
                    ArrayList<ClientsAppointmentModel> models = new ArrayList<>();
                    models.add(appointment);
                    // serviceArray.add(appointment);

                    String time = d.child("appointmentTime").getValue(String.class);
                    String date = d.child("appointmentDate").getValue(String.class);

                    for (ClientsAppointmentModel cartArray : serviceArray) {
                        for (ClientsAppointmentModel model : models) {
                            if (cartArray.getOfferId() != null) {
                                if (model.getOfferId() != null) {
                                    if (model.getOfferId().equals(cartArray.getOfferId())) {
                                        if (cartArray.getAppointmentDate().equals(date)) {
                                            if (cartArray.getAppointmentTime().equals(time)) {
                                                int positon = serviceArray.indexOf(cartArray);
                                                cartArray.setSoldStatus("booked");
                                                serviceArray.set(positon, cartArray);
                                            }
                                        }
                                    }
                                }

                            } else if (cartArray.getServiceId() != null) {
                                if (model.getServiceId() != null) {
                                    if (model.getServiceId().equals(cartArray.getServiceId())) {
                                        if (cartArray.getAppointmentDate().equals(date)) {
                                            if (cartArray.getAppointmentTime().equals(time)) {
                                                int positon = serviceArray.indexOf(cartArray);
                                                cartArray.setSoldStatus("booked");
                                                serviceArray.set(positon, cartArray);
                                            }
                                        }
                                    }
                                }


                            } else {

                            }
                        }
                    }

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
