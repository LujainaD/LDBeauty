package com.lujaina.ldbeauty.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;

public class SectionsAdapter extends RecyclerView.Adapter<SectionsAdapter.MyViewHolder> {


    private final Context context;
    private ArrayList<SPRegistrationModel> mNames;

    public SectionsAdapter(Context mContext) {
        mNames = new ArrayList<>();
        context = mContext;

    }

    public void update(int position, SPRegistrationModel names) {
        mNames.add(position, names);
        notifyItemChanged(position);
    }

    public void update(ArrayList<SPRegistrationModel> names) {
        mNames = names;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public SectionsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_salon_sections, parent, false);

        return new SectionsAdapter.MyViewHolder(listItemView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionsAdapter.MyViewHolder holder, final int position) {
        final SPRegistrationModel names = mNames.get(position);

        holder.title.setText(names.getSalonSection());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.afterAdapterItemClicked(position, names);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;


        public MyViewHolder(@NonNull View itemView, final SectionsAdapter.onItemClickListener listener) {
            super(itemView);

            title = itemView.findViewById(R.id.tv_section);


        }
    }

    private SectionsAdapter.onItemClickListener mListener;

    public interface onItemClickListener {
        /*void onItemClick( IntroduceSalonModel introduce);*/
        void afterAdapterItemClicked(int adapterPosition, SPRegistrationModel names);
    }

    public void setupOnItemClickListener(SectionsAdapter.onItemClickListener listener) {
        mListener = listener;
    }

}
