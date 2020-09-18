package com.lujaina.ldbeauty.SP;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.HomeActivity;
import com.lujaina.ldbeauty.Dialogs.ImageDialogFragment;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;


public class SPSignUpFragment extends Fragment implements ImageDialogFragment.ChooseDialogInterface{
    private static final int PICK_IMAGE = 100;
    ImageView ownerImg;
    private Uri mImageUri;
    private FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;

    FirebaseUser user;
    private Context mContext;
    private MediatorInterface mMediatorInterface;

    TextInputEditText userName ;
            TextInputEditText userEmail;
    TextInputEditText userPass ;
            TextInputEditText userVerify;
    TextInputEditText userPhone;
    TextInputEditText salonName;
    TextInputEditText salonCity;
    TextInputEditText salonPhone;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^" + "(?=.*[A-Z])" + ".{6,20}");
    private static final Pattern PHONENUMBER_PATTERN = Pattern.compile("^" + ".{8,20}");
    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );
    public SPSignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if(context instanceof MediatorInterface) {
            mMediatorInterface = (MediatorInterface) context;
        }
        else{
            throw new RuntimeException(context.toString()+ "must implement MediatorInterface");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_s_p_sign_up, container, false);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        ownerImg = parentView.findViewById(R.id.iv_user);
        ImageView salonImg = parentView.findViewById(R.id.iv_salon);
        final TextInputEditText userName = parentView.findViewById(R.id.ti_userName);
        final TextInputEditText userEmail = parentView.findViewById(R.id.ti_userEmail);
        final TextInputEditText userPass = parentView.findViewById(R.id.ti_password);
        final TextInputEditText userVerify = parentView.findViewById(R.id.ti_verify);
        final TextInputEditText userPhone = parentView.findViewById(R.id.ti_phone);
        final TextInputEditText salonName = parentView.findViewById(R.id.ti_salonName);
        final TextInputEditText salonCity = parentView.findViewById(R.id.ti_city);
        final TextInputEditText salonPhone = parentView.findViewById(R.id.ti_salonNumber);
        Button signUp = parentView.findViewById(R.id.btn_signUp);
        TextView login = parentView.findViewById(R.id.tv_login);



        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mMediatorInterface != null) {
                    String name = userName.getText().toString();
                    final String email = userEmail.getText().toString();
                    final  String password = userPass.getText().toString();
                    final  String verifyPassword = userVerify.getText().toString();
                    final  String phoneNumber = userPhone.getText().toString();
                    final  String salonname = salonName.getText().toString();
                    final  String city = salonCity.getText().toString();
                    final  String phoneSalon = salonPhone.getText().toString();

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
                    } else {
                        if (password.equals(verifyPassword)) {

                            SPRegistrationModel registration = new SPRegistrationModel();
                            registration.setOwnerName(name);
                            registration.setOwnerEmail(email);
                            registration.setPassWord(password);
                            registration.setPhoneNumber(phoneNumber);
                            registration.setSalonName(salonname);
                            registration.setSalonCity(city);
                            registration.setSalonPhoneNumber(phoneSalon);
                            registration.setUserType(userType);
                            registerToFirebase(email, password, name, phoneNumber, salonname, city, phoneSalon, userType);


                        } else {
                            userVerify.setError("verify password must be the same as your entered password ");

                        }
                    }
                }
            }
        });

        ownerImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediatorInterface != null){
                    ImageDialogFragment dialogFragment = new ImageDialogFragment();
                    dialogFragment.setChooseDialogListener(SPSignUpFragment.this);
                    dialogFragment.show(getChildFragmentManager(), ImageDialogFragment.class.getSimpleName());
                }
            }
        });


        return parentView;
    }

    private void registerToFirebase(final String userType, final String email, final String password, final String name, final String phoneNumber, final String salonname, final String city, final String phoneSalon) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(Objects.requireNonNull(getActivity()), new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            SPRegistrationModel registration = new SPRegistrationModel();
                            assert user != null;
                            registration.setOwnerId(user.getUid());
                           /* registration.setOwnerName(name);
                            registration.setOwnerEmail(email);
                            registration.setPhoneNumber(phoneNumber);
                            registration.setSalonName(salonname);
                            registration.setSalonCity(city);
                            registration.setSalonPhoneNumber(phoneSalon);
                            registration.setUserType(userType);
                            registration.setRegistrationDate(getCurrentDate());*/

/*
                            AddToDB(registration);
*/


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(mContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });

    }

    private void AddToDB(SPRegistrationModel registration) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner);
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Welcome To LD Beauty");
        progressDialog.show();
        myRef.child(registration.getOwnerId()).setValue(registration).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(mMediatorInterface != null){

                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    startActivity(intent);
                    progressDialog.dismiss();

                }else {
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
    @Override
    public void openGallery() {
        Intent i = new Intent();
        i.setType("image/*"); // specify the type of data you expect
        i.setAction(Intent.ACTION_GET_CONTENT); // we need to get content from another act.
        startActivityForResult(Intent.createChooser(i, "choose App"), PICK_IMAGE);
    }

    @Override
    public void onCameraButtonClick() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if image from Camera
/*
        if (requestCode == CAPTURE_IMAGE) {
            if (data == null) {
                Toast.makeText(mContext, "Unexpected Error Happened while capturing the picture!", Toast.LENGTH_SHORT).show();
            } else {

                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                salonImage.setImageBitmap(imageBitmap);

                mImageUri = data.getParcelableExtra("info"); //uri
                salonImage.setImageBitmap(imageBitmap);
                Log.d("img-uri", mImageUri.toString());

            }

        } else if (requestCode == PICK_IMAGE) {
*/

            if (data == null) {
                Toast.makeText(mContext, "Unexpected Error Happened while selecting  picture!", Toast.LENGTH_SHORT).show();

            } else {

                try {
                    Uri imgUri = data.getData();//1
                    InputStream imageStream = mContext.getContentResolver().openInputStream(imgUri);//2
                    Bitmap selectedImageBitmap = BitmapFactory.decodeStream(imageStream);//3}
                    mImageUri = imgUri;
                    ownerImg.setImageURI(imgUri);
/*
                    uploadImage();
*/

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }


    }

