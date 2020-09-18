package com.lujaina.ldbeauty.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lujaina.ldbeauty.Models.ColorModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;

public class ColorAdapter extends ArrayAdapter<ColorModel> {

	public ColorAdapter(Context context, ArrayList<ColorModel> colorList) {
		super(context, 0, colorList);
	}

	@NonNull
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		return initView(position, convertView, parent);
	}
	@Override
	public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		return initView(position, convertView, parent);
	}
	private View initView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.cutome_spinner_item, parent, false
			);
		}
		TextView colorView = convertView.findViewById(R.id.btn_color_view);
		ColorModel colorValue = getItem(position);
		if (colorValue != null) {
			colorView.setText(colorValue.getColorName());
			colorView.setBackgroundColor(Color.parseColor(colorValue.getColorValue()));
		}
		return convertView;
	}
}
