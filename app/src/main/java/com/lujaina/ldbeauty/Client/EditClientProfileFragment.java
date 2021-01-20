package com.lujaina.ldbeauty.Client;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import com.lujaina.ldbeauty.Dialogs.ImageDialogFragment;
import com.lujaina.ldbeauty.Dialogs.PreviousLoginDialogFragment;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;


public class EditClientProfileFragment extends Fragment implements ImageDialogFragment.ChooseDialogInterface{
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int MY_CAMERA_REQUEST_CODE = 1;

    private static final int PICK_SALON_IMAGE = 1002;
    private static final int STORAGE_PERMISSION_REQUEST = 300;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^" + "(?=.*[A-Z])" + ".{6,20}");
    private static final Pattern PHONENUMBER_PATTERN = Pattern.compile("^[+]?[0-9]{8,20}$");
            //("^" + ".{8,20}");

    private Uri userImageUri;
    private CircleImageView profileImg;
    private SPRegistrationModel currentUserInfo;

    private FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;

    private ProgressDialog progressDialog;
    private int status = 0;
    Handler handler = new Handler();
    private String mImagePath;
    private SPRegistrationModel info;
    NavController navController;

    public EditClientProfileFragment() {
        // Required empty public constructor
    }



    @SuppressLint("UseCompatLoadingForDrawables")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_edit_client_profile, container, false);
        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_nav);
        navBar.setVisibility(View.GONE);
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Client).child(mAuth.getUid());

        ImageButton back = parentView.findViewById(R.id.ib_back);
        profileImg = parentView.findViewById(R.id.profile_img);
        final Button save = parentView.findViewById(R.id.btn_save);
      //  final Button editPassword = parentView.findViewById(R.id.btn_password);

        final EditText userName =parentView.findViewById(R.id.et_name);
        final TextView userEmail =parentView.findViewById(R.id.et_email);
        final EditText userPhone =parentView.findViewById(R.id.et_phone);
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
                    userName.setText(currentUserInfo.getUserName());
                    userEmail.setText(currentUserInfo.getUserEmail());
                    userPhone.setText(currentUserInfo.getPhoneNumber());
                    registerDate.setText(currentUserInfo.getRegistrationDate());
                    updateDate.setText(currentUserInfo.getUpdatedDate());
                    Glide.with(getContext())
                            .load(currentUserInfo.getOwnerImageURL())
                            .error(R.drawable.profile)
                            .into(profileImg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tv_changeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageDialogFragment dialog = new ImageDialogFragment();
                dialog.setChooseDialogListener((ImageDialogFragment.ChooseDialogInterface) EditClientProfileFragment.this);
                dialog.show(getChildFragmentManager(), ImageDialogFragment.class.getSimpleName());
            }
                //openGallery(PICK_SALON_IMAGE);
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = userName.getText().toString().trim();
                String phone = userPhone.getText().toString().trim();
                final String newPassword = password.getText().toString();

                SPRegistrationModel model = currentUserInfo;
                model.setUserName(name);
                model.setPhoneNumber(phone);
                model.setUpdatedDate(getCurrentDate());

                /*progressDialog = new ProgressDialog(getContext());
                progressDialog.setCancelable(false);
               // progressDialog.show();
                progressDialog.setContentView(R.layout.custom_progress_dialog);
                final TextView progressText = progressDialog.findViewById(R.id.tv_bar);
                final TextView progressPercentage = progressDialog.findViewById(R.id.tv_progress);
                progressText.setText("Updating ...");
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
*/
                if (name.isEmpty()) {
                    userName.setError("Enter User Name");
                } else if (phone.isEmpty()) {
                    userPhone.setError("Enter your phone number");
                } else if (!PHONENUMBER_PATTERN.matcher(phone).matches()) {
                    userPhone.setError("Your phone number must contain at least 8 digit");
                }else if(!newPassword.isEmpty() &&!PASSWORD_PATTERN.matcher(newPassword).matches()) {
                        password.setError("weak password(must contain a digit ,uppercase characters and at least 6 characters)");
                }
                else{
                    if (userImageUri == null) {
                        progressDialog = new ProgressDialog(getContext());
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        progressDialog.setContentView(R.layout.custom_progress_dialog);
                        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        final TextView progressText = (TextView) progressDialog.findViewById(R.id.tv_bar);
                        final TextView progressPercentage = progressDialog.findViewById(R.id.tv_progress);
                        progressText.setText("Updating ...");
                        progressDialog.show();
                        updatUserInfo(model,newPassword);
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
                    } else {
                        progressDialog = new ProgressDialog(getContext());
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        progressDialog.setContentView(R.layout.custom_progress_dialog);
                        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        final TextView progressText = (TextView) progressDialog.findViewById(R.id.tv_bar);
                        final TextView progressPercentage = progressDialog.findViewById(R.id.tv_progress);
                        progressText.setText("Updating ...");
                        progressDialog.show();
                        uploadUserImageToStorage(model,newPassword);
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


            }
        });

        return parentView;
    }


    private void uploadUserImageToStorage(final SPRegistrationModel model, String newPassword) {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        //generate unique id for the image
        String imageId = UUID.randomUUID().toString();
        final StorageReference ownerImageRef = mStorageRef.child("images/" + imageId + ".jpg");
        ownerImageRef.putFile(userImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ownerImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri firebaseImageUri) {
                                model.setOwnerImageURL(firebaseImageUri.toString());
                                updatUserInfo(model, newPassword);

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

    private void updatUserInfo(SPRegistrationModel update, String newPassword) {
        mFirebaseUser = mAuth.getCurrentUser();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Client).child(mAuth.getUid());

        myRef.child("phoneNumber").setValue(update.getPhoneNumber());
        myRef.child("registrationDate").setValue(currentUserInfo.getRegistrationDate());
        myRef.child("salonPhoneNumber").setValue(currentUserInfo.getSalonPhoneNumber());
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
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (status < 200) {

                    status += 1;

                    try {
                        Thread.sleep(23);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (status == 100) {
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

    private void gallery(int requestCode) {
        Intent i = new Intent();
        i.setType("image/*"); // specify the type of data you expect
        i.setAction(Intent.ACTION_GET_CONTENT); // we need to get content from another act.
        startActivityForResult(Intent.createChooser(i, "choose a Picture"),requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_SALON_IMAGE){
            if (data == null) {
                Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
            }else {
                Uri salonImgUri = data.getData();//1
                userImageUri = salonImgUri;
                profileImg.setImageURI(salonImgUri);
                /*Glide.with(Objects.requireNonNull(getContext()))
                        .load(userImageUri)
                        .error(R.drawable.profile)
                        .into(profileImg);*/

            }

        }else if (requestCode == MY_CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
           // Bundle extras = data.getExtras();
            galleryAddPic();
           /* Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            profileImg.setImageBitmap(imageBitmap);
            Glide.with(Objects.requireNonNull(getContext()))
                    .load(userImageUri)
                    .error(R.drawable.profile)
                    .into(profileImg);*/
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        userImageUri = contentUri;
        mediaScanIntent.setData(userImageUri);
        //sendBroadcast(mediaScanIntent);
        //startActivityForResult(Intent.createChooser(mediaScanIntent, "tack Picture"),requestCode);

        setPic();
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = profileImg.getWidth();
        int targetH = profileImg.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(currentPhotoPath);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
       // bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
        //profileImg.setLayerType(View.LAYER_TYPE_SOFTWARE , null);
        profileImg.setImageBitmap(bitmap);
    }


    @Override
    public void openGallery() {

        gallery(PICK_SALON_IMAGE);
    }

    @Override
    public void onCameraButtonClick() {
        if (isPermissionGranted()) {
            dispatchTakePictureIntent();
        } else {
            showRunTimePermission();
        }
    }

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.lujaina.ldbeauty.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, MY_CAMERA_REQUEST_CODE);
            }
        }
    }

    private boolean isPermissionGranted() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public void showRunTimePermission() {
        // Permission is not Granted !
        // we should Request the Permission!
        // put all permissions you need in this Screen into string array
        String[] permissionsArray = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        requestPermissions(new String[]{Manifest.permission.CAMERA},
                MY_CAMERA_REQUEST_CODE );

        //here we requet the permission
       // requestPermissions(permissionsArray, STORAGE_PERMISSION_REQUEST);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_CAMERA_REQUEST_CODE :

                dispatchTakePictureIntent();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}