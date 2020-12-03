package com.lujaina.ldbeauty.SP;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class EditSalonProfileFragment extends Fragment {
    private static final int PICK_SALON_IMAGE = 1002;
    private static final int STORAGE_PERMISSION_REQUEST = 300;

    private Uri salonImageUri;
    private CircleImageView profileImg;
    private SPRegistrationModel currentUserInfo;

    private FirebaseAuth mAuth;

    public EditSalonProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_edit_salon_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mAuth.getUid());

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUserInfo = snapshot.getValue(SPRegistrationModel.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        profileImg = parentView.findViewById(R.id.profile_img);
        final Button save = parentView.findViewById(R.id.btn_save);
         final EditText salonName =parentView.findViewById(R.id.et_salonName);
        final EditText salonCity =parentView.findViewById(R.id.et_city);
        final EditText salonPhone =parentView.findViewById(R.id.et_phone);


        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(PICK_SALON_IMAGE);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameSalon = salonName.getText().toString().trim();
                String city = salonCity.getText().toString().trim();
                String phone = salonPhone.getText().toString().trim();

                SPRegistrationModel model = currentUserInfo;
                model.setSalonName(nameSalon);
                model.setSalonCity(nameSalon);
                model.setSalonPhoneNumber(nameSalon);

                if (salonImageUri == null) {
                    updatSalonInfo(model);
                } else {
                    uploadSalonImageToStorage(model);
                }
            }
        });


        return parentView;


    }


    private void uploadSalonImageToStorage(final SPRegistrationModel model) {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        //generate unique id for the image
        String imageId = UUID.randomUUID().toString();
        final StorageReference ownerImageRef = mStorageRef.child("images/" + imageId + ".jpg");
        ownerImageRef.putFile(salonImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ownerImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri firebaseImageUri) {
                                model.setSalonImageURL(firebaseImageUri.toString());
                                updatSalonInfo(model);

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

    private void updatSalonInfo(SPRegistrationModel update) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mAuth.getUid());

        myRef.child("ownerImageURL").setValue(currentUserInfo.getOwnerImageURL());
        myRef.child("phoneNumber").setValue(update.getSalonPhoneNumber());
        myRef.child("registrationDate").setValue(currentUserInfo.getRegistrationDate());
        myRef.child("salonCity").setValue(update.getSalonCity());
        myRef.child("salonName").setValue(update.getSalonName());
        myRef.child("salonPhoneNumber").setValue(currentUserInfo.getSalonPhoneNumber());
        myRef.child("statusType").setValue(currentUserInfo.getStatusType());
        myRef.child("userEmail").setValue(currentUserInfo.getUserEmail());
        myRef.child("userId").setValue(currentUserInfo.getUserId());
        myRef.child("userName").setValue(currentUserInfo.getUserName());
        myRef.child("userType").setValue(currentUserInfo.getUserType());
        myRef.child("updatedDate").setValue(getCurrentDate());
        myRef.child("salonImageURL").setValue(update.getSalonImageURL()).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {


                    Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                } else {
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
        return df.format(c);
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
            }
            Uri salonImgUri = data.getData();//1
            salonImageUri = salonImgUri;
            profileImg.setImageURI(salonImgUri);
        }
    }

}