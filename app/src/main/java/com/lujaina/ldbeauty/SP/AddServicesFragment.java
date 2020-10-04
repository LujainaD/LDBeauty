package com.lujaina.ldbeauty.SP;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lujaina.ldbeauty.Adapters.CategoryAdapter;
import com.lujaina.ldbeauty.Adapters.ServiceAdapter;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Dialogs.AddCategoriesDialogFragment;
import com.lujaina.ldbeauty.Dialogs.AddServiceDialogFragment;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.CategoryModel;
import com.lujaina.ldbeauty.Models.ServiceModel;
import com.lujaina.ldbeauty.R;
import com.lujaina.ldbeauty.RecyclerItemTouchHelperListener;

import java.util.ArrayList;

public class AddServicesFragment extends Fragment implements RecyclerItemTouchHelperListener {
    FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    private ArrayList<ServiceModel> serviceList;
    private ServiceAdapter mAdapter;
    private MediatorInterface mMediatorInterface;
    private Context mContext;
    ServiceModel serviceModel;
    ProgressDialog progressDialog;
    private CategoryModel category;
    RecyclerView.ViewHolder viewHolder;
    RecyclerView recyclerView;
    public AddServicesFragment() {
        // Required empty public constructor
    }
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
        View parentView = inflater.inflate(R.layout.fragment_add_services, container, false);
        FloatingActionButton add = parentView.findViewById(R.id.add_button);
        ImageButton back = parentView.findViewById(R.id.ib_back);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
         recyclerView = parentView.findViewById(R.id.recyclerView);
        serviceList = new ArrayList<>();
        mAdapter = new ServiceAdapter(mContext);
        recyclerView.setAdapter(mAdapter);
        setupRecyclerView(recyclerView);
        readSalonInfoFromFirebaseDB();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediatorInterface.onBackPressed();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddServiceDialogFragment dialogFragment = new AddServiceDialogFragment();
                dialogFragment.setService(category);
                dialogFragment.show(getChildFragmentManager(), AddServiceDialogFragment.class.getSimpleName());
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
        if(Constants.Salon_Service == null){
            progressDialog.dismiss();
        }else {
            progressDialog = new ProgressDialog(mContext);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_bar);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    serviceList.clear();
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        ServiceModel service = d.getValue(ServiceModel.class);
                        serviceList.add(service);
                        progressDialog.dismiss();
                        mAdapter.update(serviceList);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    progressDialog.dismiss();
                }
            });
        }


    }

    public void setService(CategoryModel categoryList) {
        category = categoryList;

    }
    private void setupRecyclerView(RecyclerView recyclerView) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }
    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction, int position) {
        position = viewHolder.getAdapterPosition();

        if(viewHolder instanceof ServiceAdapter.MyViewHolder){
            switch (direction) {
                case ItemTouchHelper.LEFT:
                    final String serviceId = serviceList.get(position).getServiceId();
                    final int position1 = viewHolder.getAdapterPosition();
                    mAdapter.removeItem(position1);
                    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                    myRef = mDatabase.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid())
                            .child(Constants.Salon_Category).child(category.getCategoryId()).child(Constants.Salon_Service)
                            .child(serviceId);
                    myRef.removeValue();
                    Snackbar.make(recyclerView, serviceModel.getServiceTitle(), Snackbar.LENGTH_LONG).setAction("undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            serviceList.add(position1, serviceModel);
                            mAdapter.notifyItemInserted(position1);
                        }

                    }).show();
                    break;
                case ItemTouchHelper.RIGHT:
                        mMediatorInterface.changeFragmentTo(new CustomizeCalendar(), CustomizeCalendar.class.getSimpleName());
                   mAdapter.notifyDataSetChanged();
                    break;

                }

        }
    }

}