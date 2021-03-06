package com.lujaina.ldbeauty;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lujaina.ldbeauty.Interfaces.MediatorInterface;


public class LoginChoicesFragment extends Fragment {

    private Context mContext;
    private MediatorInterface mMediatorInterface;
    NavController navController;

    public LoginChoicesFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
       /* if (context instanceof MediatorInterface) {
            mMediatorInterface = (MediatorInterface) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement MediatorInterface");
        }*/
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_login_choices, container, false);
        if(getActivity().getActionBar()!= null){
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        }
      /*  if(getActivity().getActionBar()== null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        }*/
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

       // Bundle b = getActivity().getIntent().getExtras();
        //String checkDirection = b.getString("dialog");

        Button client = parentView.findViewById(R.id.btn_client);
        Button salonOwner = parentView.findViewById(R.id.btn_salonOwner);
        Button appOwner = parentView.findViewById(R.id.tv_appOwner);
        TextView signup = parentView.findViewById(R.id.tv_SignUp);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if(mMediatorInterface != null){
                    SignUpFragment usertype= new SignUpFragment();
                    usertype.setViewPager("Client");
                    mMediatorInterface.changeFragmentTo(usertype, SignUpFragment.class.getSimpleName());
                }*/
               /* Bundle bundle = new Bundle();
                bundle.putString("userType", "Client");
                navController.navigate(R.id.action_loginChoicesFragment_to_signUpFragment, bundle);*/

                Intent intent = new Intent(getContext(), SignUpActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("userType","Client");
                intent.putExtras(bundle);
                startActivity(intent);
                getActivity().finish();
            }
        });

        client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if(mMediatorInterface != null){
                    LoginFragment usertype= new LoginFragment();
                    usertype.setUserType("Client");
                    mMediatorInterface.changeFragmentTo(usertype, LoginFragment.class.getSimpleName());
                }*/
               /*if(checkDirection=="dialog"){
                   Bundle bundle = new Bundle();
                   bundle.putString("userType", "Client");
                   navController.navigate(R.id.action_loginChoicesFragment2_to_loginFragment2, bundle);

               }else{
                   Bundle bundle = new Bundle();
                   bundle.putString("userType", "Client");
                   navController.navigate(R.id.action_loginChoicesFragment_to_loginFragment, bundle);
               }*/

                /*Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();*/
                Bundle bundle = new Bundle();
                bundle.putString("userType", "Client");
                navController.navigate(R.id.action_loginChoicesFragment_to_loginFragment,bundle);

            }
        });

        salonOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if(mMediatorInterface != null){
                    LoginFragment usertype= new LoginFragment();
                    usertype.setUserType("Salon Owner");
                    mMediatorInterface.changeFragmentTo(usertype, LoginFragment.class.getSimpleName());
                }*/

              /*  if(checkDirection=="dialog"){
                    Bundle bundle = new Bundle();
                    bundle.putString("userType", "Salon Owner");
                    navController.navigate(R.id.action_loginChoicesFragment2_to_loginFragment2, bundle);

                }
                Bundle bundle = new Bundle();
                bundle.putString("userType", "Salon Owner");
                navController.navigate(R.id.action_loginChoicesFragment_to_loginFragment, bundle);
*/
                Bundle bundle = new Bundle();
                bundle.putString("userType", "Salon Owner");
                navController.navigate(R.id.action_loginChoicesFragment_to_loginFragment, bundle);

            }
        });
        appOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(mMediatorInterface != null){
                    LoginFragment usertype= new LoginFragment();
                    usertype.setUserType("App Owner");
                    mMediatorInterface.changeFragmentTo(usertype, LoginFragment.class.getSimpleName());
                }*/

                /*if(checkDirection=="dialog"){
                    Bundle bundle = new Bundle();
                    bundle.putString("userType", "App Owner");
                    navController.navigate(R.id.action_loginChoicesFragment2_to_loginFragment2, bundle);

                }
                Bundle bundle = new Bundle();
                bundle.putString("userType", "App Owner");
                navController.navigate(R.id.action_loginChoicesFragment_to_loginFragment, bundle);
*/
                Bundle bundle = new Bundle();
                bundle.putString("userType", "App Owner");
                navController.navigate(R.id.action_loginChoicesFragment_to_loginFragment, bundle);

            }
        });


        return parentView;
    }

}