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
import com.lujaina.ldbeauty.HomeActivity;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.CategoryModel;
import com.lujaina.ldbeauty.R;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class AddCategoriesDialogFragment extends DialogFragment {
    private static final int PICK_IMAGE = 1002;
    private static final int STORAGE_PERMISSION_REQUEST = 300;
    private static final ImageView.ScaleType SCALE_TYPE = ImageView.ScaleType.CENTER_CROP;


    private Context mContext;

    private Uri cateImageUri;
    private ProgressDialog progressDialog;
    private FirebaseUser mFirebaseUser;

    private ImageView picture;
    private int status = 0;
    Handler handler = new Handler();
    public AddCategoriesDialogFragment() {
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
        View parentView = inflater.inflate(R.layout.fragment_add_categories_dialog, container, false);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        progressDialog = new ProgressDialog(mContext);
        final EditText et_title = parentView.findViewById(R.id.ti_title);
       picture = parentView.findViewById(R.id.iv_picture);
        picture.setScaleType(SCALE_TYPE);
        Button btnAdd = parentView.findViewById(R.id.btn_add);
        Button btnCancel = parentView.findViewById(R.id.btn_cancel);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = et_title.getText().toString();
                if(title.isEmpty()) {
                    et_title.setError("you should write category name ex.(Hair, spa))");
                }else if (cateImageUri == null) {
                        Toast.makeText(mContext, "please add salon logo", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog = new ProgressDialog(mContext);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.custom_progress_dialog);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    final TextView progressPercentage = progressDialog.findViewById(R.id.tv_progress);
                    CategoryModel category = new CategoryModel();
                    category.setCategoryTitle(title);
                    UploadToFirebaseStorage(category);

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
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
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

    private void UploadToFirebaseStorage(final CategoryModel category) {
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
                                category.setCategoryURL(firebaseImageUri.toString());

                                addToFirebase(category);
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

    private void addToFirebase(CategoryModel category) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid()).child(Constants.Salon_Category);
        String id = myRef.push().getKey();
        category.setCategoryId(id);
        category.setOwnerId(mFirebaseUser.getUid());
        myRef.child(category.categoryId).setValue(category).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(mContext, "Your service category is added successfully ", Toast.LENGTH_SHORT).show();
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


}