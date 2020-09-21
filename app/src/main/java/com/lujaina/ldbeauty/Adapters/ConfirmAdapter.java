package com.lujaina.ldbeauty.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfirmAdapter extends RecyclerView.Adapter<ConfirmAdapter.MyViewHolder>{
    private final Context mContext;
    private ArrayList<SPRegistrationModel> mNames;

    public ConfirmAdapter(Context mContext) {
        this.mContext = mContext;
        this.mNames = new ArrayList<>();
    }

    public void update(ArrayList<SPRegistrationModel> names) {
        mNames = names;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_salons_list_confi, parent, false);
    return new MyViewHolder(listItemView);

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final SPRegistrationModel names = mNames.get(position);

        Glide.with(mContext).load(names.getSalonImageURL()).into(holder.salonImg);
        holder.name.setText(names.getSalonName());
        holder.status.setImageDrawable(mContext.getResources().getDrawable(R.drawable.confirm));
        holder.salonDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onItemClick(names);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    public interface onItemClickListener {
        void onItemClick(SPRegistrationModel salonsDetails);
    }

    private onItemClickListener mListener;
    public void setupOnItemClickListener(onItemClickListener listener){
        mListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView salonImg;
        TextView name;
        TextView salonDetails;
        ImageView status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            salonImg = itemView.findViewById(R.id.civ_profile);
            name = itemView.findViewById(R.id.tv_salonName);
            salonDetails = itemView.findViewById(R.id.tv_details);
            status = itemView.findViewById(R.id.iv_status);
        }
    }


}
