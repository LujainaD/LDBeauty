package com.lujaina.ldbeauty.SP;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.lujaina.ldbeauty.Adapters.CategoryAdapter;
import com.lujaina.ldbeauty.Adapters.GalleryAdapter;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Dialogs.AddCategoriesDialogFragment;
import com.lujaina.ldbeauty.Dialogs.AddGalleryDialogFragment;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.CategoryModel;
import com.lujaina.ldbeauty.Models.GalleryModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;


public class AddGalleryFragment extends Fragment  {
    FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    private ArrayList<GalleryModel> galleryList;
    private GalleryAdapter mAdapter;
    private MediatorInterface mMediatorInterface;
    private Context mContext;
    ProgressDialog progressDialog;

    public AddGalleryFragment() {
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
        View parentView = inflater.inflate(R.layout.fragment_add_gallery, container, false);
        FloatingActionButton add = parentView.findViewById(R.id.add_button);
        ImageButton back = parentView.findViewById(R.id.ib_back);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        RecyclerView recyclerView = parentView.findViewById(R.id.rv_categories);
        galleryList = new ArrayList<>();
        mAdapter = new GalleryAdapter(mContext);
        recyclerView.setAdapter(mAdapter);
        setupRecyclerView(recyclerView);

        mAdapter.setonClickListener(new GalleryAdapter.onClickListener() {
            @Override
            public void onClick(GalleryModel category) {
                if(mMediatorInterface != null){
                    FullScreenPictureFragment img = new FullScreenPictureFragment();
                    img.setImg(category);
                    img.show(getChildFragmentManager(), FullScreenPictureFragment.class.getSimpleName());
                }
            }
        });
        readSalonInfoFromFirebaseDB();
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
                AddGalleryDialogFragment dialogFragment = new AddGalleryDialogFragment();
                dialogFragment.show(getChildFragmentManager(), AddGalleryDialogFragment.class.getSimpleName());
            }
        });

        return parentView;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {

        GridLayoutManager layoutManager = new GridLayoutManager(mContext,3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    private void readSalonInfoFromFirebaseDB() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid()).child(Constants.Salon_Gallery);
        // Read from the mDatabase
        progressDialog = new ProgressDialog(mContext);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_bar);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                galleryList.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    GalleryModel category = d.getValue(GalleryModel.class);
                    galleryList.add(category);
                    progressDialog.dismiss();
                    mAdapter.update(galleryList);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                progressDialog.dismiss();
            }
        });
    }

  /*  @Override
    public void onDelete(int img, GalleryModel picture) {
        String galleryID = galleryList.get(img).getPictureId();
        mAdapter.removeItem(img);
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid()).child(Constants.Salon_Gallery).child(galleryID);
        myRef.removeValue();

    }*/
}