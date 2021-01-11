package com.lujaina.ldbeauty.SP;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Dialogs.GreetingDialogFragment;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class EditSpProfileFragment extends Fragment {

    private static final int PICK_SALON_IMAGE = 1002;
    private static final int STORAGE_PERMISSION_REQUEST = 300;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^" + "(?=.*[A-Z])" + ".{6,20}");
    private static final Pattern PHONENUMBER_PATTERN = Pattern.compile("^[+]?[0-9]{8,20}$");

    private Uri ownerImageUri;
    private CircleImageView profileImg;
    private SPRegistrationModel currentUserInfo;

    private FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;

    private ProgressDialog progressDialog;
    private int status = 0;
    Handler handler = new Handler();
    NavController navController;
    public EditSpProfileFragment() {
        // Required empty public constructor
    }

    /*@Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
       *//* if (context instanceof MediatorInterface) {
            mMediatorInterface = (MediatorInterface) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement MediatorInterface");
        }*//*
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_edit_sp_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mAuth.getUid());
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
         navController = navHostFragment.getNavController();
        ImageButton back = parentView.findViewById(R.id.ib_back);
        profileImg = parentView.findViewById(R.id.profile_img);
        final Button save = parentView.findViewById(R.id.btn_save);

        final EditText ownerName =parentView.findViewById(R.id.et_name);
        final TextView ownerEmail =parentView.findViewById(R.id.et_email);
        final EditText ownerPhone =parentView.findViewById(R.id.et_phone);
        final EditText password =parentView.findViewById(R.id.et_pass);

        final TextView updateDate =parentView.findViewById(R.id.date);
        final TextView registerDate =parentView.findViewById(R.id.registerDate);
        final TextView tv_changeImg =parentView.findViewById(R.id.tv_changeImg);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.popBackStack();
            }
        });
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUserInfo = snapshot.getValue(SPRegistrationModel.class);
                if(currentUserInfo!= null){
                    ownerName.setText(currentUserInfo.getUserName());
                    ownerEmail.setText(currentUserInfo.getUserEmail());
                    ownerPhone.setText(currentUserInfo.getPhoneNumber());
                    registerDate.setText(currentUserInfo.getRegistrationDate());
                    if(currentUserInfo.getUpdatedDate() == null){
                        updateDate.setText("--");
                    }
                    updateDate.setText(currentUserInfo.getUpdatedDate());
                    Glide.with(getContext()).load(currentUserInfo.getOwnerImageURL()).into(profileImg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        tv_changeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(PICK_SALON_IMAGE);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ownerName.getText().toString().trim();
                String phone = ownerPhone.getText().toString().trim();
                final String newPassword = password.getText().toString();

                SPRegistrationModel model = currentUserInfo;
                model.setUserName(name);
                model.setPhoneNumber(phone);
                model.setUpdatedDate(getCurrentDate());


                progressDialog = new ProgressDialog(getContext());
                progressDialog.setCancelable(false);
                //progressDialog.show();
                progressDialog.setContentView(R.layout.custom_progress_dialog);
                final TextView progressText = (TextView) progressDialog.findViewById(R.id.tv_bar);
                final TextView progressPercentage = progressDialog.findViewById(R.id.tv_progress);
                progressText.setText("Updating ...");
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                if (name.isEmpty()) {
                    ownerName.setError("Enter User Name");
                } else if (phone.isEmpty()) {
                    ownerPhone.setError("Enter your phone number");
                } else if (!PHONENUMBER_PATTERN.matcher(phone).matches()) {
                    ownerPhone.setError("Your phone number must contain at least 8 digit");
                }else if(!newPassword.isEmpty() &&!PASSWORD_PATTERN.matcher(newPassword).matches()) {
                    password.setError("weak password(must contain a digit ,uppercase characters and at least 6 characters)");
                }
                else{
                    if (ownerImageUri == null) {
                        progressDialog.show();
                        updatOwnerInfo(model,newPassword);
                    } else {
                        progressDialog.show();
                        uploadOwnerImageToStorage(model,newPassword);
                    }
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
        return parentView;
    }

    private void uploadOwnerImageToStorage(final SPRegistrationModel model, String newPassword) {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        //generate unique id for the image
        String imageId = UUID.randomUUID().toString();
        final StorageReference ownerImageRef = mStorageRef.child("images/" + imageId + ".jpg");
        ownerImageRef.putFile(ownerImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ownerImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri firebaseImageUri) {
                                model.setOwnerImageURL(firebaseImageUri.toString());
                                updatOwnerInfo(model, newPassword);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "onFailure: :" + e.getLocalizedMessage());
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: Uploading image Failed"+e.getLocalizedMessage());
            }
        });
    }

    private void updatOwnerInfo(SPRegistrationModel update, String newPassword) {
        mFirebaseUser = mAuth.getCurrentUser();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mAuth.getUid());

        myRef.child("salonImageURL").setValue(currentUserInfo.getSalonImageURL());
        myRef.child("phoneNumber").setValue(update.getPhoneNumber());
        myRef.child("registrationDate").setValue(currentUserInfo.getRegistrationDate());
        myRef.child("salonCity").setValue(currentUserInfo.getSalonCity());
        myRef.child("salonName").setValue(currentUserInfo.getSalonName());
        myRef.child("salonPhoneNumber").setValue(currentUserInfo.getSalonPhoneNumber());
        myRef.child("statusType").setValue(currentUserInfo.getStatusType());
        myRef.child("userEmail").setValue(currentUserInfo.getUserEmail());
        myRef.child("userId").setValue(currentUserInfo.getUserId());
        myRef.child("userName").setValue(update.getUserName());
        myRef.child("userType").setValue(currentUserInfo.getUserType());
        myRef.child("updatedDate").setValue(currentUserInfo.getUpdatedDate());
        myRef.child("ownerImageURL").setValue(update.getOwnerImageURL()).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
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
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Failed updating your info", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
        return df.format(c);
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

    private void backToProfile() {
       navController.popBackStack();
    }

    private void openGallery(int requestCode) {
        Intent i = new Intent();
        i.setType("image/*"); // specify the type of data you expect
        i.setAction(Intent.ACTION_GET_CONTENT); // we need to get content from another act.
        startActivityForResult(Intent.createChooser(i, "choose a Picture"),requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_SALON_IMAGE){
            if (data == null) {
                Toast.makeText(getContext(), "Unexpected Error Happened while selecting  picture!", Toast.LENGTH_SHORT).show();
            }else {
                Uri salonImgUri = data.getData();//1
                ownerImageUri = salonImgUri;
                profileImg.setImageURI(salonImgUri);
            }

        }
    }

}