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
import com.lujaina.ldbeauty.Models.CategoryModel;
import com.lujaina.ldbeauty.Models.TestInsModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.MyViewHolder>{
    private final Context mContext;
    private ArrayList<TestInsModel> modelArrayList;
    private TestAdapter.onClickListener mListener;

    public TestAdapter(Context mContext, ArrayList<TestInsModel> modelArrayList) {
        this.mContext = mContext;
        this.modelArrayList = modelArrayList;
    }

    public void update(ArrayList<TestInsModel> testInsModels) {
        modelArrayList = testInsModels;
        notifyDataSetChanged();

    }
    public void update(int position, TestInsModel testInsModels) {
        modelArrayList.add(position, testInsModels);
        notifyItemChanged(position);
    }
    public void removeItem(int position){
        modelArrayList.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public TestAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_inst, parent, false);
        return new TestAdapter.MyViewHolder(listItemView);

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull TestAdapter.MyViewHolder holder, int position) {
        final TestInsModel model = modelArrayList.get(position);
        holder.text.setText(model.getText());
        holder.picture.setImageResource(model.getImg());


    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    public void setonClickListener(TestAdapter.onClickListener listener){
        mListener = listener;
    }

    public interface onClickListener {
        void onClick(CategoryModel category);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView text;
        ImageView picture;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
            picture = itemView.findViewById(R.id.img);
        }
    }
}