package com.lujaina.ldbeauty.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lujaina.ldbeauty.Models.ServiceModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;

public class UserServiceAdapter extends RecyclerView.Adapter<UserServiceAdapter.MyViewHolder> {
    private final Context mContext;
    private ArrayList<ServiceModel> mCategory;
    private UserServiceAdapter.onClickListener mListener;

    public UserServiceAdapter(Context mContext) {
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

    public void removeItem(int position) {
        mCategory.remove(position);
        notifyItemRemoved(position);

    }


    @NonNull
    @Override
    public UserServiceAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_services, parent, false);
        return new UserServiceAdapter.MyViewHolder(listItemView);

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull UserServiceAdapter.MyViewHolder holder, int position) {
        final ServiceModel category = mCategory.get(position);
        holder.service.setText(category.getServiceTitle());
        holder.price.setText(category.getServicePrice());
        holder.specialist.setText(category.getServiceSpecialist());
        Glide.with(mContext).load(category.getServiceURL()).centerCrop().
                into(holder.picture);

    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public void setonClickListener(View.OnClickListener listener) {
        mListener = (UserServiceAdapter.onClickListener) listener;
    }

    public interface onClickListener {
        void onClick(ServiceModel category);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView service;
        TextView specialist;
        TextView price;
        ImageView picture;
        TextView book;

        public RelativeLayout viewForground, viewBackground;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            viewForground = itemView.findViewById(R.id.view_forground);
            viewBackground = itemView.findViewById(R.id.view_background);
            specialist = itemView.findViewById(R.id.tv_name);
            price = itemView.findViewById(R.id.tv_price);
            service = itemView.findViewById(R.id.tv_title);
            picture = itemView.findViewById(R.id.iv_service);
            book = itemView.findViewById(R.id.tv_book);
            book.setVisibility(View.VISIBLE);
        }
    }
}
