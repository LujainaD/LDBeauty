package com.lujaina.ldbeauty.SP;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.HomeActivity;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.LoginChoicesFragment;
import com.lujaina.ldbeauty.MainActivity;
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


public class SPSignUpFragment extends Fragment {
    private static final int PICK_OWNER_IMAGE = 1001;
    private static final int PICK_SALON_IMAGE = 1002;
    private static final int STORAGE_PERMISSION_REQUEST = 300;
    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^" + "(?=.*[A-Z])" + ".{6,20}");
    private static final Pattern PHONENUMBER_PATTERN = Pattern.compile("^" + ".{8,20}");

    private FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;

    private int status = 0;
    Handler handler = new Handler();
    private Context mContext;
    private MediatorInterface mMediatorInterface;
    private ProgressDialog progressDialog;

    private CircleImageView ownerImg;
    private CircleImageView salonImg;

    private Uri userImageUri;
    private Uri salonImageUri;


    public SPSignUpFragment() {
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
        View parentView = inflater.inflate(R.layout.fragment_s_p_sign_up, container, false);

        progressDialog = new ProgressDialog(mContext);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        ownerImg = parentView.findViewById(R.id.iv_user);
        salonImg = parentView.findViewById(R.id.iv_salon);
        final EditText userName = parentView.findViewById(R.id.ti_userName);
        final EditText userEmail = parentView.findViewById(R.id.ti_userEmail);
        final EditText userPass = parentView.findViewById(R.id.ti_password);
        final EditText userVerify = parentView.findViewById(R.id.ti_verify);
        final EditText userPhone = parentView.findViewById(R.id.ti_phone);
        final EditText salonName = parentView.findViewById(R.id.ti_salonName);
        final EditText salonCity = parentView.findViewById(R.id.ti_city);
        final EditText salonPhone = parentView.findViewById(R.id.ti_salonNumber);
        Button signUp = parentView.findViewById(R.id.btn_signUp);
        TextView login = parentView.findViewById(R.id.tv_login);

        userName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    userName.setHint("");
                }else{
                    userName.setHint(R.string.username);
                }

            }
        });
        userEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    userEmail.setHint("");
                }else{
                    userEmail.setHint(R.string.userEmail);
                }

            }
        });
        userPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    userPass.setHint("");
                }else{
                    userPass.setHint(R.string.password);
                }

            }
        });
        userVerify.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    userVerify.setHint("");
                }else{
                    userVerify.setHint(R.string.verify);
                }

            }
        });
        userPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    userPhone.setHint("");
                }else{
                    userPhone.setHint(R.string.userPhone);
                }

            }
        });
        salonName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    salonName.setHint("");
                }else{
                    salonName.setHint(R.string.salonName);
                }

            }
        });
        salonCity.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    salonCity.setHint("");
                }else{
                    salonCity.setHint(R.string.salonCity);
                }

            }
        });
        salonPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    salonPhone.setHint("");
                }else{
                    salonPhone.setHint(R.string.salonPhone);
                }

            }
        });



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediatorInterface != null){
                    mMediatorInterface.changeFragmentTo(new LoginChoicesFragment(), LoginChoicesFragment.class.getSimpleName());
                }
            }
        });


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediatorInterface != null) {
                    String name = userName.getText().toString();
                    final String email = userEmail.getText().toString().trim();
                    final String password = userPass.getText().toString().trim();
                    final String verifyPassword = userVerify.getText().toString().trim();
                    final String phoneNumber = userPhone.getText().toString();
                    final String salonname = salonName.getText().toString();
                    final String city = salonCity.getText().toString();
                    final String phoneSalon = salonPhone.getText().toString();
                    String userType = "Salon Owner";

                    if (name.isEmpty()) {
                        userName.setError("Enter User Name");
                    } else if (email.isEmpty()) {
                        userEmail.setError("Enter User Email");
                    } else if (password.isEmpty()) {
                        userPass.setError("Enter PassWord");
                    } else if (verifyPassword.isEmpty()) {
                        userVerify.setError("Enter your password again");
                    } else if (phoneNumber.isEmpty()) {
                        userPhone.setError("Enter your phone number");
                    } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
                        userPass.setError("Weak password(must contain a digit ,uppercase characters and at least 6 characters)");
                    } else if (!EMAIL_ADDRESS_PATTERN.matcher(email).matches()) {
                        userEmail.setError("Please enter a valid email address");
                    } else if (!PHONENUMBER_PATTERN.matcher(phoneNumber).matches()) {
                        userPhone.setError("Your phone number must contain at least 8 digit");
                    } else if (salonname.isEmpty()) {
                        salonName.setError("Enter Salon Name");
                    } else if (city.isEmpty()) {
                        salonCity.setError("Enter Salon city ex.Nizwa,Muscat... ");
                    } else if (phoneSalon.isEmpty()) {
                        salonPhone.setError("Enter Salon Phone Number");
                    } else if (!PHONENUMBER_PATTERN.matcher(phoneSalon).matches()) {
                        salonPhone.setError("Your phone number must contain at least 8 digit");
                    } else if (userImageUri == null) {
                        Toast.makeText(mContext, "please add your profile picture", Toast.LENGTH_SHORT).show();
                    } else if (salonImageUri == null) {
                        Toast.makeText(mContext, "please add salon logo", Toast.LENGTH_SHORT).show();
                    } else {
                        if (password.equals(verifyPassword)) {
                            progressDialog = new ProgressDialog(mContext);
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            progressDialog.setMax(100);
                            progressDialog.setContentView(R.layout.custom_progress_dialog);
                            final TextView progressText = (TextView) progressDialog.findViewById(R.id.tv_bar);
                            final TextView progressPercentage = progressDialog.findViewById(R.id.tv_progress);
                            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            progressText.setText("Welcome To LD Beauty");
                            progressText.setVisibility(View.VISIBLE);

                            SPRegistrationModel registration = new SPRegistrationModel();

                            registerToFirebase(email, password, name, phoneNumber, salonname, city, phoneSalon, userType);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    while (status < 200) {

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
                                                progressPercentage.setText(String.valueOf(status)+ "%");

                                                if (status == 100) {
                                                    progressDialog.dismiss();
                                                }
                                            }
                                        });
                                    }
                                }
                            }).start();

                        } else {
                            userVerify.setError("verify password must be the same as your entered password ");
                            progressDialog.dismiss();
                        }
                    }
                }
            }
        });

        ownerImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPermissionGranted()){
                    openGallery(PICK_OWNER_IMAGE);
                }else {
                    showRunTimePermission();

                }
            }
        });
        salonImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPermissionGranted()){
                    openGallery(PICK_SALON_IMAGE);
                }else {
                    showRunTimePermission();
                }        }
        });
        return parentView;
    }


    private boolean isPermissionGranted() {
        return ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void showRunTimePermission() {
        String[] permissionsArray = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
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

    private void registerToFirebase(final String email, final String password, final String name, final String phoneNumber, final String salonname, final String city, final String phoneSalon, final String userType) {
       Activity currentActivity = getActivity();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(currentActivity, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            SPRegistrationModel registration = new SPRegistrationModel();
                            assert user != null;
                            registration.setUserId(user.getUid());
                            registration.setUserName(name);
                            registration.setUserEmail(email);
                            registration.setPhoneNumber(phoneNumber);
                            registration.setSalonName(salonname);
                            registration.setSalonCity(city);
                            registration.setSalonPhoneNumber(phoneSalon);
                            registration.setUserType(userType);
                            registration.setStatusType("new");
                            registration.setRegistrationDate(getCurrentDate());
                            uploadUserImageToStorage(registration);

                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(mContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
              /*  }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: "+e.getLocalizedMessage());
                }*/
        });

    }

    private void uploadUserImageToStorage(final SPRegistrationModel registration) {
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
								registration.setOwnerImageURL(firebaseImageUri.toString());
								uploadAndGetSaloonImageURL(registration);
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

	private void uploadAndGetSaloonImageURL(final SPRegistrationModel registration) {
		StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
		String imageId2 = UUID.randomUUID().toString();
		final StorageReference salonImageRef = mStorageRef.child("images/" + imageId2 + ".jpg");
        final TextView text = progressDialog.findViewById(R.id.tv_progress);

		salonImageRef.putFile(salonImageUri)
				.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
					@Override
					public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
						salonImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
							@Override
							public void onSuccess(Uri firebaseImageUri) {
								registration.setSalonImageURL(firebaseImageUri.toString());
								addToDB(registration);
							}
						}).addOnFailureListener(new OnFailureListener() {
							@Override
							public void onFailure(@NonNull Exception e) {
								Log.e(TAG, "onFailure in 2nd Image: "+e.getLocalizedMessage() );
							}
						});

					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception exception) {
						Log.e(TAG, "onFailure: "+exception.getLocalizedMessage());
					}
				});
	}

	private void addToDB(SPRegistrationModel registration) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner);

        myRef.child(registration.getUserId()).setValue(registration).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (mMediatorInterface != null) {
                    progressDialog.dismiss();
                    mMediatorInterface.changeFragmentTo(new SPProfileFragment(),SPProfileFragment.class.getSimpleName());


                } else {
                    Toast.makeText(mContext, "failed ", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
            }
        });
    }

    private String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
        return df.format(c);
    }

    public void openGallery(int requestCode ) {
    Intent i = new Intent();
    i.setType("image/*"); // specify the type of data you expect
    i.setAction(Intent.ACTION_GET_CONTENT); // we need to get content from another act.
    startActivityForResult(Intent.createChooser(i, "choose a Picture"),requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_OWNER_IMAGE) {
            if (data == null) {
                Toast.makeText(mContext, "Unexpected Error Happened while selecting  picture!", Toast.LENGTH_SHORT).show();
            } else {
                        Uri ownerImgUri = data.getData();//1
                        userImageUri = ownerImgUri;
                        ownerImg.setImageURI(ownerImgUri);
            }
        }
        else if (requestCode == PICK_SALON_IMAGE){
            if (data == null) {
                Toast.makeText(mContext, "Unexpected Error Happened while selecting  picture!", Toast.LENGTH_SHORT).show();
            }
                Uri salonImgUri = data.getData();//1
                salonImageUri = salonImgUri;
                salonImg.setImageURI(salonImgUri);
        }
    }

}
