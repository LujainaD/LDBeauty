package com.lujaina.ldbeauty.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.MyViewHolder>{
        private final Context mContext;
        private ArrayList<AppointmentModel> mAppointment;
        private onClickListener mListener;

        public TimeAdapter(Context mContext) {
            this.mContext = mContext;
            this.mAppointment = new ArrayList<>();
        }

        public void update(ArrayList<AppointmentModel> names) {
            mAppointment = names;
            notifyDataSetChanged();

        }
        public void update(int position, AppointmentModel appointmentModel) {
            mAppointment.add(position, appointmentModel);
            notifyItemChanged(position);
        }
        public void removeItem(int position){
            mAppointment.remove(position);
            notifyItemRemoved(position);
        }

        @NonNull
        @Override
        public TimeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_time, parent, false);
            return new TimeAdapter.MyViewHolder(listItemView);

        }

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onBindViewHolder(@NonNull TimeAdapter.MyViewHolder holder, int position) {
            final AppointmentModel appointment = mAppointment.get(position);
            holder.time.setText(appointment.getPickedTime());

            if(appointment.getIsChosen().equals("yes")){
                holder.delete.setVisibility(View.GONE);
                holder.card.getBackground().setTint(Color.parseColor("#E6E7E8"));

            }else {

                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mListener != null){
                            mListener.onClick(appointment);
                        }

                    }
                });
            }


        }

        @Override
        public int getItemCount() {
            return mAppointment.size();
        }

        public void setonClickListener(onClickListener listener){
            mListener = listener;
        }

        public interface onClickListener {
            void onClick(AppointmentModel appointment);
        }


        public class MyViewHolder extends RecyclerView.ViewHolder{
            TextView time;
            ImageButton delete;
            CardView card;
            public RelativeLayout viewForground, viewBackground;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                time = itemView.findViewById(R.id.itv_time);
                delete = itemView.findViewById(R.id.iv_delete);
            }
        }
    }