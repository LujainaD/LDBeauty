package com.lujaina.ldbeauty.User;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.lujaina.ldbeauty.Adapters.GalleryAdapter;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.GalleryModel;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;
import com.lujaina.ldbeauty.SP.FullScreenPictureFragment;

import java.util.ArrayList;


public class GalleryFragment extends Fragment {
    FirebaseUser mFirebaseUser;

    private Context mContext;
    private ProgressDialog progressDialog;

    private ArrayList<GalleryModel> galleryList;
    private GalleryAdapter mAdapter;
    private SPRegistrationModel ownerId;

    RecyclerView recyclerView;
    TextView empty;
    NavController navController;

    public GalleryFragment() {
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
        View parentView = inflater.inflate(R.layout.fragment_salon_gallery, container, false);
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        ownerId = (SPRegistrationModel) getArguments().getSerializable("info");

        empty = parentView.findViewById(R.id.tv_empty);
        ImageButton back = parentView.findViewById(R.id.ib_back);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
         recyclerView = parentView.findViewById(R.id.rv_Gallery);
        galleryList = new ArrayList<>();
        mAdapter = new GalleryAdapter(mContext);
        recyclerView.setAdapter(mAdapter);
        setupRecyclerView(recyclerView);

        mAdapter.setonClickListener(new GalleryAdapter.onClickListener() {
            @Override
            public void onClick(GalleryModel category) {
                    FullScreenPictureFragment img = new FullScreenPictureFragment();
                    img.setImg(category , 0);
                    img.show(getChildFragmentManager(), FullScreenPictureFragment.class.getSimpleName());

            }
        });
        readSalonInfoFromFirebaseDB();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               navController.popBackStack();
            }
        });

        return parentView;
    }


    private void setupRecyclerView(RecyclerView recyclerView) {

        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    private void readSalonInfoFromFirebaseDB() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(ownerId.getUserId()).child(Constants.Salon_Gallery);
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
                galleryList.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    GalleryModel category = d.getValue(GalleryModel.class);
                    galleryList.add(category);
                    recyclerView.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                }
                progressDialog.dismiss();
                mAdapter.update(galleryList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                progressDialog.dismiss();
            }
        });
    }

    public void setSalonGallery(SPRegistrationModel section) {
        ownerId = section;
    }
}