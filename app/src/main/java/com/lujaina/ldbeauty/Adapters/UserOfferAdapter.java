package com.lujaina.ldbeauty.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lujaina.ldbeauty.Models.OfferModel;
import com.lujaina.ldbeauty.Models.ServiceModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;

public class UserOfferAdapter extends RecyclerView.Adapter<UserOfferAdapter.MyViewHolder> {
    private final Context mContext;
    private ArrayList<OfferModel> mOffer;
    private UserOfferAdapter.onClickListener mListener;

    public UserOfferAdapter(Context mContext) {
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
    public UserOfferAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_offers, parent, false);
        return new UserOfferAdapter.MyViewHolder(listItemView);

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull UserOfferAdapter.MyViewHolder holder, int position) {
        final OfferModel offerModel = mOffer.get(position);
        holder.offerTitle.setText(offerModel.getTitle());
        holder.offerService.setText(offerModel.getServices());
        holder.prePrice.setText(offerModel.getPreviousPrice());
        holder.curPrice.setText(offerModel.getCurrentPrice());
        holder.book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onClick(offerModel);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mOffer.size();
    }

    public void setonClickListener(onClickListener listener) {
        mListener = listener;
    }

    public interface onClickListener {
        void onClick(OfferModel offerModel);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView offerTitle;
        TextView offerService;
        TextView prePrice;
        TextView curPrice;
        TextView book;

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
            book = itemView.findViewById(R.id.tv_book);
            book.setVisibility(View.VISIBLE);
        }
    }
}
