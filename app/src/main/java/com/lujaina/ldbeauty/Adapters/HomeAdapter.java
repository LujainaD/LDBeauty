package com.lujaina.ldbeauty.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lujaina.ldbeauty.Models.CategoryModel;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder>{
    private final Context mContext;
    private ArrayList<SPRegistrationModel> mNames;
    private onClickListener mListener;

    public HomeAdapter(Context mContext) {
        this.mContext = mContext;
        this.mNames = new ArrayList<>();
    }

    public void update(int position, SPRegistrationModel names) {
        mNames.add(position,names);
        notifyItemChanged(position);
    }

    public void update(ArrayList<SPRegistrationModel> names) {
        mNames = names;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public HomeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_salons, parent, false);
        return new MyViewHolder(listItemView);

    }
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
                    Log.d("selectedSalon", "onItemClick-HomeAdapter : " + names.getUserId());
                    mListener.onClick(names);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }


    public void setonClickListener(onClickListener listener){
        mListener = listener;
    }

    public interface onClickListener {
        void onClick(SPRegistrationModel category);
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
