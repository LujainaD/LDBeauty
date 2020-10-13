package com.lujaina.ldbeauty.Adapters;

import android.animation.ObjectAnimator;
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
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lujaina.ldbeauty.Dialogs.AddInfoDialogFragment;
import com.lujaina.ldbeauty.Dialogs.SalonConfirmDialogFragment;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.AddInfoModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;

import at.blogc.android.views.ExpandableTextView;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.MyViewHolder> {

	public Context mContext;
	public MediatorInterface mMidiatorCallback;
	private ArrayList<AddInfoModel> mUpdate;
/*
	private infoListener mListener;
*/
/*
	private AddInfoDialogFragment.color mColor;
*/

	public InfoAdapter(Context mContext) {
		mUpdate = new ArrayList<>();
		this.mContext = mContext;
		mMidiatorCallback = (MediatorInterface) mContext;
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
	public void restoreItem(AddInfoModel item ,int position) {
		mUpdate.add(position,item);
		notifyItemInserted(position);

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
		holder.expandableTextView.setText(aboutSalon.getBody());
		holder.expandableTextView.setAnimationDuration(750L);
		holder.expandableTextView.setInterpolator(new OvershootInterpolator());
		holder.expandableTextView.setExpandInterpolator(new OvershootInterpolator());
		holder.expandableTextView.setCollapseInterpolator(new OvershootInterpolator());

// toggle the ExpandableTextView
		holder.tvExpandable.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				holder.tvExpandable.setText(holder.expandableTextView.isExpanded() ? R.string.showmore : R.string.showless);

				holder.expandableTextView.toggle();
			}
		});

// but, you can also do the checks yourself
		holder.tvExpandable.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(final View v)
			{
				if (holder.expandableTextView.isExpanded())
				{
					holder.expandableTextView.collapse();
					holder.tvExpandable.setText(R.string.showmore);

				}
				else
				{
					holder.expandableTextView.expand();
					holder.tvExpandable.setText(R.string.showless);

				}


			}
		});

// listen for expand / collapse events
		/*holder.expandableTextView.setOnExpandListener(new ExpandableTextView.OnExpandListener()
		{
			@Override
			public void onExpand(final ExpandableTextView view)
			{
				Log.d(TAG, "ExpandableTextView expanded");
			}

			@Override
			public void onCollapse(final ExpandableTextView view)
			{
				Log.d(TAG, "ExpandableTextView collapsed");
			}
		});*/











	/*	if (aboutSalon.getBody().length() <= 100 ) {
			holder.tvBody.setText(aboutSalon.getBody());
			holder.tvExpandable.setVisibility(View.GONE);
		}else {
			holder.tvBody.setText(aboutSalon.getBody().substring(0, 100) + " . . . ");

		}*/
		holder.cvAboutSalon.setCardBackgroundColor(Color.parseColor(aboutSalon.getBackgroundColor()));





		/*holder.cvAboutSalon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (mListener != null) {
					mListener.deleteInfo(position);
				}
			}
		});
*/
		final boolean[] isCollapse = {true};

		/*holder.tvExpandable.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("SetTextI18n")
			@Override
			public void onClick(View view) {

				if (isCollapse[0]) {

					if (aboutSalon.getBody().length() <= 100 ) {
						holder.tvBody.setText(aboutSalon.getBody());
						holder.tvExpandable.setVisibility(View.GONE);
					} else {
						holder.tvBody.setText(aboutSalon.getBody());
						holder.tvExpandable.setText("Show less");
					}


				} else {

					if (aboutSalon.getBody().length() <= 100 ) {
						holder.tvBody.setText(aboutSalon.getBody());
						holder.tvExpandable.setVisibility(View.GONE);
					} else {
						holder.tvBody.setText(aboutSalon.getBody().substring(0, 100) + " . . . ");
						holder.tvExpandable.setText("Show more");
					}


				}
				isCollapse[0] = !isCollapse[0];


			}
		});*/
	}

	@Override
	public int getItemCount() {
		return mUpdate.size();
	}

	/*public void setRemoveListener(infoListener listener) {

		mListener = listener;
	}



	public interface infoListener {

		void deleteInfo(int position);
	}
*/
	public class MyViewHolder extends RecyclerView.ViewHolder {

/*
		final TextView tvBody;
*/
		final TextView tvExpandable;
		TextView tvTitle;
		CardView cvAboutSalon;
		ExpandableTextView expandableTextView;
        public RelativeLayout viewForground, viewBackground;
		public MyViewHolder(@NonNull View itemView) {
			super(itemView);
			viewForground = itemView.findViewById(R.id.view_forground);
			viewBackground =itemView.findViewById(R.id.view_background);
			cvAboutSalon = itemView.findViewById(R.id.cv_info);
			tvTitle = itemView.findViewById(R.id.title_tv);
/*
			tvBody = itemView.findViewById(R.id.body_tv);
*/
			tvExpandable = itemView.findViewById(R.id.tv_expandable);
			expandableTextView = itemView.findViewById(R.id.body_tv);

		}


	}

}
