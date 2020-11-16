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

public class ClientAppointmentAdapter extends RecyclerView.Adapter<ClientAppointmentAdapter.MyViewHolder>{
    private final Context mContext;
    private ArrayList<ClientsAppointmentModel> clientsAppointment;

    public ClientAppointmentAdapter(Context mContext) {
        this.mContext = mContext;
        this.clientsAppointment = new ArrayList<>();
    }

    public void update(ArrayList<ClientsAppointmentModel> names) {
        clientsAppointment = names;
        notifyDataSetChanged();

    }
    public void update(int position, ClientsAppointmentModel clientsAppointmentModel) {
        clientsAppointment.add(position, clientsAppointmentModel);
        notifyItemChanged(position);
    }
    public void removeItem(int position){
        clientsAppointment.remove(position);
        notifyItemRemoved(position);
    }


    @NonNull
    @Override
    public ClientAppointmentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_appointment_sections, parent, false);
        return new ClientAppointmentAdapter.MyViewHolder(listItemView);

    }

    @Override
    public void onBindViewHolder(@NonNull ClientAppointmentAdapter.MyViewHolder holder, int position) {
        final ClientsAppointmentModel appointment = clientsAppointment.get(position);
        if (appointment.getServiceType().equals("Service")){
            holder.dateAndTime.setText(appointment.getAppointmentDate() + " , "+ appointment.getAppointmentTime());
            holder.specialist.setText(appointment.getSalonName() + " , " + appointment.getSpecialList()) ;
            holder.serviceTitle.setText(appointment.getServiceTitle() + " , " + appointment.getPrice()+" R.O");
            holder.status.setText(appointment.getAppointmentStatus());

        }else {
            holder.dateAndTime.setText(appointment.getAppointmentDate() + " , "+ appointment.getAppointmentTime());
            holder.tv_service.setText("Offer Title");
            holder.serviceTitle.setText(appointment.getOfferTitle());
            holder.tv_specialist.setText("Offer Services");
            holder.specialist.setText(appointment.getOfferServices() + " , " + appointment.getPrice()+" R.O") ;
            holder.status.setText(appointment.getAppointmentStatus());
        }
    }

    private int limit = 2;
    @Override
    public int getItemCount() {
        if(clientsAppointment.size() > limit){
            return limit;

        }else {
            return clientsAppointment.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_service ;
        TextView tv_specialist;

        TextView dateAndTime;
        TextView specialist;
        TextView serviceTitle;
        TextView status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_service = itemView.findViewById(R.id.service);
            tv_specialist = itemView.findViewById(R.id.specialist);

            dateAndTime = itemView.findViewById(R.id.tv_date);
            specialist = itemView.findViewById(R.id.tv_specialist);
            serviceTitle = itemView.findViewById(R.id.tv_service);
            status = itemView.findViewById(R.id.tv_status);

        }
    }
}
