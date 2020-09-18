package com.lujaina.ldbeauty.AppOwner;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.R;
import com.lujaina.ldbeauty.SP.SalonProfileFragment;


public class AppOwnerProfileFragment extends Fragment {
    private Context mContext;
    private MediatorInterface mMediatorInterface;




    public AppOwnerProfileFragment() {
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
        View parentView = inflater.inflate(R.layout.fragment_app_owner_profile, container, false);
        TextView confirmPage = parentView.findViewById(R.id.tv_confirm);


        confirmPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mMediatorInterface !=null){
                    mMediatorInterface.changeFragmentTo(new AoConfirmSalonsFragment(), AoConfirmSalonsFragment.class.getSimpleName());
                }

            }
        });


        return parentView;
    }
}