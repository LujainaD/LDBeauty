package com.lujaina.ldbeauty.Client;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.lujaina.ldbeauty.Dialogs.RatingDialogFragment;
import com.lujaina.ldbeauty.Models.CommentModel;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;
import com.lujaina.ldbeauty.User.RatingFragment;


public class UpdateCommentFragment extends DialogFragment {
    FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
    private DatabaseReference myRef;

    private Context mContext;

    private CommentModel commentInfo;

    private Bundle bundle;
    private int status = 0;
    private UpdateCommentFragment.setFeedback mListener;

    public UpdateCommentFragment() {
        // Required empty public constructor
    }

    public UpdateCommentFragment(ClientFeedbackFragment clientFeedbackFragment, CommentModel commentModel) {
        mListener = (setFeedback)clientFeedbackFragment;
    }

    public UpdateCommentFragment(RatingFragment ratingFragment) {
        mListener = (setFeedback) ratingFragment;
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
        if(bundle!= null){
            commentInfo = (CommentModel) bundle.getSerializable("comment");

            if (commentInfo != null) {
                comment.setText(commentInfo.getComment());
                ratingbar.setRating(commentInfo.getNumStars());
            }
        }



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
                        mListener.onUpdate(commentInfo,userComment,rating);

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


    public void setDialog(Bundle bundle) {
        this.bundle=bundle;
    }

    public void setFeedbackAndRating(UpdateCommentFragment.setFeedback listener){
        mListener=listener;
    }


    public interface setFeedback {
        //  void onAdd(SPRegistrationModel salonInfo,CommentModel commentModel);
        void onUpdate( CommentModel commentModel,String userComment, float rating);
    }
}