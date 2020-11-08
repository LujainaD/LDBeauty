package com.lujaina.ldbeauty.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.lujaina.ldbeauty.Models.ClientsAppointmentModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;

public class CartOffersAdapter extends RecyclerView.Adapter<CartOffersAdapter.MyViewHolder>{
    private final Context mContext;
    private ArrayList<ClientsAppointmentModel> mAppointment;
    private CartOffersAdapter.onDeleteListener mListener;

    public interface onDeleteListener {
        void onDelete( ClientsAppointmentModel model);
    }
    
    
    public CartOffersAdapter(Context mContext) {
        this.mContext = mContext;
        this.mAppointment = new ArrayList<>();
    }

    public void update(ArrayList<ClientsAppointmentModel> appointmentModels) {
        mAppointment = appointmentModels;
        notifyDataSetChanged();

    }
    public void update(int position, ClientsAppointmentModel appointmentModels) {
        mAppointment.add(position, appointmentModels);
        notifyItemChanged(position);
    }
    public void removeItem(int position){
        mAppointment.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public CartOffersAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.liste_item_cart, parent, false);
        return new CartOffersAdapter.MyViewHolder(listItemView);

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull final CartOffersAdapter.MyViewHolder holder, final int position) {
        final ClientsAppointmentModel clientAppointment = mAppointment.get(position);
        holder.salonName.setText(clientAppointment.getSalonName());
        holder.time.setText(clientAppointment.getAppointmentTime());
        holder.date.setText(clientAppointment.getAppointmentDate());
        holder.serviceTitle.setText(clientAppointment.getOfferServices());
        holder.price.setText(clientAppointment.getPrice() + " OMR");

        holder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.hiddenView.getVisibility() == View.VISIBLE){
                    TransitionManager.beginDelayedTransition(holder.cardView, new AutoTransition());
                    holder.hiddenView.setVisibility(View.GONE);
                    holder.arrow.setImageResource(R.drawable.ic_baseline_expand_more_24);

                }
                else {
                    TransitionManager.beginDelayedTransition(holder.cardView,
                            new AutoTransition());
                    holder.hiddenView.setVisibility(View.VISIBLE);
                    holder.arrow.setImageResource(R.drawable.ic_baseline_expand_less_24);
                }

            }
        });
        
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDelete(clientAppointment);
                
            }
        });


    }

    public void setonClickListener(CartOffersAdapter.onDeleteListener listener){
        mListener = listener;
    }
    @Override
    public int getItemCount() {
        return mAppointment.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        Button delete;
        TextView serviceTitle;
        TextView price;
        TextView salonName;
        TextView specialist;
        TextView date;
        TextView time;

        ImageButton arrow;
        LinearLayout hiddenView;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            delete = itemView.findViewById(R.id.btn_delete);
            serviceTitle = itemView.findViewById(R.id.tv_title);
            price        = itemView.findViewById(R.id.tv_price);
            salonName    = itemView.findViewById(R.id.tv_salonName);
            specialist   = itemView.findViewById(R.id.tv_specialist);
            date         = itemView.findViewById(R.id.tv_date);
            time         = itemView.findViewById(R.id.tv_time);

            cardView = itemView.findViewById(R.id.card);
            arrow = itemView.findViewById(R.id.arrow_button);
            hiddenView =itemView.findViewById(R.id.hidden_view);
        }
    }
}
