package com.lujaina.ldbeauty;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.lujaina.ldbeauty.SP.SPSignUpFragment;
import com.lujaina.ldbeauty.Client.ClientSignUpFragment;

import java.util.Objects;

public class SignUpViewAdapter extends FragmentPagerAdapter {



    private Context mContext ;
    private CharSequence [] tabTitles = {mContext.getString(R.string.client),mContext.getString(R.string.salon_owner)};


    //private CharSequence [] tabTitles = {"Client ", "Salon Owner"};
    public SignUpViewAdapter(@NonNull FragmentManager fm, Context mContext) {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.mContext=mContext;
    }



    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new ClientSignUpFragment();
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
