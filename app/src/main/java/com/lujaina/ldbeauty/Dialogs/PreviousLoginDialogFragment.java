package com.lujaina.ldbeauty.Dialogs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.lujaina.ldbeauty.Client.EditClientProfileFragment;
import com.lujaina.ldbeauty.Models.ClientsAppointmentModel;
import com.lujaina.ldbeauty.R;


public class PreviousLoginDialogFragment extends DialogFragment {

    private FirebaseAuth mAuth;

    private PreviousLoginDialogFragment.selectedButton mListener;

    public PreviousLoginDialogFragment(EditClientProfileFragment editClientProfileFragment) {
        // Required empty public constructor
        mListener =(selectedButton)editClientProfileFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_previous_login_dialog, container, false);
        mAuth = FirebaseAuth.getInstance();

        EditText et_email= parentView.findViewById(R.id.ti_userEmail);
        EditText et_password= parentView.findViewById(R.id.ti_password);
        Button confirm = parentView.findViewById(R.id.btn_add);
        Button cancel = parentView.findViewById(R.id.btn_cancel);

cancel.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        dismiss();
    }
});


confirm.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();

        login(email,password);
    }
});
        return parentView;
    }

    private void login(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        /*progressDialog.setTitle("Welcome Back"+ user.getOwnerName() );
                        progressDialog.show();*/
                        if (task.isSuccessful()) {
                            if(mListener!= null){
                                mListener.onButtonSelected(1);
                            }
                            dismiss();

                            Toast.makeText(getContext(),"great",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            dismiss();
                            //Log.w(KEY_TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getContext(),"incorrect email or password",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void setonClickListener(PreviousLoginDialogFragment.selectedButton listener){
        mListener = listener;
    }
    public interface selectedButton {
    void onButtonSelected(int confirmOrDecline );
}
}