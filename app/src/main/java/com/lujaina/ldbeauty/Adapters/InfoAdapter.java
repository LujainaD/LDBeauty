package com.lujaina.ldbeauty.Adapters;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.AddInfoModel;
import com.lujaina.ldbeauty.Models.CategoryModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;


public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.MyViewHolder> {

	public Context mContext;
	//public MediatorInterface mMidiatorCallback;
	private ArrayList<AddInfoModel> mUpdate;
	private onClickListener mListener;

	public InfoAdapter(Context mContext) {
		mUpdate = new ArrayList<>();
		this.mContext = mContext;
	//	mMidiatorCallback = (MediatorInterface) mContext;
	}

	public void update(int position, AddInfoModel aboutSalon) {
		mUpdate.add(position, aboutSalon);
		notifyItemChanged(position);
	}

	public void update(ArrayList<AddInfoModel> aboutSalon) {
		mUpdate = aboutSalon;
		notifyDataSetChanged();
	}

	public void removeItem(int position){
		mUpdate.remove(position);
		notifyItemRemoved(position);
	}

	@NonNull
	@Override
	public InfoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_add, parent, false);

		return new InfoAdapter.MyViewHolder(listItemView);
	}

	@SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
	@Override
	public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
		final AddInfoModel aboutSalon = mUpdate.get(position);
		holder.tvTitle.setText(aboutSalon.getTitle());
		holder.tvBody.setText(aboutSalon.getBody());


		holder.tvBody.setOnStateChangeListener(new ru.embersoft.expandabletextview.ExpandableTextView.OnStateChangeListener() {
			@Override
			public void onStateChange(boolean isShrink) {
				AddInfoModel item = mUpdate.get(position);
				item.setShrink(isShrink);
				mUpdate.set(position, item);

			}
		});
		holder.tvBody.setText(aboutSalon.getBody());
		holder.tvBody.resetState(aboutSalon.isShrink());

		holder.cvAboutSalon.setCardBackgroundColor(Color.parseColor(aboutSalon.getBackgroundColor()));

		holder.tvTitle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mListener != null){
					mListener.onClick(aboutSalon);
				}

			}
		});
	}

	@Override
	public int getItemCount() {
		return mUpdate.size();
	}

	public void setonClickListener(InfoAdapter.onClickListener listener){
		mListener = listener;
	}

	public interface onClickListener {
		void onClick(AddInfoModel info);
	}

	public class MyViewHolder extends RecyclerView.ViewHolder {

		final ru.embersoft.expandabletextview.ExpandableTextView tvBody;
		TextView tvTitle;
		CardView cvAboutSalon;
        public RelativeLayout viewForground, viewBackground;

		public MyViewHolder(@NonNull View itemView) {
			super(itemView);
			viewForground = itemView.findViewById(R.id.view_forground);
			viewBackground =itemView.findViewById(R.id.view_background);
			cvAboutSalon = itemView.findViewById(R.id.cv_info);
			tvTitle = itemView.findViewById(R.id.title_tv);
			tvBody = itemView.findViewById(R.id.body_tv);

		}
	}
}
