package com.lujaina.ldbeauty.SP;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.lujaina.ldbeauty.HomeActivity;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.R;

import java.util.regex.Pattern;


public class SPSignUpFragment extends Fragment {
    private Context mContext;
    private MediatorInterface mMediatorInterface;
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
        ImageView ownerImg = parentView.findViewById(R.id.iv_user);
        ImageView salonImg = parentView.findViewById(R.id.iv_salon);
        final TextInputEditText userName = parentView.findViewById(R.id.ti_userName);
        final TextInputEditText userEmail = parentView.findViewById(R.id.ti_userEmail);
        final TextInputEditText userPass = parentView.findViewById(R.id.ti_password);
        final TextInputEditText userVerify = parentView.findViewById(R.id.ti_verify);
        final TextInputEditText userPhone = parentView.findViewById(R.id.ti_phone);
        TextInputEditText salonName = parentView.findViewById(R.id.ti_salonName);
        TextInputEditText salonCity = parentView.findViewById(R.id.ti_city);
        TextInputEditText salonPhone = parentView.findViewById(R.id.ti_salonNumper);
        Button signUp = parentView.findViewById(R.id.btn_signUp);
        TextView login = parentView.findViewById(R.id.tv_login);



        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mMediatorInterface != null) {
                    String name = userName.getText().toString();
                    final String email = userEmail.getText().toString();

                    String password = userPass.getText().toString();
                    String verifyPassword = userVerify.getText().toString();
                    String phoneNumber = userPhone.getText().toString();

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
                    } else {

                        Toast.makeText(mContext, "Welcome To LD Beauty App", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        startActivity(intent);

                    }
                }
            }
        });


        return parentView;
    }
}