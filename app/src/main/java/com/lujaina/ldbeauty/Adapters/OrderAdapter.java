package com.lujaina.ldbeauty.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lujaina.ldbeauty.Models.ClientsAppointmentModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {
    private final Context mContext;
    private ArrayList<ClientsAppointmentModel> mAppointment;



    public OrderAdapter(Context mContext) {
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

    public void removeItem(int position) {
        mAppointment.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public OrderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_order, parent, false);
        return new OrderAdapter.MyViewHolder(listItemView);

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull final OrderAdapter.MyViewHolder holder, final int position) {
        final ClientsAppointmentModel clientAppointment = mAppointment.get(position);

        if (clientAppointment.getServiceType().equals("Service")) {

            holder.serviceTitle.setText(clientAppointment.getServiceTitle());
            holder.price.setText(clientAppointment.getPrice() + " R.O");
        } else {
            holder.serviceTitle.setText(clientAppointment.getOfferServices());
            holder.price.setText(clientAppointment.getPrice() + " R.O");
        }



    }

    @Override
    public int getItemCount() {
        return mAppointment.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView serviceTitle;
        TextView price;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceTitle = itemView.findViewById(R.id.tv_service);
            price = itemView.findViewById(R.id.tv_price);

        }
    }
}
