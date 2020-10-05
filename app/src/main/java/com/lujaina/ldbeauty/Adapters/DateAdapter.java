package com.lujaina.ldbeauty.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lujaina.ldbeauty.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.MyViewHolder>{
    List<Date> dates ;
    Calendar currentDate;
    private int itemCount;
    public DateAdapter(Context mContext, int weeksInView) {
        this.itemCount = weeksInView*7;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_cell_layout, parent, false);
        return new DateAdapter.MyViewHolder(listItemView);    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       /* Date monthDate = dates.get(position);
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(monthDate);
        int dayNo = dateCalendar.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCalendar.get(Calendar.MONTH) + 1;
        int displayYear = dateCalendar.get(Calendar.YEAR);
        int currentYear = currentDate.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH) + 1;

        holder.date.setText(String.valueOf(dayNo));
*/
    }
    @Override
    public int getItemCount() {
       return itemCount;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView date ;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.calendar_day);

        }
    }
}
