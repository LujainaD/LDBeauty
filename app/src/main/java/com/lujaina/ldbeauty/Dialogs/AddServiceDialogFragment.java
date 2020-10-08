package com.lujaina.ldbeauty.Dialogs;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.CategoryModel;
import com.lujaina.ldbeauty.Models.ServiceModel;
import com.lujaina.ldbeauty.R;

import java.util.UUID;

import static android.content.ContentValues.TAG;


public class AddServiceDialogFragment extends DialogFragment {
    ImageView picture;
    private Context mContext;
    private MediatorInterface mMediatorInterface;
    private static final int PICK_IMAGE = 1002;
    private static final int STORAGE_PERMISSION_REQUEST = 300;
    private Uri cateImageUri;
    ProgressDialog progressDialog;
    FirebaseUser mFirebaseUser;
    private FirebaseAuth mAuth;
    String title;
    String specialist;
    String price;
    private CategoryModel mCategory;

    public AddServiceDialogFragment() {
        // Required empty public constructor
    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
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
        View parentView = inflater.inflate(R.layout.fragment_add_service_dialog, container, false);
        progressDialog = new ProgressDialog(mContext);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        final EditText et_title = parentView.findViewById(R.id.ti_title);
        final EditText et_name = parentView.findViewById(R.id.ti_name);
        final EditText et_price = parentView.findViewById(R.id.ti_price);
        picture = parentView.findViewById(R.id.iv_picture);
        Button btnAdd = parentView.findViewById(R.id.btn_add);
        Button btnCancel = parentView.findViewById(R.id.btn_cancel);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                title = et_title.getText().toString();
                specialist = et_name.getText().toString();
                price = et_price.getText().toString();

                if(title.isEmpty()) {
                    et_title.setError("you should write service name ex.(Hair cut, hair coloring))");
                }else if (specialist.isEmpty()) {
                    et_title.setError("you should write specialist name)");
                }else if (price.isEmpty()) {
                    et_title.setError("you should write the price)");
                }else if (cateImageUri == null) {
                    Toast.makeText(mContext, "please add service picture", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog = new ProgressDialog(mContext);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.custom_progress_dialog);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    ServiceModel service = new ServiceModel();
                    service.setServiceTitle(title);
                    service.setServicePrice(price);
                    service.setServiceSpecialist(specialist);
                    uploadToStorage(service);

                }
            }
        });
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPermissionGranted()){
                    openGallery(PICK_IMAGE);
                }else {
                    showRunTimePermission();
                }
            }
        });

        return parentView;
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
        if (requestCode == PICK_IMAGE) {
            if (data == null) {
                Toast.makeText(mContext, "Unexpected Error Happened while selecting  picture!", Toast.LENGTH_SHORT).show();
            } else {
                Uri ownerImgUri = data.getData();//1
                cateImageUri = ownerImgUri;
                picture.setImageURI(ownerImgUri);
            }
        }
    }


    private boolean isPermissionGranted() {
        return ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void showRunTimePermission() {
        // Permission is not Granted !
        // we should Request the Permission!
        // put all permissions you need in this Screen into string array
        String[] permissionsArray = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //here we requet the permission
        requestPermissions(permissionsArray, STORAGE_PERMISSION_REQUEST);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // user grants the Permission!
            // you can call the function to write/read to storage here!
        } else {
            // user didn't grant the Permission we need
            Toast.makeText(mContext, "didn't grant the Permission", Toast.LENGTH_LONG).show();
        }
    }

    private void uploadToStorage(final ServiceModel service) {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        //generate unique id for the image
        String imageId = UUID.randomUUID().toString();

        final StorageReference ownerImageRef = mStorageRef.child("images/" + imageId + ".jpg");

        ownerImageRef.putFile(cateImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ownerImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri firebaseImageUri) {
                                service.setServiceURL(firebaseImageUri.toString());

                                addToFirebase(service);
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

    private void addToFirebase(ServiceModel service) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid()).child(Constants.Salon_Category).child(mCategory.getCategoryId()).child(Constants.Salon_Service);
        String id = myRef.push().getKey();
        service.setServiceId(id);
        service.setIdCategory(mCategory.getCategoryId());
        service.setServiceTitle(title);
        service.setServiceSpecialist(specialist);
        service.setServicePrice(price);
        service.setOwnerId(mFirebaseUser.getUid());
        myRef.child(service.serviceId).setValue(service).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(mContext, "Your service service is added successfully ", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "failed ", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });


    }

    public void setService(CategoryModel category) {
        mCategory = category;
    }
}