package com.lujaina.ldbeauty.SP;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

    RecyclerView recyclerView;
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
        Toolbar toolbar = parentView.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.side_menu);
        setHasOptionsMenu(true);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.my_profile_menu: {
                        if (mFirebaseUser != null) {
                            mMediatorInterface.changeFragmentTo(new SPProfileFragment(), SPProfileFragment.class.getSimpleName());
                            break;
                        } else {

                            mMediatorInterface.changeFragmentTo(new LoginChoicesFragment(), LoginChoicesFragment.class.getSimpleName());
                        }
                    }

                    case R.id.log_out_menu: {
                        if (mFirebaseUser != null) {
                            mMediatorInterface.changeFragmentTo(new LoginChoicesFragment(), LoginChoicesFragment.class.getSimpleName());
                        }else {
                        }
                    }
                    case R.id.choose_menu: {
                        if (mFirebaseUser == null) {
                            startActivity(new Intent(mContext, MainActivity.class));
                        }else {
                        }
                    }

                }
                return false;
            }

        });


        /*
        ImageButton menuButton = parentView.findViewById(R.id.menu);
*/
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference(Constants.Users).child(Constants.Salon_Owner);
        final EditText ti_search = parentView.findViewById(R.id.tv_search);

   /*     menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(mContext, v);
                popup.getMenuInflater().inflate(R.menu.side_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.my_profile_menu: {
                                if (mFirebaseUser != null) {
                                    item.setVisible(true);
                                    mMediatorInterface.changeFragmentTo(new SPProfileFragment(), SPProfileFragment.class.getSimpleName());
                                    break;
                                } else {
                                    item.setVisible(false);
*//*
                                    mMediatorInterface.changeFragmentTo(new LoginChoicesFragment(), LoginChoicesFragment.class.getSimpleName());
*//*
                                }
                            }

                            case R.id.log_out_menu: {
                                if (mFirebaseUser != null) {
                                    item.setVisible(true);
                                    mMediatorInterface.changeFragmentTo(new LoginChoicesFragment(), LoginChoicesFragment.class.getSimpleName());
                                }else {
                                    item.setVisible(false);
                                }
                                }
                            case R.id.choose_menu: {
                                if (mFirebaseUser == null) {
                                    item.setVisible(true);
                                    startActivity(new Intent(mContext, MainActivity.class));
                                }else {
                                    item.setVisible(false);
                                }
                                }

                        }
                        return false;
                    }
                });
                popup.show();
            }
        });*/

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


        recyclerView = parentView.findViewById(R.id.rv_salons);
        readSalonNamesFromFireBaseDB();

        setupRecyclerView(recyclerView);
        mAdapter = new HomeAdapter(mContext);
        recyclerView.setAdapter(mAdapter);



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
/*
        DividerItemDecoration divider = new DividerItemDecoration(mContext, layoutManager.getOrientation());
*/

        recyclerView.setLayoutManager(layoutManager);
/*
        recyclerView.addItemDecoration(divider);
*/
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
                    Log.d("serviceId", salon.getOwnerId());
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