package com.lujaina.ldbeauty.AppOwner;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lujaina.ldbeauty.Dialogs.AddInfoDialogFragment;
import com.lujaina.ldbeauty.Dialogs.ResetPasswordDialogFragment;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.ProgressDialogFragment;
import com.lujaina.ldbeauty.R;
import com.lujaina.ldbeauty.SP.SPProfileFragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AOLoginFragment extends Fragment {
    private Context mContext;
    private MediatorInterface mMediatorInterface;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private static final String KEY_TAG = "login";
    ProgressDialog progressDialog;

    public AOLoginFragment() {
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
        View parentView = inflater.inflate(R.layout.fragment_a_o_login, container, false);
        final EditText ti_email = parentView.findViewById(R.id.ti_userEmail);
        final EditText ti_password = parentView.findViewById(R.id.ti_password);
        Button login = parentView.findViewById(R.id.btn_login);
        TextView forget = parentView.findViewById(R.id.tv_forget);


        ti_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        ti_email.setHint("");
                    }else{
                        ti_email.setHint(R.string.userEmail);
                    }

            }
        });

        ti_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    ti_password.setHint("");
                }else{
                    ti_password.setHint(R.string.password);
                }

            }
        });


        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(mContext);
                progressDialog.setCancelable(false);
                progressDialog.show();
                progressDialog.setContentView(R.layout.fragment_progress_dialog);
                TextView progressText = (TextView) progressDialog.findViewById(R.id.tv_bar);
                progressText.setText("Welcome Back..");
                progressText.setVisibility(View.VISIBLE);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                if (mMediatorInterface != null) {
                    final String email = ti_email.getText().toString();
                    final String password = ti_password.getText().toString();

                    if (email.isEmpty()) {
                        ti_email.setError("Please write your email");
                    } else if (!isEmailValid(email)) {
                        ti_email.setError("invalid email");
                    } else if (password.isEmpty()) {
                        ti_password.setError("please write your password");
                    }else {
                        if(email.equals("Lujaina.me@hotmail.com") && password.equals("Lujaina95")) {

                            SPRegistrationModel salonOwner = new SPRegistrationModel();
                            salonOwner.setOwnerEmail(email);
                            salonOwner.setPassWord(password);
                            loginUsingFirebaseAuth(email, password);

                        }else{
                            Toast.makeText(mContext, "Sorry your are not the App Owner", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                    }
                }
            }
        });


        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediatorInterface != null){
                    ResetPasswordDialogFragment dialog =new ResetPasswordDialogFragment();
                    dialog.show(getChildFragmentManager(), ResetPasswordDialogFragment.class.getSimpleName());
                }
            }
        });



        return parentView;
    }

    private void loginUsingFirebaseAuth(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(KEY_TAG, "signInWithEmail:success");

                            mFirebaseUser = mAuth.getCurrentUser();
                            progressDialog.dismiss();

                            Toast.makeText(mContext, "Welcome to LD beauty App", Toast.LENGTH_SHORT).show();
                            if(mMediatorInterface != null){
                                mMediatorInterface.changeFragmentTo(new AppOwnerProfileFragment(), AppOwnerProfileFragment.class.getSimpleName());
                            }



                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();

                            Log.w(KEY_TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(mContext,"incorrect email or password",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }

    private boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


}