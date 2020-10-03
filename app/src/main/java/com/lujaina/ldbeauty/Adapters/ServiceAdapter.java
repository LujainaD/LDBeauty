package com.lujaina.ldbeauty.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.lujaina.ldbeauty.Models.ServiceModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.MyViewHolder>{
    private final Context mContext;
    private ArrayList<ServiceModel> mCategory;
    private ServiceAdapter.onClickListener mListener;
    public ServiceAdapter(Context mContext) {
        this.mContext = mContext;
        this.mCategory = new ArrayList<>();
    }

    public void update(ArrayList<ServiceModel> names) {
        mCategory = names;
        notifyDataSetChanged();

    }
    public void update(int position, ServiceModel ServiceModel) {
        mCategory.add(position, ServiceModel);
        notifyItemChanged(position);
    }
    public void removeItem(int position){
        mCategory.remove(position);
        notifyItemRemoved(position);

    }


    @NonNull
    @Override
    public ServiceAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_services, parent, false);
        return new ServiceAdapter.MyViewHolder(listItemView);

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ServiceAdapter.MyViewHolder holder, int position) {
        final ServiceModel category = mCategory.get(position);
        holder.service.setText(category.getServiceTitle());
        holder.price.setText(category.getServicePrice());
        holder.specialist.setText(category.getServiceSpecialist());
        Glide.with(mContext).load(category.getServiceURL()).centerCrop().
                into(holder.picture);
     /*   holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onClick(category);
                }

            }
        });*/

    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public void setonClickListener(View.OnClickListener listener){
        mListener = (onClickListener) listener;
    }

    public interface onClickListener {
        void onClick(ServiceModel category);
    }

/*interface serviceListener{
        public void handleDeleteItem(DocumentSnapshot snapshot);
}*/

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView service;
        TextView specialist;
        TextView price;
        ImageView picture;

        public RelativeLayout viewForground, viewBackground;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            viewForground = itemView.findViewById(R.id.view_forground);
            viewBackground =itemView.findViewById(R.id.view_background);
            specialist =itemView.findViewById(R.id.tv_name);
             price =   itemView.findViewById(R.id.tv_price);
            service = itemView.findViewById(R.id.tv_title);
            picture = itemView.findViewById(R.id.iv_service);
        }
    }
}