package com.lujaina.ldbeauty.Client;

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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lujaina.ldbeauty.Adapters.RatingAdapter;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Dialogs.RatingDialogFragment;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Interfaces.RecyclerItemTouchHelperListener;
import com.lujaina.ldbeauty.Models.CommentModel;
import com.lujaina.ldbeauty.R;
import com.lujaina.ldbeauty.RecyclerItemTouchHelperFeedback;
import com.lujaina.ldbeauty.User.RatingFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class ClientFeedbackFragment extends Fragment implements RecyclerItemTouchHelperListener, UpdateCommentFragment.setFeedback {
    FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
    private DatabaseReference myRef;

    private MediatorInterface mMediatorInterface;
    private Context mContext;
    private ProgressDialog progressDialog;

    private ArrayList<CommentModel> commentArray;
    private RatingAdapter mAdapter;
    String ownerId;
    RecyclerView recyclerView;
    TextView empty;
    NavController navController;

    public ClientFeedbackFragment() {
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
        View parentView = inflater.inflate(R.layout.fragment_rating, container, false);
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav);

        navBar.setVisibility(View.GONE);

        empty = parentView.findViewById(R.id.tv_empty);
        FloatingActionButton add = parentView.findViewById(R.id.add_button);
        add.setVisibility(View.GONE);
        ImageButton back = parentView.findViewById(R.id.ib_back);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

         recyclerView = parentView.findViewById(R.id.rv_comment);
        commentArray = new ArrayList<>();
        mAdapter = new RatingAdapter(mContext);
        recyclerView.setAdapter(mAdapter);
        setupRecyclerView(recyclerView);
        readSalonInfoFromFirebaseDB();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.popBackStack();
            }
        });

        mAdapter.setonClickListener(new RatingAdapter.onClickListener() {
            @Override
            public void onClick(CommentModel commentModel) {
                UpdateCommentFragment dialogFragment = new UpdateCommentFragment(ClientFeedbackFragment.this,commentModel);
                Bundle bundle =new Bundle();
                bundle.putSerializable("comment",commentModel);
                dialogFragment.setDialog(bundle);
                if(getActivity()!=null && isAdded()){
                    dialogFragment.show(getChildFragmentManager(), UpdateCommentFragment.class.getSimpleName());
                }
               // updateComment(commentModel);
            }
        });

        ItemTouchHelper.SimpleCallback item = new RecyclerItemTouchHelperFeedback(0, ItemTouchHelper.LEFT, this) ;
        new ItemTouchHelper(item).attachToRecyclerView(recyclerView);

        return parentView;
    }

    private void updateComment(CommentModel commentModel) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid()).child(Constants.Comments).child(commentModel.getCommentId());
        CommentModel model = new CommentModel();
        model.setComment(commentModel.getComment());
        commentModel.setCommentId(commentModel.getCommentId());
        model.setComment(commentModel.getComment());
        commentModel.setNumStars(commentModel.getNumStars());
        commentModel.setCommentDate(getCurrentDate());
        commentModel.setOwnerId(commentModel.getOwnerId());
        commentModel.setClientId(mFirebaseUser.getUid());
        myRef.setValue(commentModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });

    }

    private String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
        return df.format(c);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    private void readSalonInfoFromFirebaseDB() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid()).child(Constants.Comments);
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
                commentArray.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    CommentModel commentModel = d.getValue(CommentModel.class);
                    commentArray.add(commentModel);
                    ownerId = commentModel.getOwnerId();
                    recyclerView.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                }
                progressDialog.dismiss();
                mAdapter.update(commentArray);
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
        if (viewHolder instanceof RatingAdapter.MyViewHolder) {
            String commentId = commentArray.get(position).getCommentId();
            String owner = commentArray.get(position).getOwnerId();

            int position1 = viewHolder.getAdapterPosition();
            mAdapter.removeItem(position1);
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
            DatabaseReference myRef = mDatabase.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid())
                    .child(Constants.Comments)
                    .child(commentId);
            DatabaseReference salonRef = mDatabase.getReference(Constants.Users).child(Constants.Salon_Owner).child(owner)
                    .child(Constants.Comments)
                    .child(commentId);
            myRef.removeValue();
            salonRef.removeValue();

        }
    }


    @Override
    public void onUpdate(CommentModel commentModel,String userComment, float rating) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid()).child(Constants.Comments).child(commentModel.getCommentId());
        DatabaseReference salonOwnerRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(commentModel.getOwnerId()).child(Constants.Comments).child(commentModel.getCommentId());

        CommentModel model = new CommentModel();
        model.setComment(userComment);
        model.setCommentId(commentModel.getCommentId());
        model.setNumStars(rating);
        model.setCommentDate(getCurrentDate());
        model.setOwnerId(commentModel.getOwnerId());
        model.setClientId(mFirebaseUser.getUid());
        myRef.setValue(model);

        salonOwnerRef.setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(mContext, R.string.updated_comment, Toast.LENGTH_SHORT).show();
            }
        });
    }
}