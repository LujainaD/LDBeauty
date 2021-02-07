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

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lujaina.ldbeauty.Adapters.RatingAdapter;
import com.lujaina.ldbeauty.Client.UpdateCommentFragment;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Dialogs.DialogToUpdateCommentFragment;
import com.lujaina.ldbeauty.Dialogs.GreetingDialogFragment;
import com.lujaina.ldbeauty.Dialogs.NoLoginDialogFragment;
import com.lujaina.ldbeauty.Dialogs.RatingDialogFragment;
import com.lujaina.ldbeauty.Models.CommentModel;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class RatingFragment extends Fragment implements RatingDialogFragment.setFeedback ,UpdateCommentFragment.setFeedback, DialogToUpdateCommentFragment.setUpdateDialog{
    FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
    private DatabaseReference myRef;

    private Context mContext;
    private ProgressDialog progressDialog;

    private ArrayList<CommentModel> commentArray;
    private RatingAdapter mAdapter;
    private SPRegistrationModel salonInfo;
    String userRole;
    RecyclerView recyclerView;
    TextView empty;
    ItemTouchHelper.SimpleCallback item;
    NavController navController;
    private int status = 0;
    FloatingActionButton add;
    public RatingFragment() {
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
        salonInfo = (SPRegistrationModel) getArguments().getSerializable("info");
        TextView toolbarText = parentView.findViewById(R.id.tv_toolbar);
        toolbarText.setText("Feedback & Rating");
        empty = parentView.findViewById(R.id.tv_empty);
        ImageButton back = parentView.findViewById(R.id.ib_back);
         add = parentView.findViewById(R.id.add_button);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        if(mFirebaseUser!= null){
            checkUserRole(mFirebaseUser.getUid());
            //checkIfFeedBackExist();

        }

        recyclerView = parentView.findViewById(R.id.rv_comment);
        commentArray = new ArrayList<>();
        mAdapter = new RatingAdapter(mContext);
        recyclerView.setAdapter(mAdapter);
        setupRecyclerView(recyclerView);
        readSalonInfoFromFirebaseDB();



        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             if(mFirebaseUser == null || userRole.equals("Salon Owner")){
                 NoLoginDialogFragment dialog = new NoLoginDialogFragment();
                 Bundle bundle = new Bundle();
                 bundle.putInt("num",2);
                 dialog.setArguments(bundle);
                 dialog.show(getChildFragmentManager(),NoLoginDialogFragment.class.getSimpleName());
             }else {
                 if( mFirebaseUser.isEmailVerified()) {
                     //countOrderChildren();
                     checkIfFeedBackExist();
                 }else {
                     Toast.makeText(mContext, "Please verify your email address first", Toast.LENGTH_SHORT).show();

                 }
             }
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

    private void checkIfFeedBackExist() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef  = database.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid()).child(Constants.Comments);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() !=null){
                    Toast.makeText(mContext, "not null", Toast.LENGTH_SHORT).show();

                    DialogToUpdateCommentFragment fragment = new DialogToUpdateCommentFragment(RatingFragment.this);
                    if(getActivity()!=null && isAdded()) {

                        fragment.show(getChildFragmentManager(), DialogToUpdateCommentFragment.class.getSimpleName());
                    }
                   // add.setVisibility(View.INVISIBLE);
                   // readCommentFromUser();
                }else {
                    Toast.makeText(mContext, "null", Toast.LENGTH_SHORT).show();
                        showDialog();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readCommentFromUser() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef  = database.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid()).child(Constants.Comments);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d : snapshot.getChildren()) {
                    CommentModel commentModel = d.getValue(CommentModel.class);
                  //  ArrayList<CommentModel> commentModels = new ArrayList<>();
                  //  commentModels.add(commentModel);
                    showCommentDialogToUpdate(commentModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showCommentDialogToUpdate(CommentModel commentModels) {
        UpdateCommentFragment dialogFragment = new UpdateCommentFragment(RatingFragment.this);
        Bundle bundle =new Bundle();
        bundle.putSerializable("comment",commentModels);
        dialogFragment.setDialog(bundle);
        if(getActivity()!=null && isAdded()){
            dialogFragment.show(getChildFragmentManager(), UpdateCommentFragment.class.getSimpleName());
        }

    }
/*
    private void countOrderChildren() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference clientOrderRef = database.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid()).child(Constants.History_Order);
        clientOrderRef.orderByChild("ownerId").equalTo(salonInfo.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    snapshot.getChildrenCount();

                    String countedOrder = String.valueOf(snapshot.getChildrenCount());
                    if (Integer.parseInt(countedOrder) == 0) {
                        Toast.makeText(mContext, "you need to book appointment to add feedback", Toast.LENGTH_SHORT).show();

                    } else{
                       // Log.w("countedFeedback", countedOrder);
                        checkFeedbackChildren(countedOrder);
                    }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void checkFeedbackChildren(String countedOrder) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef  = database.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid()).child(Constants.Comments);
        myRef.orderByChild("ownerId").equalTo(salonInfo.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String  numberofFeedback = String.valueOf(snapshot.getChildrenCount());
                    int countFeedback = Integer.parseInt(numberofFeedback);
                    if(Integer.parseInt(countedOrder)==countFeedback){
                        Toast.makeText(mContext, "order=feedback, you need to book appointment to add feedback", Toast.LENGTH_SHORT).show();
                    }else*//*if(Integer.parseInt(countedOrder)>Integer.parseInt(numberofFeedback))*//*{
                    //Toast.makeText(mContext, "appointment more than feedback", Toast.LENGTH_SHORT).show();
                       showDialog();

                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void showDialog() {
        RatingDialogFragment dialogFragment = new RatingDialogFragment();
        //dialogFragment.setSalonInfo(salonInfo);
        Bundle bundle =new Bundle();
        bundle.putSerializable("info",salonInfo);
        dialogFragment.setDialog(bundle);
        if(getActivity()!=null && isAdded()){
            dialogFragment.show(getChildFragmentManager(), RatingDialogFragment.class.getSimpleName());

        }
    }*/

    private void showDialog() {
        RatingDialogFragment dialogFragment = new RatingDialogFragment();
        Bundle bundle =new Bundle();
        bundle.putSerializable("info",salonInfo);
        dialogFragment.setDialog(bundle);
        if(getActivity()!=null && isAdded()){
            dialogFragment.show(getChildFragmentManager(), RatingDialogFragment.class.getSimpleName());

        }
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
        myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(salonInfo.getUserId()).child(Constants.Comments);
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
    public void onAdd(SPRegistrationModel salonInformation, String userComment, float rating){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference clientRef;
        DatabaseReference ownertRef;

        clientRef  = database.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid()).child(Constants.Comments);
        ownertRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(salonInfo.getUserId()).child(Constants.Comments);
        String commentId = clientRef.push().getKey();

        CommentModel commentModel = new CommentModel();
        commentModel.setCommentId(commentId);
        commentModel.setComment(userComment);
        commentModel.setNumStars(rating);
        commentModel.setCommentDate(getCurrentDate());
        commentModel.setOwnerId(salonInfo.getUserId());
        commentModel.setClientId(mFirebaseUser.getUid());

        clientRef.child(commentId).setValue(commentModel);
        ownertRef.child(commentId).setValue(commentModel);
        showGreetingDialog();
    }

    private void showGreetingDialog() {
        GreetingDialogFragment dialogFragment = new GreetingDialogFragment();
        dialogFragment.getDialogText(1);
        dialogFragment.show(getChildFragmentManager(), GreetingDialogFragment.class.getSimpleName());
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (status < 200) {

                    status += 1;

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (status == 100) {
                                //  dismiss();
                                enableAddComment();
                            }
                        }
                    });
                }
            }
        }).start();
    }

    private void enableAddComment() {
        add.setVisibility(View.INVISIBLE);
    }
    @Override
    public void onUpdate(CommentModel commentModel, String userComment, float rating) {
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
                Toast.makeText(mContext, "updated comment", Toast.LENGTH_SHORT).show();
            }
        });

     /*   UpdateCommentFragment dialogFragment = new UpdateCommentFragment(RatingFragment.this);
        Bundle bundle =new Bundle();
        bundle.putInt("dismiss",1);
        dialogFragment.setDialogOptions(bundle);
        if(getActivity()!=null && isAdded()){
            dialogFragment.show(getChildFragmentManager(), UpdateCommentFragment.class.getSimpleName());
        }
        DialogToUpdateCommentFragment fragment = new DialogToUpdateCommentFragment(RatingFragment.this);
        Bundle bundle2 =new Bundle();
        bundle2.putInt("dismiss",2);
        fragment.setDialogOptions(bundle2);
        if(getActivity()!=null && isAdded()) {
            fragment.show(getChildFragmentManager(), DialogToUpdateCommentFragment.class.getSimpleName());
        }*/

    }

    private String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
        return df.format(c);
    }

    @Override
    public void onUpdateClickDialog() {
         readCommentFromUser();

    }
}