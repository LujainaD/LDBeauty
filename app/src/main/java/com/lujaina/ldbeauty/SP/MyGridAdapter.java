package com.lujaina.ldbeauty.SP;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lujaina.ldbeauty.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyGridAdapter extends ArrayAdapter {
    List<Date> dates ;
    Calendar currentDate;
    LayoutInflater inflater;

    public MyGridAdapter(Context context, List<Date> dates, Calendar currentDate) {
        super(context, R.layout.single_cell_layout);
        this.dates = dates;
        this.currentDate = currentDate;
        inflater = LayoutInflater.from(context);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Date monthDate = dates.get(position);
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(monthDate);
        int dayNo = dateCalendar.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCalendar.get(Calendar.MONTH)+1;
        int displayYear = dateCalendar.get(Calendar.YEAR);
        int currentYear = currentDate.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH)+1;
        View view = convertView;
        if(view == null){
            view = inflater.inflate(R.layout.single_cell_layout,parent,false);
        }
        if (displayMonth == currentMonth && displayYear==currentYear){
            view.setBackgroundColor(getContext().getResources().getColor(R.color.pink_text));

        }
        else {
            view.setBackgroundColor(Color.parseColor("#cccccc"));
        }
        TextView cellNumber = view.findViewById(R.id.calendar_day);
        cellNumber.setText(String.valueOf(dayNo));
        return view;
    }


    @Override
    public int getCount() {
        return dates.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }

    @Override
    public int getPosition(@Nullable Object item) {
        return dates.indexOf(item);
    }



}
