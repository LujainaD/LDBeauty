package com.lujaina.ldbeauty.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.lujaina.ldbeauty.Models.AppointmentModel;
import com.lujaina.ldbeauty.Models.OfferModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.MyViewHolder>{
    private final Context mContext;
    private ArrayList<AppointmentModel> mCategory;
    private AppointmentAdapter.onClickListener mListener;

    int clickedposition = 0;

    public AppointmentAdapter(Context mContext) {
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
    public AppointmentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_time, parent, false);
        return new AppointmentAdapter.MyViewHolder(listItemView);

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull final AppointmentAdapter.MyViewHolder holder, final int position) {
        final AppointmentModel category = mCategory.get(position);
        holder.time.setText(category.getPickedTime());

        final boolean[] isClicked = {true};

        holder.card.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if(isClicked[0]){

                    holder.card.getCardBackgroundColor();
                    ColorStateList.valueOf(Color.parseColor("#FFFFFF"));
                    holder.card.setCardBackgroundColor(Color.parseColor("#DA6EA4"));
                    holder.time.setTextColor(Color.parseColor("#FFFFFF"));
/*
                    mCategory.add(category);
*/

                }else {
                    holder.card.getCardBackgroundColor();
                    ColorStateList.valueOf(Color.parseColor("#DA6EA4"));
                    holder.card.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                    holder.time.setTextColor(Color.parseColor("#000000"));

/*
                    mCategory.remove(category);
*/


                }


                isClicked[0] = !isClicked[0];

            }
        });

      /*  holder.card.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                category.setSelected(!category.isSelected());
                if(category.isSelected()){
                    mCategory.add(category);
                }else {
                    mCategory.remove(category);
                }

            }
        });*/



    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

   /* public ArrayList<AppointmentModel> getAppointmentArrayList() {
        return mCategory;
    }*/

    public void setonClickListener(AppointmentAdapter.onClickListener listener){
        mListener = listener;
    }

    public interface onClickListener {
        void onClick(AppointmentModel category);
        void onDelete(AppointmentModel category);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView time;
        CardView card;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.itv_time);
            card = itemView.findViewById(R.id.card);


        }


    }
}
