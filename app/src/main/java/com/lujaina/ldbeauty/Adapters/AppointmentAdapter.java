package com.lujaina.ldbeauty.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lujaina.ldbeauty.Models.AppointmentModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.MyViewHolder>{
    private final Context mContext;
    private ArrayList<AppointmentModel> mCategory;
    private AppointmentAdapter.onClickListener mListener;

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
    public void onBindViewHolder(@NonNull AppointmentAdapter.MyViewHolder holder, int position) {
        final AppointmentModel category = mCategory.get(position);
        holder.time.setText(category.getPickedTime());


    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    public void setonClickListener(AppointmentAdapter.onClickListener listener){
        mListener = listener;
    }

    public interface onClickListener {
        void onClick(AppointmentModel category);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView time;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.itv_time);

        }
    }
}
