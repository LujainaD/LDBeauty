package com.lujaina.ldbeauty.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder>{
    private final Context mContext;
    private ArrayList<SPRegistrationModel> mNames;

    public HomeAdapter(Context mContext) {
        this.mContext = mContext;
        this.mNames = new ArrayList<>();
    }

    public void update(ArrayList<SPRegistrationModel> names) {
        mNames = names;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public HomeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_salons, parent, false);
        return new HomeAdapter.MyViewHolder(listItemView);

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull HomeAdapter.MyViewHolder holder, int position) {
        final SPRegistrationModel names = mNames.get(position);
        Glide.with(mContext).load(names.getSalonImageURL()).into(holder.salonImg);
        holder.name.setText(names.getSalonName());
        holder.city.setText(names.getSalonCity());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onItemClick(names);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

//    public void setStatusListener(SalonConfirmDialogFragment.status status) {
//
//    }


    public interface onItemClickListener {
        void onItemClick(SPRegistrationModel salonsDetails);
    }

    private HomeAdapter.onItemClickListener mListener;
    public void setupOnItemClickListener(HomeAdapter.onItemClickListener listener){
        mListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView salonImg;
        TextView name;
        TextView city;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            salonImg = itemView.findViewById(R.id.civ_profile);
            name = itemView.findViewById(R.id.tv_salonName);
            city = itemView.findViewById(R.id.tv_city);
            cardView = itemView.findViewById(R.id.cv_salonNames);
        }
    }
}
