package com.lujaina.ldbeauty;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;

public class SignUpFragment extends Fragment {
    private Context mContext;
    private MediatorInterface mMediatorInterface;
    private int mSPposition;
    NavController navController;

    public SignUpFragment() {
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
        View parentView =  inflater.inflate(R.layout.fragment_sign_up, container, false);

        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

       String userRole = getActivity().getIntent().getExtras().getString("userType");

       Toolbar toolbar = parentView.findViewById(R.id.toolbar);
        TabLayout tabs = parentView.findViewById(R.id.tab);
        ViewPager viewPager = parentView.findViewById(R.id.viewPager);

        SignUpViewAdapter adapter = new SignUpViewAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);

        Toast.makeText(mContext, userRole, Toast.LENGTH_SHORT).show();

        if(userRole.equals("Client")){
            viewPager.setCurrentItem(0);
        }
        if(userRole.equals("Salon Owner")){
            viewPager.setCurrentItem(1);
        }

        tabs.setupWithViewPager(viewPager);

        return parentView;
    }

}