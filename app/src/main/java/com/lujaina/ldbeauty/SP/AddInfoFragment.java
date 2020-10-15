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
import com.lujaina.ldbeauty.RecyclerItemTouchHelperInfo;
import com.lujaina.ldbeauty.RecyclerItemTouchHelperListener;

import java.util.ArrayList;


public class AddInfoFragment extends Fragment implements  RecyclerItemTouchHelperListener {
    FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
    private DatabaseReference myRef;

    private MediatorInterface mMediatorInterface;
    private Context mContext;
    private ProgressDialog progressDialog;

    private ArrayList<AddInfoModel> infoArray;
    private InfoAdapter mAdapter;

    public AddInfoFragment() {
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
        View parentView = inflater.inflate(R.layout.fragment_add_info, container, false);
        ImageButton back = parentView.findViewById(R.id.ib_back);
        FloatingActionButton add = parentView.findViewById(R.id.add_button);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        RecyclerView recyclerView = parentView.findViewById(R.id.add_rv);
        infoArray = new ArrayList<>();
        mAdapter = new InfoAdapter(mContext);
        recyclerView.setAdapter(mAdapter);
        setupRecyclerView(recyclerView);
        readSalonInfoFromFirebaseDB();
        ItemTouchHelper.SimpleCallback item = new RecyclerItemTouchHelperInfo(0, ItemTouchHelper.LEFT, this) {

        };

        mAdapter.setonClickListener(new InfoAdapter.onClickListener() {
            @Override
            public void onClick(AddInfoModel info) {
                UpdateInfoFragment dialogUpdate = new UpdateInfoFragment();
                dialogUpdate.setupdate(info);
                dialogUpdate.show(getChildFragmentManager(), UpdateInfoFragment.class.getSimpleName());
            }
        });

        new ItemTouchHelper(item).attachToRecyclerView(recyclerView);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediatorInterface != null){
                    mMediatorInterface.onBackPressed();
                }
            }
        });

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
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    private void readSalonInfoFromFirebaseDB() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
         myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid()).child(Constants.Salon_Info);
        // Read from the mDatabase
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(true);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_bar);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                infoArray.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    AddInfoModel aboutModel = d.getValue(AddInfoModel.class);
                    infoArray.add(aboutModel);

                }
                progressDialog.dismiss();
                mAdapter.update(infoArray);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                progressDialog.dismiss();
            }
        });
    }
    

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof InfoAdapter.MyViewHolder){
            String infoID = infoArray.get(position).getInfoId();
            int position1 = viewHolder.getAdapterPosition();
            mAdapter.removeItem(position1);
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
            myRef = mDatabase.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid())
                    .child(Constants.Salon_Info)
                    .child(infoID);
            myRef.removeValue();

        }
    }
}
