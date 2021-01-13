package com.lujaina.ldbeauty.User;

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
import com.lujaina.ldbeauty.Adapters.CategoryAdapter;
import com.lujaina.ldbeauty.Client.CartFragment;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Dialogs.AddCategoriesDialogFragment;
import com.lujaina.ldbeauty.Dialogs.NoLoginDialogFragment;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.CategoryModel;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;
import com.lujaina.ldbeauty.SP.AddServicesFragment;
import com.lujaina.ldbeauty.SP.RecyclerItemTouchHelperCategories;

import java.util.ArrayList;


public class CategoriesFragment extends Fragment {
    private FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
    private DatabaseReference myRef;

    private Context mContext;
    private ProgressDialog progressDialog;

    private ArrayList<CategoryModel> categoryList;
    private CategoryAdapter mAdapter;
    private SPRegistrationModel info;
    RecyclerView recyclerView;
    TextView empty;
    String userRole;
    NavController navController;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        /*if (context instanceof MediatorInterface) {
            mMediatorInterface = (MediatorInterface) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement MediatorInterface");
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_add_categories, container, false);
        mAuth = FirebaseAuth.getInstance();
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        info = (SPRegistrationModel) getArguments().getSerializable("info");

        FloatingActionButton add = parentView.findViewById(R.id.add_button);
        empty = parentView.findViewById(R.id.tv_empty);
        TextView cart = parentView.findViewById(R.id.tv_cart);
        cart.setVisibility(View.VISIBLE);
        add.setVisibility(View.INVISIBLE);
        ImageButton back = parentView.findViewById(R.id.ib_back);
        mFirebaseUser = mAuth.getCurrentUser();
        if(mFirebaseUser!= null){
            checkUserRole(mFirebaseUser.getUid());
        }
        recyclerView = parentView.findViewById(R.id.rv_categories);
        categoryList = new ArrayList<>();
        mAdapter = new CategoryAdapter(mContext);
        recyclerView.setAdapter(mAdapter);
        setupRecyclerView(recyclerView);
        readSalonInfoFromFirebaseDB();

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFirebaseUser != null && !userRole.equals("Salon Owner")){
                    /*if(mMediatorInterface != null){
                        mMediatorInterface.changeFragmentTo(new CartFragment(), CartFragment.class.getSimpleName());
                    }*/
                    navController.navigate(R.id.action_categoriesFragment_to_cartFragment);

                }else{
                    NoLoginDialogFragment dialog = new NoLoginDialogFragment();
                    dialog.showText(2);
                    dialog.show(getChildFragmentManager(),NoLoginDialogFragment.class.getSimpleName());
                }


            }
        });


        mAdapter.setonClickListener(new CategoryAdapter.onClickListener() {
            @Override
            public void onClick(CategoryModel category) {
                /*if(mMediatorInterface != null){
                    ServicesFragment service = new ServicesFragment();
                    service.setServiceID(category);
                    mMediatorInterface.changeFragmentTo(service, ServicesFragment.class.getSimpleName());

                }*/
                Bundle bundle = new Bundle();
                bundle.putSerializable("CategoryModel", category);
                navController.navigate(R.id.action_categoriesFragment_to_servicesFragment, bundle);

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.popBackStack();
            }
        });


        return parentView;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    private void checkUserRole(String uid) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef;
        myRef = (DatabaseReference) database.getReference(Constants.Users).child(Constants.All_Users).child(uid);

        myRef.orderByChild(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                SPRegistrationModel model = snapshot.getValue(SPRegistrationModel.class);
                model.getUserType();
                mAuth.getCurrentUser();
                if(mAuth!=null) {
                    //Get Currrent User info from Firebase Database
                    SPRegistrationModel currentUser = SPRegistrationModel.getInstance();
                    currentUser.setUserName(model.getUserName());
                    currentUser.setUserEmail(model.getUserEmail());
                    currentUser.setUserId(model.getUserId());
                    currentUser.setUserType(model.getUserType());
                    userRole = model.getUserType();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readSalonInfoFromFirebaseDB() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(info.getUserId()).child(Constants.Salon_Category);
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
                categoryList.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    CategoryModel category = d.getValue(CategoryModel.class);
                    categoryList.add(category);
                    recyclerView.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                }
                progressDialog.dismiss();
                mAdapter.update(categoryList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                progressDialog.dismiss();
            }
        });
    }

}