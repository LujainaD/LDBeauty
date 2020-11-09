package com.lujaina.ldbeauty.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.lujaina.ldbeauty.Models.AppointmentModel;
import com.lujaina.ldbeauty.R;
import com.lujaina.ldbeauty.User.OfferAppointmentFragment;

import java.util.ArrayList;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.MyViewHolder>{
    private final Context mContext;
    //private final Button btnConfirm;
    private ArrayList<AppointmentModel> mTime;
    private AppointmentAdapter.onTimePickedListener mListener;

	public interface onTimePickedListener {
		void onItemSelected(int position, int previousSelectedPosition, AppointmentModel model);
	}

    int previousSelectedItem = 0;

    public AppointmentAdapter(Context mContext, OfferAppointmentFragment offerAppointmentFragment, Button btnConfirm) {
        this.mContext = mContext;
        this.mTime = new ArrayList<>();
        mListener = (onTimePickedListener) offerAppointmentFragment;
        // this.btnConfirm = btnConfirm;
    }

    public void update(ArrayList<AppointmentModel> timeArray) {
        mTime = timeArray;
        notifyDataSetChanged();

    }
    public void update(int position, AppointmentModel appointmentModel) {
        mTime.add(position, appointmentModel);
        notifyItemChanged(position);
    }
    public void removeItem(int position){
        mTime.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public AppointmentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_time, parent, false);
        return new AppointmentAdapter.MyViewHolder(listItemView);

    }

    @SuppressLint({"UseCompatLoadingForDrawables", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull final AppointmentAdapter.MyViewHolder holder, final int position) {
        final AppointmentModel model = mTime.get(position);
        holder.time.setText(model.getPickedTime());
		holder.card.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
               // btnConfirm.setEnabled(true);
               // btnConfirm.getBackground().setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
                mListener.onItemSelected(position,previousSelectedItem, model);
			}
		});



		if(model.isSelected()){

			previousSelectedItem = position;
			holder.card.getCardBackgroundColor();
			ColorStateList.valueOf(Color.parseColor("#FFFFFF"));
			holder.card.setCardBackgroundColor(Color.parseColor("#DA6EA4"));
			holder.time.setTextColor(Color.parseColor("#FFFFFF"));
           // btnConfirm.setEnabled(true);
          //  btnConfirm.getBackground().setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent), PorterDuff.Mode.MULTIPLY);

		}else {
          //  btnConfirm.setEnabled(false);
            //btnConfirm.getBackground().setColorFilter(ContextCompat.getColor(mContext, R.color.lightGray), PorterDuff.Mode.MULTIPLY);
            holder.card.getCardBackgroundColor();
			ColorStateList.valueOf(Color.parseColor("#DA6EA4"));
			holder.card.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
			holder.time.setTextColor(Color.parseColor("#000000"));
        }
    }

    @Override
    public int getItemCount() {
        return mTime.size();
    }

    public void setonClickListener(AppointmentAdapter.onTimePickedListener listener){
        mListener = listener;
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
