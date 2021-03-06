package com.lujaina.ldbeauty.AppOwner;

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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lujaina.ldbeauty.Adapters.ConfirmAdapter;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Dialogs.SalonConfirmDialogFragment;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;


public class AOConfirmSalonsFragment extends Fragment implements SalonConfirmDialogFragment.statusConfirmed {
    FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
    private FirebaseDatabase mDatabase;

    private Context mContext;
    ProgressDialog progressDialog;

    RecyclerView recyclerView;
    TextView empty;

    private ConfirmAdapter mAdapter;
    ArrayList<SPRegistrationModel> salonNamesArray;
    NavController navController;

    public AOConfirmSalonsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
       /* if(context instanceof MediatorInterface) {
            mMediatorInterface = (MediatorInterface) context;
        }
        else{
            throw new RuntimeException(context.toString()+ "must implement MediatorInterface");
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView= inflater.inflate(R.layout.fragment_ao_confirm_salons, container, false);

        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        empty = parentView.findViewById(R.id.tv_empty);
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav);
        navBar.setVisibility(View.GONE);
        ImageButton back = parentView.findViewById(R.id.ib_back);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        recyclerView = parentView.findViewById(R.id.rv_confirm);
        salonNamesArray = new ArrayList<>();

        setupRecyclerView(recyclerView);
        readSalonNamesFromFireBaseDB();
        mAdapter = new ConfirmAdapter(mContext);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setupOnItemClickListener(new ConfirmAdapter.onItemClickListener() {
            @Override
            public void onItemClick(SPRegistrationModel
                                            salonsDetails) {
                    SalonConfirmDialogFragment details = new SalonConfirmDialogFragment(AOConfirmSalonsFragment.this ,salonsDetails);
                    Log.d("serviceId", "onItemClick-SalonNamesFragment : " + salonsDetails.getUserId());
                    details.setSalonObj(salonsDetails);
                    details.show(getChildFragmentManager(), SalonConfirmDialogFragment.class.getSimpleName());


            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.popBackStack();
            }
        });

        return parentView;
    }

    private void readSalonNamesFromFireBaseDB() {

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner);
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setCancelable(true);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_bar);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        recyclerView.setVisibility(View.GONE);
        empty.setVisibility(View.VISIBLE);

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    salonNamesArray = new ArrayList<>();
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        SPRegistrationModel salon = d.getValue(SPRegistrationModel.class);
                        salonNamesArray.add(salon);
                        recyclerView.setVisibility(View.VISIBLE);
                        empty.setVisibility(View.GONE);
                    }
                    progressDialog.dismiss();
                    mAdapter.update(salonNamesArray);

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
	public void onStatusSelected(int confirmOrDecline, SPRegistrationModel sprObj) {

		int itemPos = salonNamesArray.indexOf(sprObj);
		String id = sprObj.getUserId();
		sprObj.setUserId(id);
		if(confirmOrDecline==1) {
			salonNamesArray.get(itemPos).setStatusType("Confirm");
		}else{
			salonNamesArray.get(itemPos).setStatusType("Cancel");
        }
        uploadIcon(sprObj);
        mAdapter.update(salonNamesArray);
	}
    private void uploadIcon(SPRegistrationModel sprObj) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(sprObj.getUserId());
        myRef.child("statusType").setValue(sprObj.getStatusType())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(mContext, "success", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }
}
