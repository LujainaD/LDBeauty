package com.lujaina.ldbeauty.User;

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
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;
import com.lujaina.ldbeauty.RecyclerItemTouchHelperInfo;
import com.lujaina.ldbeauty.SP.UpdateInfoFragment;

import java.util.ArrayList;

public class InfoFragment extends Fragment {

    private DatabaseReference myRef;

    private MediatorInterface mMediatorInterface;
    private Context mContext;
    private ProgressDialog progressDialog;

    private ArrayList<AddInfoModel> infoArray;
    private InfoAdapter mAdapter;
    FloatingActionButton add;
    private SPRegistrationModel info;
    TextView empty;
    RecyclerView recyclerView;
    public InfoFragment() {
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
        TextView salonName = parentView.findViewById(R.id.tv_toolbar);
        empty = parentView.findViewById(R.id.tv_empty);
        ImageButton back = parentView.findViewById(R.id.ib_back);
        add = parentView.findViewById(R.id.add_button);
        add.setVisibility(View.GONE);
         recyclerView = parentView.findViewById(R.id.add_rv);
        infoArray = new ArrayList<>();
        mAdapter = new InfoAdapter(mContext);
        recyclerView.setAdapter(mAdapter);
        setupRecyclerView(recyclerView);
        readSalonInfoFromFirebaseDB();

        if (info != null) {
            salonName.setText(info.getSalonName()+ " Info");
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediatorInterface != null){
                    mMediatorInterface.onBackPressed();
                }
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
        myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(info.getUserId()).child(Constants.Salon_Info);
        // Read from the mDatabase
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(true);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_bar);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        recyclerView.setVisibility(View.GONE);
        empty.setVisibility(View.VISIBLE);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                infoArray.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    AddInfoModel aboutModel = d.getValue(AddInfoModel.class);
                    infoArray.add(aboutModel);
                    recyclerView.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);

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



    public void setInfo(SPRegistrationModel names) {
        info  = names;

    }

}