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

import android.os.Parcelable;
import android.util.Log;
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
import com.lujaina.ldbeauty.Adapters.ServiceAdapter;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Dialogs.AddServiceDialogFragment;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.CategoryModel;
import com.lujaina.ldbeauty.Models.ServiceModel;
import com.lujaina.ldbeauty.R;
import com.lujaina.ldbeauty.Interfaces.RecyclerItemTouchHelperListener;
import com.lujaina.ldbeauty.SP.Instruction.TestInstructionFragment;

import java.util.ArrayList;

public class AddServicesFragment extends Fragment implements RecyclerItemTouchHelperListener {
    FirebaseUser mFirebaseUser;
    private DatabaseReference myRef;

    private ArrayList<ServiceModel> serviceList;
    private ServiceAdapter mAdapter;

    private Context mContext;
    private ProgressDialog progressDialog;

    private ServiceModel service;
    private CategoryModel category;
    //private String salonName;
    RecyclerView recyclerView;
    TextView empty;
    NavController navController;

    public AddServicesFragment() {
        // Required empty public constructor
    }
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
        View parentView = inflater.inflate(R.layout.fragment_add_services, container, false);
        ImageButton instruction = parentView.findViewById(R.id.instruction_button);

        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        String salonName = getArguments().getString("salonName");
        category = (CategoryModel) getArguments().getSerializable("CategoryModel");
        empty = parentView.findViewById(R.id.tv_empty);

        FloatingActionButton add = parentView.findViewById(R.id.add_button);
        ImageButton back = parentView.findViewById(R.id.ib_back);
        FirebaseAuth mAuth= FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
         recyclerView = parentView.findViewById(R.id.recyclerView);
        serviceList = new ArrayList<>();
        mAdapter = new ServiceAdapter(mContext);
        recyclerView.setAdapter(mAdapter);
        setupRecyclerView(recyclerView);
        readSalonInfoFromFirebaseDB();


        //Log.w("category",categoryModel.categoryId);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.popBackStack();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddServiceDialogFragment dialogFragment = new AddServiceDialogFragment();
                dialogFragment.setService(category, salonName);
                dialogFragment.show(getChildFragmentManager(), AddServiceDialogFragment.class.getSimpleName());
            }
        });
        instruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    /*InsAddInfoFragment dialog = new InsAddInfoFragment();
                    dialog.show(getChildFragmentManager(), AddInfoDialogFragment.class.getSimpleName());
*/
                TestInstructionFragment dialog = new TestInstructionFragment();
                dialog.setInstructionPage("service");
                dialog.show(getChildFragmentManager(), TestInstructionFragment.class.getSimpleName());
            }
        });

        ItemTouchHelper.SimpleCallback item = new RecyclerItemTouchHelperService(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT, this) {

        };

        new ItemTouchHelper(item).attachToRecyclerView(recyclerView);
      return parentView;
    }

    private void readSalonInfoFromFirebaseDB() {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid()).child(Constants.Salon_Category).child(category.getCategoryId()).child(Constants.Salon_Service);
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
                    serviceList.clear();
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                            service = d.getValue(ServiceModel.class);
                            serviceList.add(service);
                            recyclerView.setVisibility(View.VISIBLE);
                            empty.setVisibility(View.GONE);
                    }
                    progressDialog.dismiss();
                    mAdapter.update(serviceList);

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    progressDialog.dismiss();
                }
            });
        }

   /* public void setService(CategoryModel categoryList, String salonName) {
        category = categoryList;
        this.salonName = salonName;

    }*/
    private void setupRecyclerView(RecyclerView recyclerView) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }
    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction, int position) {
		ServiceModel swipedService = serviceList.get(position);

        if(viewHolder instanceof ServiceAdapter.MyViewHolder){
            switch (direction) {
                case ItemTouchHelper.LEFT:
                    final String serviceId = swipedService.getServiceId();
                    mAdapter.removeItem(position);
                    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                    myRef = mDatabase.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid())
                            .child(Constants.Salon_Category).child(category.getCategoryId()).child(Constants.Salon_Service)
                            .child(serviceId);
                    myRef.removeValue();
                    break;
                case ItemTouchHelper.RIGHT:
                   /* if(mMediatorInterface != null){
                        AddServiceAppointmentFragment appointment = new AddServiceAppointmentFragment();
                        appointment.setAddAppointmentFragment(swipedService);
                        mMediatorInterface.changeFragmentTo(appointment, AddServiceAppointmentFragment.class.getSimpleName());
                    }
*/
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("ServiceModel", swipedService);
                    //Navigation.findNavController(parentView).navigate(R.id.action_addCategoriesFragment_to_addServicesFragment2, bundle);
                    navController.navigate(R.id.action_addServicesFragment_to_addServiceAppointmentFragment22, bundle);

                    break;

                }

        }
    }

}
