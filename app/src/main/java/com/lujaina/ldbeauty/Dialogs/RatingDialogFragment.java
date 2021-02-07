package com.lujaina.ldbeauty.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Models.CommentModel;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;
import com.lujaina.ldbeauty.User.RatingFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class RatingDialogFragment extends DialogFragment {
    FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
    private DatabaseReference myRef;

    private Context mContext;

    private SPRegistrationModel salonInfo;

    private Bundle bundle;
    private int status = 0;
    private setFeedback mListener;

    public RatingDialogFragment() {
        // Required empty public constructor
    }

    public RatingDialogFragment(RatingFragment ratingFragment, SPRegistrationModel salonInfo) {
        mListener= (setFeedback) ratingFragment;
        this.salonInfo= salonInfo;
    }

    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
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
        View parentView = inflater.inflate(R.layout.fragment_rating_dialog, container, false);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        final EditText comment = parentView.findViewById(R.id.et_comment);
        final RatingBar ratingbar = parentView.findViewById(R.id.ratingBar);
         Button add = parentView.findViewById(R.id.btn_add);
        Button cancel = parentView.findViewById(R.id.btn_cancel);
        salonInfo = (SPRegistrationModel) bundle.getSerializable("info");


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userComment = comment.getText().toString().trim();
                float rating = (float) ratingbar.getRating();
                if(userComment.isEmpty()){
                    comment.setError("you need to write a comment");
                }else {
                      //  addCommentToFB(userComment, rating);

                    if(mListener!= null){
                        dismiss();
                        mListener.onAdd(salonInfo,userComment,rating);
                    }

                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });



        return parentView;
    }

    private void addCommentToFB(String userComment, float rating) {
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
        dismiss();

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
                            }
                        }
                    });
                }
            }
        }).start();
    }

    private String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
        return df.format(c);
    }

    public void setDialog(Bundle bundle) {
        this.bundle=bundle;
    }

    /*public void setSalonInfo(SPRegistrationModel salonInfo) {
        this.salonInfo = salonInfo;
    }
*/


    public void setFeedbackAndRating(setFeedback listener){
        mListener=listener;
    }
    public interface setFeedback {
        void onAdd(SPRegistrationModel salonInfo, String userComment, float rating);
    }

}