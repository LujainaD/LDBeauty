package com.lujaina.ldbeauty.SP;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SPProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SPProfileFragment extends Fragment {
    private Context mContext;
    private MediatorInterface mMediatorInterface;



    public SPProfileFragment() {
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
        View parentView = inflater.inflate(R.layout.fragment_s_p_profile, container, false);
        TextView salonPages = parentView.findViewById(R.id.tv_salonPages);


        salonPages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mMediatorInterface !=null){
                    mMediatorInterface.changeFragmentTo(new SalonProfileFragment(), SalonProfileFragment.class.getSimpleName());
                }

            }
        });

        return parentView;
    }
}