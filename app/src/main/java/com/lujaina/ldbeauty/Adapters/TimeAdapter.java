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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lujaina.ldbeauty.Models.AppointmentModel;
import com.lujaina.ldbeauty.Models.CategoryModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;

public class TimeAdapter extends RecyclerView.Adapter<com.lujaina.ldbeauty.Adapters.TimeAdapter.MyViewHolder>{
        private final Context mContext;
        private ArrayList<AppointmentModel> mCategory;
        private com.lujaina.ldbeauty.Adapters.TimeAdapter.onClickListener mListener;

        public TimeAdapter(Context mContext) {
            this.mContext = mContext;
            this.mCategory = new ArrayList<>();
        }

        public void update(ArrayList<AppointmentModel> names) {
            mCategory = names;
            notifyDataSetChanged();

        }
        public void update(int position, AppointmentModel appointmentModel) {
            mCategory.add(position, appointmentModel);
            notifyItemChanged(position);
        }
        public void removeItem(int position){
            mCategory.remove(position);
            notifyItemRemoved(position);
        }

        @NonNull
        @Override
        public com.lujaina.ldbeauty.Adapters.TimeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_time, parent, false);
            return new com.lujaina.ldbeauty.Adapters.TimeAdapter.MyViewHolder(listItemView);

        }

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onBindViewHolder(@NonNull com.lujaina.ldbeauty.Adapters.TimeAdapter.MyViewHolder holder, int position) {
            final AppointmentModel category = mCategory.get(position);
            holder.time.setText(category.getPickedTime());

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null){
                        mListener.onClick(category);
                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return mCategory.size();
        }

        public void setonClickListener(com.lujaina.ldbeauty.Adapters.TimeAdapter.onClickListener listener){
            mListener = listener;
        }

        public interface onClickListener {
            void onClick(AppointmentModel category);
        }


        public class MyViewHolder extends RecyclerView.ViewHolder{
            TextView time;
            ImageButton delete;
            public RelativeLayout viewForground, viewBackground;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                time = itemView.findViewById(R.id.itv_time);
                delete = itemView.findViewById(R.id.iv_delete);
            }
        }
    }