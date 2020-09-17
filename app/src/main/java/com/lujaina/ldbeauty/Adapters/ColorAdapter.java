package com.lujaina.ldbeauty.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.lujaina.ldbeauty.R;

public class ColorAdapter extends BaseAdapter {

private Context mContext;
int [] colors;
LayoutInflater inflater;

    public ColorAdapter(@NonNull Context context, int[] colors) {
         mContext = context;
         this.colors = colors;
         inflater = (LayoutInflater.from(context));

    }


    @Override
    public int getCount() {
        return colors.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
/*
          convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cutome_spinner_item,parent,false);
*/

        convertView = inflater.inflate(R.layout.cutome_spinner_item,null);
        Button color = convertView.findViewById(R.id.view_color);

        color.setBackgroundColor(colors[position]);
        return convertView;
    }
}



