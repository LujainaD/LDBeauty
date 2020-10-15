package com.lujaina.ldbeauty.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lujaina.ldbeauty.Models.OfferModel;
import com.lujaina.ldbeauty.Models.ServiceModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.MyViewHolder> {
    private final Context mContext;
    private ArrayList<OfferModel> mOffer;
    private OfferAdapter.onClickListener mListener;

    public OfferAdapter(Context mContext) {
        this.mContext = mContext;
        this.mOffer = new ArrayList<>();
    }

    public void update(ArrayList<OfferModel> names) {
        mOffer = names;
        notifyDataSetChanged();

    }

    public void update(int position, OfferModel offerModel) {
        mOffer.add(position, offerModel);
        notifyItemChanged(position);
    }

    public void removeItem(int position) {
        mOffer.remove(position);
        notifyItemRemoved(position);

    }


    @NonNull
    @Override
    public OfferAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_offers, parent, false);
        return new OfferAdapter.MyViewHolder(listItemView);

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull OfferAdapter.MyViewHolder holder, int position) {
        final OfferModel offerModel = mOffer.get(position);
        holder.offerTitle.setText(offerModel.getTitle());
        holder.offerService.setText(offerModel.getServices());
        holder.prePrice.setText(offerModel.getPreviousPrice());
        holder.curPrice.setText(offerModel.getCurrentPrice());

    }

    @Override
    public int getItemCount() {
        return mOffer.size();
    }

    public void setonClickListener(View.OnClickListener listener) {
        mListener = (OfferAdapter.onClickListener) listener;
    }

    public interface onClickListener {
        void onClick(ServiceModel category);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView offerTitle;
        TextView offerService;
        TextView prePrice;
        TextView curPrice;
        public RelativeLayout viewForground, viewBackground;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            viewForground = itemView.findViewById(R.id.view_forground);
            viewBackground = itemView.findViewById(R.id.view_background);
            offerTitle = itemView.findViewById(R.id.tv_title);
            offerService = itemView.findViewById(R.id.tv_name);
            prePrice = itemView.findViewById(R.id.tv_price);
            curPrice = itemView.findViewById(R.id.tv_current);
            prePrice.setPaintFlags(prePrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }
}