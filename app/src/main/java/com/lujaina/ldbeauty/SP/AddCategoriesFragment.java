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
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Dialogs.AddCategoriesDialogFragment;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.CategoryModel;
import com.lujaina.ldbeauty.R;
import com.lujaina.ldbeauty.RecyclerItemTouchHelperListener;

import java.util.ArrayList;


public class AddCategoriesFragment extends Fragment implements RecyclerItemTouchHelperListener {
    FirebaseUser mFirebaseUser;
    private DatabaseReference myRef;

    private MediatorInterface mMediatorInterface;
    private Context mContext;
    private ProgressDialog progressDialog;

    private ArrayList<CategoryModel> categoryList;
    private CategoryAdapter mAdapter;
    RecyclerView recyclerView;
    TextView empty;
    public AddCategoriesFragment() {
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
        View  parentView = inflater.inflate(R.layout.fragment_add_categories, container, false);
        FloatingActionButton add = parentView.findViewById(R.id.add_button);
         empty = parentView.findViewById(R.id.tv_empty);
        ImageButton back = parentView.findViewById(R.id.ib_back);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        recyclerView = parentView.findViewById(R.id.rv_categories);
        categoryList = new ArrayList<>();
        mAdapter = new CategoryAdapter(mContext);
        recyclerView.setAdapter(mAdapter);
        setupRecyclerView(recyclerView);
        readSalonInfoFromFirebaseDB();

        mAdapter.setonClickListener(new CategoryAdapter.onClickListener() {
            @Override
            public void onClick(CategoryModel category) {
                if(mMediatorInterface != null){
                    AddServicesFragment service = new AddServicesFragment();
                    service.setService(category);
                    mMediatorInterface.changeFragmentTo(service, AddServicesFragment.class.getSimpleName());

                }
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCategoriesDialogFragment dialogFragment = new AddCategoriesDialogFragment();
                dialogFragment.show(getChildFragmentManager(), AddCategoriesDialogFragment.class.getSimpleName());
            }
        });

        ItemTouchHelper.SimpleCallback item = new RecyclerItemTouchHelperCategories(0, ItemTouchHelper.LEFT, this) {

        };

        new ItemTouchHelper(item).attachToRecyclerView(recyclerView);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediatorInterface != null){
                    mMediatorInterface.onBackPressed();
                }
            }
        });

        return  parentView;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    private void readSalonInfoFromFirebaseDB() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid()).child(Constants.Salon_Category);
        // Read from the mDatabase
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(true);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_bar);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                categoryList.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    CategoryModel category = d.getValue(CategoryModel.class);
                    categoryList.add(category);

                }
                progressDialog.dismiss();
                recyclerView.setVisibility(View.VISIBLE);
                empty.setVisibility(View.INVISIBLE);
                mAdapter.update(categoryList);
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
        if(viewHolder instanceof CategoryAdapter.MyViewHolder){
            String categoryID = categoryList.get(position).getCategoryId();
            int position1 = viewHolder.getAdapterPosition();
            mAdapter.removeItem(position1);
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
            myRef = mDatabase.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid())
                    .child(Constants.Salon_Category)
                    .child(categoryID);
            myRef.removeValue();

        }
    }
}