package com.lujaina.ldbeauty.SP;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lujaina.ldbeauty.Adapters.InfoAdapter;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Dialogs.AddInfoDialogFragment;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.AddInfoModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;


public class AddInfoFragment extends Fragment implements InfoAdapter.infoListener{
    private MediatorInterface mMediatorInterface;
    private Context mContext;
    FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    private ArrayList<AddInfoModel> mUpdate;
    private InfoAdapter mAdapter;
    public AddInfoFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
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
        View parentView = inflater.inflate(R.layout.fragment_add_info, container, false);
        FloatingActionButton add = parentView.findViewById(R.id.add_button);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        RecyclerView recyclerView = parentView.findViewById(R.id.add_rv);
        mUpdate = new ArrayList<>();
        mAdapter = new InfoAdapter(mContext);
        recyclerView.setAdapter(mAdapter);
        setupRecyclerView(recyclerView);
        readSalonInfoFromFirebaseDB();

        mAdapter.setRemoveListener(AddInfoFragment.this);


        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                AddInfoModel aboutModel = new AddInfoModel();
                int position = viewHolder.getAdapterPosition();
                mUpdate.remove(position);
                mAdapter.notifyDataSetChanged();
                deleteInfo(aboutModel );


            }
        });
              helper.attachToRecyclerView(recyclerView);



        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddInfoDialogFragment dialogColor = new AddInfoDialogFragment();
                dialogColor.show(getChildFragmentManager(), AddInfoDialogFragment.class.getSimpleName());
            }
        });
        return parentView;
    }
    private void setupRecyclerView(RecyclerView recyclerView) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
//      DividerItemDecoration divider = new DividerItemDecoration(mContext, layoutManager.getOrientation());
        recyclerView.setLayoutManager(layoutManager);
        // recyclerView.addItemDecoration(divider);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    private void readSalonInfoFromFirebaseDB() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid()).child(Constants.Salon_Info);
        // Read from the mDatabase
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUpdate.clear();

                //  String currentUserId = mFirebaseUser.getUid();
                //base on reference  dataSnapshot holdes array of notes
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    AddInfoModel aboutModel = d.getValue(AddInfoModel.class);
                    mUpdate.add(aboutModel);
                    mAdapter.update(mUpdate);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

    @Override
    public void deleteInfo(AddInfoModel infoId) {
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid())
                .child(Constants.Salon_Info)
                .child(infoId.getInfoId());
        myRef.removeValue();
    }
}