package com.lujaina.ldbeauty.User;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuAdapter;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lujaina.ldbeauty.Adapters.ConfirmAdapter;
import com.lujaina.ldbeauty.Adapters.HomeAdapter;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.HomeActivity;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.LoginChoicesFragment;
import com.lujaina.ldbeauty.MainActivity;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;


public class SalonsHomeFragment extends Fragment {
    FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    private Context mContext;
    private MediatorInterface mMediatorInterface;
    ProgressDialog progressDialog;

    private HomeAdapter mAdapter;
    ArrayList<SPRegistrationModel> salonNamesArray;

    public SalonsHomeFragment() {
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_salons_home, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav);
        navBar.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference(Constants.Users).child(Constants.Salon_Owner);
        EditText ti_search = parentView.findViewById(R.id.tv_search);
        RecyclerView recyclerView = parentView.findViewById(R.id.rv_salons);
        salonNamesArray = new ArrayList<>();
        mAdapter = new HomeAdapter(mContext);
        recyclerView.setAdapter(mAdapter);
        setupRecyclerView(recyclerView);
        readSalonNamesFromFireBaseDB();

        mAdapter.setonClickListener(new HomeAdapter.onClickListener() {
            @Override
            public void onClick(SPRegistrationModel salon) {
                if(mMediatorInterface != null){
                    SelectedSalonFragment section = new SelectedSalonFragment();
                    section.setSection(salon);
                    mMediatorInterface.changeFragmentTo(section ,SelectedSalonFragment.class.getSimpleName());

                }

            }
        });

        ti_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                if (text.length() > 0) {
                    search(text.toString());

                } else
                {
                    mAdapter.update(salonNamesArray);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return parentView;
    }

    private void search(final String text) {

        ArrayList<SPRegistrationModel> temp = new ArrayList<>();
        for (SPRegistrationModel name : salonNamesArray) {
            if (name.getSalonName().toLowerCase().contains(text.toLowerCase()) || name.getSalonCity().toLowerCase().contains(text.toLowerCase())) {
                temp.add(name);
            }

        }
        mAdapter.update(temp);

    }
    private void setupRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        DividerItemDecoration divider = new DividerItemDecoration(mContext, layoutManager.getOrientation());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(divider);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    public void readSalonNamesFromFireBaseDB() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner);
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_bar);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        // Read from the database
        myRef.orderByChild("statusType").equalTo("Confirm").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // base on myRef, dataSnapshot is array of
                salonNamesArray = new ArrayList<>();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    SPRegistrationModel salon = d.getValue(SPRegistrationModel.class);
                    salonNamesArray.add(salon);

                }
                progressDialog.dismiss();
                mAdapter.update(salonNamesArray);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                progressDialog.dismiss();

            }
        });
    }

}