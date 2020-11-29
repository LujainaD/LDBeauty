package com.lujaina.ldbeauty.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lujaina.ldbeauty.Models.ClientsAppointmentModel;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SalonAppointmentAdapter extends RecyclerView.Adapter<SalonAppointmentAdapter.MyViewHolder> {
    private final Context mContext;
    private ArrayList<ClientsAppointmentModel> mNames;

    public SalonAppointmentAdapter(Context mContext) {
        this.mContext = mContext;
        this.mNames = new ArrayList<>();
    }

    public void update(ArrayList<ClientsAppointmentModel> names) {
        mNames = names;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public SalonAppointmentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_salon_appointment, parent, false);
        return new SalonAppointmentAdapter.MyViewHolder(listItemView);

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull final SalonAppointmentAdapter.MyViewHolder holder, int position) {
        final ClientsAppointmentModel names = mNames.get(position);

        if(names.getServiceType().equals("Service")){
            holder.salonName.setText(names.getSpecialList());

        }else {
            holder.salonName.setText(names.getOfferTitle());
        }

        if(names.getAppointmentStatus().equals("Confirm")){
            holder.decline.setVisibility(View.GONE);
            holder.confirm.setVisibility(View.GONE);
            holder.status.setVisibility(View.VISIBLE);
            holder.status.setImageDrawable(mContext.getResources().getDrawable(R.drawable.confirm));

        }else if (names.getAppointmentStatus().equals("Decline")){
            holder.decline.setVisibility(View.GONE);
            holder.confirm.setVisibility(View.GONE);
            holder.status.setVisibility(View.VISIBLE);
            holder.status.setImageDrawable(mContext.getResources().getDrawable(R.drawable.cancel));
        }


        holder.appointmentDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(names);
                }
            }
        });

        holder.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onConfirm(names);

                }
            }
        });

        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onDecline(names);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    public interface onItemClickListener {
        void onItemClick(ClientsAppointmentModel salonsDetails);
        void onConfirm(ClientsAppointmentModel confirm);
        void onDecline(ClientsAppointmentModel decline);
    }

    private SalonAppointmentAdapter.onItemClickListener mListener;

    public void setupOnItemClickListener(SalonAppointmentAdapter.onItemClickListener listener) {
        mListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView salonName;
        TextView appointmentDetails;
        Button confirm;
        Button decline;
        ImageView status;
        public RelativeLayout viewForground, viewBackground;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            viewForground = itemView.findViewById(R.id.view_forground);
            viewBackground =itemView.findViewById(R.id.view_background);
            salonName= itemView.findViewById(R.id.ttv_specialist);
            appointmentDetails = itemView.findViewById(R.id.tv_details);
            confirm = itemView.findViewById(R.id.btn_confirm);
            decline = itemView.findViewById(R.id.btn_cancel);
            status = itemView.findViewById(R.id.iv_status);

        }
    }
}
