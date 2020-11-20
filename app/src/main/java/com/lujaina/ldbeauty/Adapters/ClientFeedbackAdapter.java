package com.lujaina.ldbeauty.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lujaina.ldbeauty.Models.CommentModel;
import com.lujaina.ldbeauty.Models.ServiceModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;

public class ClientFeedbackAdapter extends RecyclerView.Adapter<ClientFeedbackAdapter.MyViewHolder> {
    private final Context mContext;
    private ArrayList<CommentModel> commentArray;
    private ClientFeedbackAdapter.onClickListener mListener;
    private onSwipeListener mswipeListener;


    public ClientFeedbackAdapter(Context mContext) {
        this.mContext = mContext;
        this.commentArray = new ArrayList<>();
    }

    public void update(ArrayList<CommentModel> commentModel) {
        commentArray = commentModel;
        notifyDataSetChanged();

    }

    public void update(int position, CommentModel commentModel) {
        commentArray.add(position, commentModel);
        notifyItemChanged(position);
    }

    public void removeItem(int position) {
        commentArray.remove(position);
        notifyItemRemoved(position);

    }


    @NonNull
    @Override
    public ClientFeedbackAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_client_feedback, parent, false);
        return new ClientFeedbackAdapter.MyViewHolder(listItemView);

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull final ClientFeedbackAdapter.MyViewHolder holder, final int position) {
        final CommentModel commentModel = commentArray.get(position);
        holder.comment.setText(commentModel.getComment());
        holder.ratingBar.setRating(commentModel.getNumStars());
        holder.date.setText(commentModel.getCommentDate());
        holder.comment.resetState(commentModel.isShrink());

       /* holder.viewForground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mswipeListener.onSwiped(holder,holder.viewForground.getLeft(),position,commentModel);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return commentArray.size();
    }

    public void setonClickListener(View.OnClickListener listener) {
        mListener = (ClientFeedbackAdapter.onClickListener) listener;
    }

    public interface onClickListener {
        void onClick(ServiceModel category);
    }

    public void setOnSwipeListener(onSwipeListener listener) {
        mswipeListener = listener;
    }

    public interface onSwipeListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction , int position, CommentModel commentModel);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final ru.embersoft.expandabletextview.ExpandableTextView comment;

        TextView date;
        RatingBar ratingBar;

        public RelativeLayout viewForground, viewBackground;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            viewForground = itemView.findViewById(R.id.view_forground);
            viewBackground = itemView.findViewById(R.id.view_background);
            comment = itemView.findViewById(R.id.et_comment);
            date = itemView.findViewById(R.id.tv_date);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }


}
