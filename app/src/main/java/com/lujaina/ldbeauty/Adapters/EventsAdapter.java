package com.lujaina.ldbeauty.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lujaina.ldbeauty.Client.OfferAppointmentFragment;
import com.lujaina.ldbeauty.Models.ClientsAppointmentModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.MyViewHolder> {
    private final Context mContext;
    private ArrayList<ClientsAppointmentModel> mTime;


    public EventsAdapter(Context mContext) {
        this.mContext = mContext;
        this.mTime = new ArrayList<>();
    }

    public void update(ArrayList<ClientsAppointmentModel> timeArray) {
        mTime = timeArray;
        notifyDataSetChanged();

    }

    public void update(int position, ClientsAppointmentModel ClientsAppointmentModel) {
        mTime.add(position, ClientsAppointmentModel);
        notifyItemChanged(position);
    }

    public void removeItem(int position) {
        mTime.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public EventsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_calendar, parent, false);
        return new EventsAdapter.MyViewHolder(listItemView);

    }

    @SuppressLint({"UseCompatLoadingForDrawables", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull final EventsAdapter.MyViewHolder holder, final int position) {
        final ClientsAppointmentModel model = mTime.get(position);
        holder.details.setText(model.getClientName()+ "  at: "+ model.getAppointmentTime());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(model);
            }
        });

        if(model.getAppointmentStatus().equals("Confirm")){
            holder.card.getCardBackgroundColor();
            ColorStateList.valueOf(Color.parseColor("#FFFFFF"));
            holder.card.setCardBackgroundColor(Color.parseColor("#00C853"));
            holder.details.setTextColor(Color.parseColor("#FFFFFF"));
            holder.img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.confirm));
        }else if(model.getAppointmentStatus().equals("Decline")){
            holder.card.getCardBackgroundColor();
            ColorStateList.valueOf(Color.parseColor("#FFFFFF"));
            holder.img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.cancel));
            holder.card.setCardBackgroundColor(Color.RED);
            holder.details.setTextColor(Color.parseColor("#FFFFFF"));
        }else{

            holder.card.getCardBackgroundColor();
            holder.card.setCardBackgroundColor(Color.parseColor("#E6E7E8"));
            holder.details.setTextColor(Color.parseColor("#000000"));
            holder.img.setVisibility(View.GONE);
        }
      
    }

    @Override
    public int getItemCount() {
        return mTime.size();
    }

    public interface onItemClickListener {
        void onItemClick(ClientsAppointmentModel appointmentDetails);

    }
    private EventsAdapter.onItemClickListener mListener;

    public void setupOnItemClickListener(EventsAdapter.onItemClickListener listener) {
        mListener = listener;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView details;
        CardView card;
        ImageView img;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            details = itemView.findViewById(R.id.tv_details);
            card = itemView.findViewById(R.id.card);
            img = itemView.findViewById(R.id.iv_status);
        }


    }
}