package com.lujaina.ldbeauty.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lujaina.ldbeauty.Models.GalleryModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder>{
private final Context mContext;
private ArrayList<GalleryModel> mGallery;
private onClickListener mListener;

    public GalleryAdapter(Context mContext) {
            this.mContext = mContext;
            this.mGallery = new ArrayList<>();
            }
    
    public void update(ArrayList<GalleryModel> names) {
            mGallery = names;
            notifyDataSetChanged();
    
            }
    public void update(int position, GalleryModel gallerymodel) {
            mGallery.add(position, gallerymodel);
            notifyItemChanged(position);
            }
    public void removeItem(int position){
            mGallery.remove(position);
            notifyItemRemoved(position);
            }
    
    @NonNull
    @Override
    public GalleryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_gallery, parent, false);
            return new GalleryAdapter.MyViewHolder(listItemView);
    
            }
    
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull GalleryAdapter.MyViewHolder holder, int position) {
    final GalleryModel category = mGallery.get(position);
            Glide.with(mContext).load(category.getPictureURL()).centerCrop().
            into(holder.picture);

            holder.fullScreen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null){
                        mListener.onClick(category);
                    }
                }
            });
    
            }
    
    @Override
    public int getItemCount() {
            return mGallery.size();
            }
    
    public void setonClickListener(onClickListener listener){
            mListener = listener;
            }
    
    public interface onClickListener {
        void onClick(GalleryModel category);
    }
    
    
    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView picture;
        ImageButton fullScreen;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            picture = itemView.findViewById(R.id.iv_gallery);
            fullScreen = itemView.findViewById(R.id.ib_fullscreen);
        }
    }
    }

