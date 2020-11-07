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
import com.lujaina.ldbeauty.Models.AppointmentModel;
import com.lujaina.ldbeauty.Models.CategoryModel;
import com.lujaina.ldbeauty.Models.ClientsAppointmentModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder>{
    private final Context mContext;
    private ArrayList<ClientsAppointmentModel> mAppointment;

    public CartAdapter(Context mContext) {
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
    public CartAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.liste_item_cart, parent, false);
        return new CartAdapter.MyViewHolder(listItemView);

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull CartAdapter.MyViewHolder holder, int position) {
        final ClientsAppointmentModel category = mAppointment.get(position);
        holder.salonName.setText(category.getSalonName());
        holder.time.setText(category.getAppointmentTime());
        holder.date.setText(category.getAppointmentDate());
        holder.serviceTitle.setText(category.getOfferServices());
        holder.price.setText(category.getPrice());



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
        public RelativeLayout viewForground, viewBackground;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            delete = itemView.findViewById(R.id.btn_delete);
            serviceTitle = itemView.findViewById(R.id.tv_title);
            price        = itemView.findViewById(R.id.tv_price);
            salonName    = itemView.findViewById(R.id.tv_salonName);
            specialist   = itemView.findViewById(R.id.tv_specialist);
            date         = itemView.findViewById(R.id.tv_date);
            time         = itemView.findViewById(R.id.tv_time);
            viewForground = itemView.findViewById(R.id.view_forground);
            viewBackground =itemView.findViewById(R.id.view_background);

        }
    }
}
