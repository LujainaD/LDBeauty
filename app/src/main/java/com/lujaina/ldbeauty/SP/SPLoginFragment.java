package com.lujaina.ldbeauty.SP;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Dialogs.ResetPasswordDialogFragment;
import com.lujaina.ldbeauty.HomeActivity;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.MainActivity;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;
import com.lujaina.ldbeauty.SignUpFragment;
import com.lujaina.ldbeauty.User.SalonsHomeFragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SPLoginFragment extends Fragment {
    private static final String KEY_TAG = "login";

    private FirebaseAuth mAuth;

    private Context mContext;
    private MediatorInterface mMediatorInterface;

    private ProgressDialog progressDialog;
    private int status = 0;
    Handler handler = new Handler();
    public SPLoginFragment() {
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
        View parentView = inflater.inflate(R.layout.fragment_s_p_login, container, false);
        final EditText ti_email = parentView.findViewById(R.id.ti_userEmail);
        final EditText ti_password = parentView.findViewById(R.id.ti_password);
        Button login = parentView.findViewById(R.id.btn_login);
        TextView signup = parentView.findViewById(R.id.tv_SignUp);
        TextView forget = parentView.findViewById(R.id.tv_forget);
        mAuth = FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(mMediatorInterface != null){
                    SignUpFragment signUpFragment = new SignUpFragment();
                    signUpFragment.setViewPager("");
                    mMediatorInterface.changeFragmentTo(signUpFragment, SignUpFragment.class.getSimpleName());
                }*/
            }
        });

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


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                        progressDialog = new ProgressDialog(mContext);
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        progressDialog.setContentView(R.layout.custom_progress_dialog);
                        final TextView progressText = (TextView) progressDialog.findViewById(R.id.tv_bar);
                        final TextView progressPercentage = progressDialog.findViewById(R.id.tv_progress);
                        progressText.setText("Welcome Back");
                        progressText.setVisibility(View.VISIBLE);
                        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (status < 100) {

                                    status += 1;

                                    try {
                                        Thread.sleep(200);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            progressDialog.setProgress(status);
                                            progressPercentage.setText(String.valueOf(status)+"%");

                                            if (status == 100) {
                                                progressDialog.dismiss();
                                            }
                                        }
                                    });
                                }
                            }
                        }).start();

                        SPRegistrationModel salonOwner = new SPRegistrationModel();
                        salonOwner.setUserEmail(email);
                        salonOwner.setPassWord(password);
                        loginUsingFirebaseAuth(email, password);
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
                        SPRegistrationModel user = new SPRegistrationModel();
                        /*progressDialog.setTitle("Welcome Back"+ user.getOwnerName() );
                        progressDialog.show();*/
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            String userId = task.getResult().getUser().getUid();

                            checkUserRole(userId);


                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Log.w(KEY_TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(mContext,"incorrect email or password",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkUserRole(String userId) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef;
        myRef = (DatabaseReference) database.getReference(Constants.Users).child(Constants.All_Users).child(userId);

        myRef.orderByChild(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                progressDialog.dismiss();
                SPRegistrationModel model = snapshot.getValue(SPRegistrationModel.class);
                model.getUserType();

                String userRole = model.getUserType();

                mAuth.getCurrentUser();
                if(mAuth!=null) {
                    //Get Currrent User info from Firebase Database
                    SPRegistrationModel currentUser = SPRegistrationModel.getInstance();
                    currentUser.setUserName(model.getUserName());
                    currentUser.setUserEmail(model.getUserEmail());
                    currentUser.setUserId(model.getUserId());
                    currentUser.setUserType(model.getUserType());
                    Toast.makeText(mContext, model.getUserType(), Toast.LENGTH_SHORT).show();
                }


                if(userRole.equals("Client")){
                    progressDialog.dismiss();
                    Toast.makeText(mContext, "u r not Salon Owner", Toast.LENGTH_SHORT).show();

                }else if(userRole.equals("Salon Owner")){
                    if (mMediatorInterface != null) {
                        progressDialog.dismiss();
                        SalonsHomeFragment clientInfo = new SalonsHomeFragment();
                        mMediatorInterface.changeFragmentTo(new SalonsHomeFragment(),SalonsHomeFragment.class.getSimpleName());


                    }
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(mContext, "u r not registered", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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