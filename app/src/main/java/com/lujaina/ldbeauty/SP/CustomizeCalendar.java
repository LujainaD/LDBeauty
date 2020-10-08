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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lujaina.ldbeauty.Adapters.CategoryAdapter;
import com.lujaina.ldbeauty.Adapters.DateAdapter;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.CategoryModel;
import com.lujaina.ldbeauty.Models.ServiceModel;
import com.lujaina.ldbeauty.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomizeCalendar extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    private MediatorInterface mMediatorInterface;
    private Context mContext;
    ImageButton PreviouseButton,NextButton;
    ImageButton PreviouseDate,NextDate;
    private DateAdapter dateAdapter;
    private int weeksInView = 1; //will need to make this update dynamicallly based on when a user toggles the view
    private ArrayList<Calendar> calendarArrayList = new ArrayList<>();
    TextView CurrentDate;

    private static final int MAX_CALENDAR_Days = 42;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);
    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM",Locale.ENGLISH);
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy",Locale.ENGLISH);
    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    Context context;
    List<Date> dateList = new ArrayList<>();
    private ServiceModel mService;

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
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid()).child(Constants.Salon_Category)
                .child(mService.getIdCategory()).child(Constants.Salon_Service).child(mService.getServiceId());
        final TextView service = parentView.findViewById(R.id.tv_serviceTitle);
        final TextView specialist = parentView.findViewById(R.id.tv_specialist);
        final TextView price = parentView.findViewById(R.id.tv_price);
        EditText time =parentView.findViewById(R.id.tv_time);
        EditText timeType =parentView.findViewById(R.id.tv_timeType);
        Button add = parentView.findViewById(R.id.btn_addTime);
        ImageButton back = parentView.findViewById(R.id.ib_back);
        PreviouseButton = parentView.findViewById(R.id.previousBtn);
        NextButton = parentView.findViewById(R.id.nextBtn);
        PreviouseDate = parentView.findViewById(R.id.btn_prevDate);
        NextDate = parentView.findViewById(R.id.btn_nextDate);
        CurrentDate = parentView.findViewById(R.id.current_Date);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediatorInterface != null){
                    mMediatorInterface.onBackPressed();
                }
            }
        });

        RecyclerView rvDate = parentView.findViewById(R.id.rv_day);
        dateAdapter = new DateAdapter(mContext,weeksInView);
        rvDate.setAdapter(dateAdapter);
        setupRecyclerView(rvDate);



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

        if(mService != null ){
            service.setText(mService.getServiceTitle());
            specialist.setText(mService.getServicePrice());
            price.setText(mService.getServicePrice());
        }

        return parentView;
    }

    private void setupRecyclerView(RecyclerView rvDate) {
        GridLayoutManager gridLayout= new GridLayoutManager(mContext,7);
        rvDate.setLayoutManager(gridLayout);
        rvDate.setItemAnimator(new DefaultItemAnimator());

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

    }

    public void setAppointment(ServiceModel service) {
        mService = service;
    }
}
