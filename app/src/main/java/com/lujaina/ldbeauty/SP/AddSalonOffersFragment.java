package com.lujaina.ldbeauty.SP;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lujaina.ldbeauty.Adapters.OfferAdapter;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Dialogs.OffersDialogFragment;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.CategoryModel;
import com.lujaina.ldbeauty.Models.OfferModel;
import com.lujaina.ldbeauty.R;
import com.lujaina.ldbeauty.Interfaces.RecyclerItemTouchHelperListener;
import com.lujaina.ldbeauty.RecyclerItemTouchHelperOffers;

import java.util.ArrayList;

public class AddSalonOffersFragment extends Fragment implements RecyclerItemTouchHelperListener {
    FirebaseUser mFirebaseUser;
    private DatabaseReference myRef;

    private ArrayList<OfferModel> offersList;
    private OfferAdapter mAdapter;

    private Context mContext;
    private ProgressDialog progressDialog;

    private OfferModel offer;
    private String salonName;
    TextView empty;
    RecyclerView recyclerView;
    NavController navController;

    public AddSalonOffersFragment() {
        // Required empty public constructor
    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        /*if (context instanceof MediatorInterface) {
            mMediatorInterface = (MediatorInterface) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement MediatorInterface");
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_add_salon_offers, container, false);

        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
         salonName = getArguments().getString("salonName");

        empty = parentView.findViewById(R.id.tv_empty);

        FloatingActionButton add = parentView.findViewById(R.id.add_button);
        ImageButton back = parentView.findViewById(R.id.ib_back);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
         recyclerView = parentView.findViewById(R.id.recyclerView);
        offersList = new ArrayList<>();
        mAdapter = new OfferAdapter(mContext);
        recyclerView.setAdapter(mAdapter);
        setupRecyclerView(recyclerView);
        readSalonOffersFromFirebaseDB();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.popBackStack();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OffersDialogFragment dialogFragment = new OffersDialogFragment();
                dialogFragment.setOffers(offer, salonName);
                dialogFragment.show(getChildFragmentManager(), OffersDialogFragment.class.getSimpleName());
            }
        });

        ItemTouchHelper.SimpleCallback item = new RecyclerItemTouchHelperOffers(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this) {

        };

        new ItemTouchHelper(item).attachToRecyclerView(recyclerView);
        return parentView;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    private void readSalonOffersFromFirebaseDB() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid()).child(Constants.Salon_Offers);
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
                offersList.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    offer = d.getValue(OfferModel.class);
                    offersList.add(offer);
                    recyclerView.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                }
                progressDialog.dismiss();
                mAdapter.update(offersList);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction, int position) {
        OfferModel swipedService = offersList.get(position);

        if (viewHolder instanceof OfferAdapter.MyViewHolder) {
            switch (direction) {
                case ItemTouchHelper.LEFT:
                    final String serviceId = swipedService.getOfferId();
                    mAdapter.removeItem(position);
                    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                    myRef = mDatabase.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid())
                            .child(Constants.Salon_Offers).child(serviceId);

                    myRef.removeValue();
                    break;
                case ItemTouchHelper.RIGHT:
                   /* if (mMediatorInterface != null) {
                        AddOffersAppointmentFragment appointment = new AddOffersAppointmentFragment();
                        appointment.setAddAppointmentFragment(swipedService, salonName);
                        mMediatorInterface.changeFragmentTo(appointment, AddOffersAppointmentFragment.class.getSimpleName());
                    }*/

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("OfferModel", swipedService);
                    //Navigation.findNavController(parentView).navigate(R.id.action_addCategoriesFragment_to_addServicesFragment2, bundle);
                    navController.navigate(R.id.action_addSalonOffersFragment_to_addOffersAppointmentFragment2, bundle);

                    break;

            }

        }
    }

}