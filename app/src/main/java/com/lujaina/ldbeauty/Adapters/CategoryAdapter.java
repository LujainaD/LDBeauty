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
import com.lujaina.ldbeauty.Models.AddInfoModel;
import com.lujaina.ldbeauty.Models.CategoryModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder>{
    private final Context mContext;
    private ArrayList<CategoryModel> mCategory;

    public CategoryAdapter(Context mContext) {
        this.mContext = mContext;
        this.mCategory = new ArrayList<>();
    }

    public void update(ArrayList<CategoryModel> names) {
        mCategory = names;
        notifyDataSetChanged();

    }
    public void update(int position, CategoryModel categoryModel) {
        mCategory.add(position, categoryModel);
        notifyItemChanged(position);
    }
    public void removeItem(int position){
        mCategory.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public CategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_categories, parent, false);
        return new CategoryAdapter.MyViewHolder(listItemView);

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.MyViewHolder holder, int position) {
        final CategoryModel category = mCategory.get(position);
        holder.title.setText(category.getCategoryTitle());
        Glide.with(mContext).load(category.getCategoryURL()).into(holder.picture);
      /*  holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        Button title;
        ImageView picture;
        CardView card;
        public RelativeLayout viewForground, viewBackground;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            viewForground = itemView.findViewById(R.id.view_forground);
            viewBackground =itemView.findViewById(R.id.view_background);
            title = itemView.findViewById(R.id.btn_category);
            picture = itemView.findViewById(R.id.iv_category);
            card = itemView.findViewById(R.id.cv_category);
        }
    }
}