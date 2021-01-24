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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.lujaina.ldbeauty.Models.ClientsAppointmentModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;

public class CartServicesAdapter extends RecyclerView.Adapter<CartServicesAdapter.MyViewHolder>{
    private final Context mContext;
    private ArrayList<ClientsAppointmentModel> mAppointment;
    private CartServicesAdapter.onDeleteListener mListener;

    public interface onDeleteListener {
        void onDelete( ClientsAppointmentModel model);
    }


    public CartServicesAdapter(Context mContext) {
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
    public CartServicesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_cart, parent, false);
        return new CartServicesAdapter.MyViewHolder(listItemView);

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull final CartServicesAdapter.MyViewHolder holder, final int position) {
        final ClientsAppointmentModel clientAppointment = mAppointment.get(position);

        if (clientAppointment.getServiceType().equals("Service")){
            holder.salonName.setText(clientAppointment.getSalonName());
            holder.time.setText(clientAppointment.getAppointmentTime());
            holder.date.setText(clientAppointment.getAppointmentDate());
            holder.serviceTitle.setText(clientAppointment.getServiceTitle());
            holder.specialist.setText(clientAppointment.getSpecialList());
            holder.price.setText(clientAppointment.getPrice() + " R.O");
            holder.offerImg.setVisibility(View.GONE);
        }else {
            holder.salonName.setText(clientAppointment.getSalonName());
            holder.time.setText(clientAppointment.getAppointmentTime());
            holder.date.setText(clientAppointment.getAppointmentDate());
            holder.serviceTitle.setText(clientAppointment.getOfferServices());
            holder.specialist.setVisibility(View.GONE);
            holder.iconSpecialist.setVisibility(View.GONE);
            holder.offerImg.setVisibility(View.VISIBLE);
            holder.price.setText(clientAppointment.getPrice() + " R.O");
        }


        holder.soldStatus.setText(clientAppointment.getSoldStatus());

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDelete(clientAppointment);

            }
        });


    }

    public void setonClickListener(CartServicesAdapter.onDeleteListener listener){
        mListener = listener;
    }
    @Override
    public int getItemCount() {
        return mAppointment.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageButton delete;
        TextView serviceTitle;
        TextView price;
        TextView salonName;
        TextView specialist;
        TextView date;
        TextView time;
        TextView soldStatus;

        CardView cardView;
        ImageView offerImg;
        ImageView iconSpecialist;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            delete       = itemView.findViewById(R.id.btn_delete);
            serviceTitle = itemView.findViewById(R.id.tv_title);
            price        = itemView.findViewById(R.id.tv_price);
            salonName    = itemView.findViewById(R.id.tv_salonName);
            specialist   = itemView.findViewById(R.id.tv_specialist);
            date         = itemView.findViewById(R.id.tv_date);
            time         = itemView.findViewById(R.id.tv_time);
            offerImg     = itemView.findViewById(R.id.iv_offer);
            iconSpecialist     = itemView.findViewById(R.id.imageView22);
            soldStatus = itemView.findViewById(R.id.soldStatus);
            cardView = itemView.findViewById(R.id.card);

        }
    }
}

