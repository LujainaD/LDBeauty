package com.lujaina.ldbeauty.AppOwner;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


public class AoConfirmSalonsFragment extends Fragment implements SalonConfirmDialogFragment.statusConfirmed {
	private static final String TAG = "AoConfirmSalonsFragment";
    private Context mContext;
    private MediatorInterface mMediatorInterface;
    FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
    private FirebaseDatabase mDatabase;
    RecyclerView recyclerView;
    private ConfirmAdapter mAdapter;
    ArrayList<SPRegistrationModel> salonNamesArray;
   ProgressDialog progressDialog;
    public AoConfirmSalonsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if(context instanceof MediatorInterface) {
            mMediatorInterface = (MediatorInterface) context;
        }
        else{
            throw new RuntimeException(context.toString()+ "must implement MediatorInterface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView= inflater.inflate(R.layout.fragment_ao_confirm_salons, container, false);
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
            public void onItemClick(SPRegistrationModel salonsDetails) {
                if (mMediatorInterface != null) {


                    SalonConfirmDialogFragment details = new SalonConfirmDialogFragment(AoConfirmSalonsFragment.this ,salonsDetails);
                    Log.d("serviceId", "onItemClick-SalonNamesFragment : " + salonsDetails.getOwnerId());
                    details.setSalonObj(salonsDetails);
                    details.show(getChildFragmentManager(), SalonConfirmDialogFragment.class.getSimpleName());

                }

            }
        });





//        mAdapter.setStatusListener(new SalonConfirmDialogFragment.status() {
//            @Override
//            public void confirm(SPRegistrationModel confirmStatus) {
//				Log.d(TAG, "confirm: ");
//            }
//
//            @Override
//            public void decline(SPRegistrationModel confirmStatus) {
//				Log.d(TAG, "decline: ");
//            }
//        });
        return parentView;
    }

    private void readSalonNamesFromFireBaseDB() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner);
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_bar);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                salonNamesArray = new ArrayList<>();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    SPRegistrationModel salon = d.getValue(SPRegistrationModel.class);
                    salonNamesArray.add(salon);
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
        DividerItemDecoration divider = new DividerItemDecoration(mContext, layoutManager.getOrientation());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(divider);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

	@Override
	public void onStatusSelected(int confirmOrDecline, SPRegistrationModel sprObj) {

		int itemPos = salonNamesArray.indexOf(sprObj);
		if(confirmOrDecline==1) {
			salonNamesArray.get(itemPos).setStatusType("Confrim");
		}else{
			salonNamesArray.get(itemPos).setStatusType("Cancel");

        }

		mAdapter.update(salonNamesArray);

	}
}
