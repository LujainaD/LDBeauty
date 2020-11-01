package com.lujaina.ldbeauty.User;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lujaina.ldbeauty.Adapters.ServiceAdapter;
import com.lujaina.ldbeauty.Adapters.UserServiceAdapter;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.CategoryModel;
import com.lujaina.ldbeauty.Models.ServiceModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;


public class ServicesFragment extends Fragment {


    private CategoryModel serviceId;
    private MediatorInterface mMediatorCallback;
    private Context mContext;

    private ArrayList<ServiceModel> serviceList;
    private UserServiceAdapter mAdapter;


    private ProgressDialog progressDialog;

    private ServiceModel service;
    private CategoryModel category;
    public ServicesFragment() {
        // Required empty public constructor
    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof MediatorInterface) {
            mMediatorCallback = (MediatorInterface) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement MediatorInterface");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_services, container, false);
        ImageView img = parentView.findViewById(R.id.iv_category);
        ImageButton back = parentView.findViewById(R.id.ib_back);
        TextView categoryTitle = parentView.findViewById(R.id.tv_categoryTitle);

        RecyclerView recyclerView = parentView.findViewById(R.id.recyclerView);
        serviceList = new ArrayList<>();
        mAdapter = new UserServiceAdapter(mContext);
        recyclerView.setAdapter(mAdapter);
        setupRecyclerView(recyclerView);
        readSalonInfoFromFirebaseDB();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediatorCallback != null){
                    mMediatorCallback.onBackPressed();
                }
            }
        });

        if(serviceId != null){
            Glide.with(mContext).load(serviceId.getCategoryURL()).into(img);
            categoryTitle.setText(serviceId.getCategoryTitle());
        }
        return parentView;
    }

    private void readSalonInfoFromFirebaseDB() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(serviceId.getOwnerId()).child(Constants.Salon_Category).child(serviceId.getCategoryId()).child(Constants.Salon_Service);
        // Read from the mDatabase
        progressDialog = new ProgressDialog(mContext);
        progressDialog.show();
        progressDialog.setCancelable(true);
        progressDialog.setContentView(R.layout.progress_bar);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                serviceList.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    service = d.getValue(ServiceModel.class);
                    serviceList.add(service);

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

    private void setupRecyclerView(RecyclerView recyclerView) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }


    public void setServiceID(CategoryModel category) {
        serviceId = category;
    }
}