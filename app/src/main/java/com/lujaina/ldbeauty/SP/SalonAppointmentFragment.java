package com.lujaina.ldbeauty.SP;

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
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.ClientsAppointmentModel;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;


public class SalonAppointmentFragment extends Fragment {
    FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
    private FirebaseDatabase mDatabase;

    private Context mContext;
    private MediatorInterface mMediatorInterface;
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
        View parentView = inflater.inflate(R.layout.fragment_salon_appointment, container, false);
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
        recyclerView.setAdapter(mAdapter);

        mAdapter.setupOnItemClickListener(new SalonAppointmentAdapter.onItemClickListener() {
            @Override
            public void onItemClick(ClientsAppointmentModel info) {

                AppointmentDialog dialog = new AppointmentDialog();
                dialog.setInfo(info);
                dialog.show(getChildFragmentManager(), AppointmentDialog.class.getSimpleName());
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediatorInterface != null) {
                    mMediatorInterface.onBackPressed();
                }
            }
        });

        return parentView;
    }

    private void readSalonAppointmentFromFireBaseDB() {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid()).child(Constants.History_Order);
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(true);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_bar);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

}