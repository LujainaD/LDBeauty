package com.lujaina.ldbeauty.AppOwner;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Dialogs.GreetingDialogFragment;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;


public class EditAppOwnerProfileFragment extends Fragment {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^" + "(?=.*[A-Z])" + ".{6,20}");
    private static final Pattern PHONENUMBER_PATTERN = Pattern.compile("^[+]?[0-9]{8,20}$");
    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );
    private FirebaseAuth mAuth;
    private MediatorInterface mMediatorInterface;
    FirebaseUser mFirebaseUser;
    private SPRegistrationModel currentUserInfo;

    private ProgressDialog progressDialog;
    private int status = 0;
    Handler handler = new Handler();


    public EditAppOwnerProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
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
        View parentView = inflater.inflate(R.layout.fragment_edit_app_owner_profile, container, false);
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav);
        navBar.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.All_Users).child(mAuth.getUid());
        ImageButton back = parentView.findViewById(R.id.ib_back);
        final Button save = parentView.findViewById(R.id.btn_save);

        final EditText appOwnerName =parentView.findViewById(R.id.et_name);
        final EditText appOwnerEmail =parentView.findViewById(R.id.et_email);
        final EditText appOwnerPhone =parentView.findViewById(R.id.et_phone);
        final EditText password =parentView.findViewById(R.id.et_pass);
        final TextView updateDate =parentView.findViewById(R.id.date);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediatorInterface.onBackPressed();
            }
        });

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUserInfo = snapshot.getValue(SPRegistrationModel.class);
                if(currentUserInfo!= null){
                    appOwnerName.setText(currentUserInfo.getUserName());
                    appOwnerEmail.setText(currentUserInfo.getUserEmail());
                    appOwnerPhone.setText(currentUserInfo.getPhoneNumber());
                    if(currentUserInfo.getUpdatedDate() == null){
                        updateDate.setText("--");
                    }
                    updateDate.setText(currentUserInfo.getUpdatedDate());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

/*
        editPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseUser = mAuth.getCurrentUser();
                final String newPassword = password.getText().toString();

                if (newPassword.isEmpty()) {
                    password.setError("write your new password");
                } else if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
                    password.setError("weak password(must contain a digit ,uppercase characters and at least 6 characters)");
                } else {

                    progressDialog = new ProgressDialog(getContext());
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.custom_progress_dialog);
                    final TextView progressText = progressDialog.findViewById(R.id.tv_bar);
                    final TextView progressPercentage = progressDialog.findViewById(R.id.tv_progress);
                    progressText.setText("Updating ...");
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

                    mFirebaseUser.updatePassword(newPassword)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        showGreetingPasswordDialog();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(),"Failed", Toast.LENGTH_SHORT).show();
                                        Log.d("password-error", task.getException().toString());
                                    }
                                }
                            });
                }
            }
        });
*/



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = appOwnerName.getText().toString().trim();
                String phone = appOwnerPhone.getText().toString().trim();
                String newEmail = appOwnerEmail.getText().toString().trim();
                final String newPassword = password.getText().toString();

                SPRegistrationModel model = currentUserInfo;
                model.setUserName(name);
                model.setPhoneNumber(phone);
                model.setUpdatedDate(getCurrentDate());
                model.setUserEmail(newEmail);

                progressDialog = new ProgressDialog(getContext());
                progressDialog.setCancelable(false);
                progressDialog.setContentView(R.layout.custom_progress_dialog);
                final TextView progressText = (TextView) progressDialog.findViewById(R.id.tv_bar);
                final TextView progressPercentage = progressDialog.findViewById(R.id.tv_progress);
                progressText.setText("Updating ...");

                if (name.isEmpty()) {
                    appOwnerName.setError("Enter User Name");
                } else if (phone.isEmpty()) {
                    appOwnerPhone.setError("Enter your phone number");
                } else if (!PHONENUMBER_PATTERN.matcher(phone).matches()) {
                    appOwnerPhone.setError("Your phone number must contain at least 8 digit");
                }else if(!newPassword.isEmpty() &&!PASSWORD_PATTERN.matcher(newPassword).matches()) {
                    password.setError("weak password(must contain a digit ,uppercase characters and at least 6 characters)");
                } else if (!EMAIL_ADDRESS_PATTERN.matcher(newEmail).matches()) {
                    appOwnerEmail.setError("Please enter a valid email address");
                }else {
                    progressDialog.show();
                    updatInfoInDB(model,newPassword,newEmail);

                }




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
            }
        });


        return parentView;
    }


/*
    private void updatOwnerInfo(SPRegistrationModel update) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.All_Users).child(mAuth.getUid());

        if (update.getUserEmail().equals(currentUserInfo.getUserEmail())) {
            myRef.child("phoneNumber").setValue(update.getPhoneNumber());
            myRef.child("userEmail").setValue(update.getUserEmail());
            myRef.child("userId").setValue(currentUserInfo.getUserId());
            myRef.child("userName").setValue(update.getUserName());
            myRef.child("userType").setValue(currentUserInfo.getUserType());
            myRef.child("updatedDate").setValue(getCurrentDate()).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                      //  changeEmail(update);
                         showGreetingDialog();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Failed updating your info", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            changeEmail(update);
        }

    }
*/

    private void changeEmail(SPRegistrationModel update, String newEmail, String newPassword) {
        mFirebaseUser = mAuth.getCurrentUser();
        mFirebaseUser.updateEmail(update.getUserEmail())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                           // updatInfoInDB(update, newPassword, newEmail);

                            changePassword(newPassword);
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(),"Failed", Toast.LENGTH_SHORT).show();
                            Log.d("password-error", task.getException().toString());
                        }
                    }
                });
    }

    private void changePassword(String newPassword) {
        if(!newPassword.isEmpty()) {
            mFirebaseUser.updatePassword(newPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                showGreetingDialog();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(),"Failed", Toast.LENGTH_SHORT).show();
                                Log.d("password-error", task.getException().toString());
                            }
                        }
                    });
        }else {
            progressDialog.dismiss();
            showGreetingDialog();
        }


    }

    private void updatInfoInDB(SPRegistrationModel update, String newPassword, String newEmail) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.All_Users).child(mAuth.getUid());

            myRef.child("phoneNumber").setValue(update.getPhoneNumber());
            myRef.child("userEmail").setValue(update.getUserEmail());
            myRef.child("userId").setValue(currentUserInfo.getUserId());
            myRef.child("userName").setValue(update.getUserName());
            myRef.child("userType").setValue(currentUserInfo.getUserType());
            myRef.child("updatedDate").setValue(update.getUpdatedDate()).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                       /* progressDialog.dismiss();
                        showGreetingDialog();*/
                       changeEmail(update,newEmail,newPassword);
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Failed updating your info", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }


    private void showGreetingDialog() {
        GreetingDialogFragment dialogFragment = new GreetingDialogFragment();
        dialogFragment.getDialogText(2);
        dialogFragment.show(getChildFragmentManager(), GreetingDialogFragment.class.getSimpleName());
        final int[] status = {0};
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (status[0] < 200) {

                    status[0] += 1;

                    try {
                        Thread.sleep(23);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (status[0] == 100) {
                                backToProfile();
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

    private void showGreetingPasswordDialog() {
        GreetingDialogFragment dialogFragment = new GreetingDialogFragment();
        dialogFragment.getDialogText(3);
        dialogFragment.show(getChildFragmentManager(), GreetingDialogFragment.class.getSimpleName());
        final int[] status = {0};
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (status[0] < 200) {

                    status[0] += 1;

                    try {
                        Thread.sleep(23);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (status[0] == 100) {
                                backToProfile();
                            }
                        }
                    });
                }
            }
        }).start();
    }

    private void backToProfile() {
        mMediatorInterface.changeFragmentTo(new AppOwnerProfileFragment(), AppOwnerProfileFragment.class.getSimpleName());
    }
}