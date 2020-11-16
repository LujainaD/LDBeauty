package com.lujaina.ldbeauty.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.lujaina.ldbeauty.Models.AddInfoModel;
import com.lujaina.ldbeauty.Models.CommentModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.MyViewHolder>  {

    private final Context mContext;
    private ArrayList<CommentModel> commentArray;

    public RatingAdapter(Context context) {
        mContext = context;
        commentArray = new ArrayList();
    }

    public void update(int position, CommentModel commentModel) {
        commentArray.add(position, commentModel);
        notifyItemChanged(position);
    }

    public void update(ArrayList<CommentModel> commentModel) {
        commentArray = commentModel;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RatingAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_rating, parent, false);

        return new RatingAdapter.MyViewHolder(listItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        CommentModel commentModel = commentArray.get(position);
        holder.comment.setText(commentModel.getComment());
        holder.ratingBar.setRating(commentModel.getNumStars());
        holder.date.setText(commentModel.getCommentDate());
        holder.comment.resetState(commentModel.isShrink());

        holder.comment.setOnStateChangeListener(new ru.embersoft.expandabletextview.ExpandableTextView.OnStateChangeListener() {
            @Override
            public void onStateChange(boolean isShrink) {
                CommentModel item = commentArray.get(position);
                item.setShrink(isShrink);
                commentArray.set(position, item);

            }
        });
    }



    @Override
    public int getItemCount() {
        return commentArray.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        final ru.embersoft.expandabletextview.ExpandableTextView comment;

        TextView date ;
        RatingBar ratingBar;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            comment = itemView.findViewById(R.id.et_comment);
            date = itemView.findViewById(R.id.tv_date);
            ratingBar = itemView.findViewById(R.id.ratingBar);

        }
    }
}

