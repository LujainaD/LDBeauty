package com.lujaina.ldbeauty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.lujaina.ldbeauty.Client.UserSignUpFragment;
import com.lujaina.ldbeauty.SP.SPSignUpFragment;

public class SignUpViewAdapter extends FragmentPagerAdapter {



    private CharSequence [] tabTitles = {"Client ", "Service Provider"};
    public SignUpViewAdapter(@NonNull FragmentManager fm ) {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);


    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new UserSignUpFragment();
            case 1 :
                return new SPSignUpFragment();

            default: return null;
        }
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
