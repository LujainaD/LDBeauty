package com.lujaina.ldbeauty.Adapters;

import android.content.Context;
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

public class colorAdapter extends ArrayAdapter<ColorModel> {


    public colorAdapter(@NonNull Context context, ArrayList<ColorModel> color) {
        super(context, 0, color);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getDropDownView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_color_spinner, parent, false);
        }

        TextView color = convertView.findViewById(R.id.colorSpinner);
        ColorModel currentItem = getItem(position);

        if (currentItem != null) {
            color.setBackgroundColor(currentItem.getColor());
        }
        return convertView;

    }
}



