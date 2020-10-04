package com.lujaina.ldbeauty.SP;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomizeCalendar extends Fragment {
    private MediatorInterface mMediatorInterface;
    private Context mContext;
    ImageButton PreviouseButton,NextButton;
    ImageButton PreviouseDate,NextDate;

    TextView CurrentDate;
    GridView gridView;
    private static final int MAX_CALENDAR_Days = 42;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);
    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM",Locale.ENGLISH);
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy",Locale.ENGLISH);
    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    Context context;
    List<Date> dateList = new ArrayList<>();
    MyGridAdapter adapter;

    public CustomizeCalendar() {

    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof MediatorInterface) {
            mMediatorInterface = (MediatorInterface) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement MediatorInterface");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View parentView = inflater.inflate(R.layout.customize_calendar, container, false);
        TextView service = parentView.findViewById(R.id.tv_serviceTitle);
        TextView specialist = parentView.findViewById(R.id.tv_specialist);
        EditText time =parentView.findViewById(R.id.tv_time);
        EditText timeType =parentView.findViewById(R.id.tv_timeType);
        Button add = parentView.findViewById(R.id.btn_addTime);

        ImageButton back = parentView.findViewById(R.id.ib_back);
        PreviouseButton = parentView.findViewById(R.id.previousBtn);
        NextButton = parentView.findViewById(R.id.nextBtn);
        PreviouseDate = parentView.findViewById(R.id.btn_prevDate);
        NextDate = parentView.findViewById(R.id.btn_nextDate);
        CurrentDate = parentView.findViewById(R.id.current_Date);
        gridView = parentView.findViewById(R.id.gridview);

        SetupCalendar();
        PreviouseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH,-1);
                SetupCalendar();

            }
        });

        NextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH,1);
                SetupCalendar();
            }
        });


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final String date = dateFormat.format(dateList.get(position));

            }
            });
        return parentView;
    }

    private void SetupCalendar(){
        String StartDate = simpleDateFormat.format(calendar.getTime());
        CurrentDate.setText(StartDate);
        dateList.clear();
        Calendar monthCalendar = (Calendar)calendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH,1);
        int FirstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK)-1;
        monthCalendar.add(Calendar.DAY_OF_MONTH,-FirstDayOfMonth);



        while (dateList.size() < MAX_CALENDAR_Days){
            dateList.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH,1);

        }
       /* adapter = new MyGridAdapter(context,dateList,calendar);
        gridView.setAdapter(adapter);*/

    }


}
