package com.lujaina.ldbeauty.SP;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lujaina.ldbeauty.Adapters.TimeAdapter;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.AppointmentModel;
import com.lujaina.ldbeauty.Models.OfferModel;
import com.lujaina.ldbeauty.Models.ServiceModel;
import com.lujaina.ldbeauty.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class AddServiceAppointmentFragment extends Fragment {
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    RecyclerView recyclerView;
    LinearLayoutManager lineralayoutManager;
    String timeNew;
    AppointmentModel appointmentModel;
    String sDay;
    String sMonth;
    String sYear;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    private MediatorInterface mMediatorInterface;
    private Context mContext;
    private ServiceModel mService;
    private TextView pickedDate;
    private TextView pickedTime;
    private Calendar calendar;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int hour;
    private int minutes;
    private ArrayList<AppointmentModel> timeList;
    private TimeAdapter mAdapter;

    public AddServiceAppointmentFragment() {
        // Required empty public constructor
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
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_add_appointment, container, false);
        TextView service = parentView.findViewById(R.id.tv_service);
        TextView specialist = parentView.findViewById(R.id.tv_specialist);
        TextView price = parentView.findViewById(R.id.tv_price);
        ImageButton ibCalendar = parentView.findViewById(R.id.ib_calender);
        ImageButton ibTimeButton = parentView.findViewById(R.id.ib_time);
        ImageButton ibBack = parentView.findViewById(R.id.ib_back);
        ImageButton ibLeft = parentView.findViewById(R.id.btn_left);
        ImageButton ibRight = parentView.findViewById(R.id.btn_right);
        recyclerView = parentView.findViewById(R.id.rv_time);
        Button btnAdd = parentView.findViewById(R.id.btn_addTime);

        pickedDate = parentView.findViewById(R.id.et_date);
        pickedTime = parentView.findViewById(R.id.et_time);


        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();

        getCurrentDate();
        getCurrentTime();
        showPreviousAppointments();


        timeList = new ArrayList<>();
        mAdapter = new TimeAdapter(mContext);
        recyclerView.setAdapter(mAdapter);
        setupRecyclerView(recyclerView);
        mAdapter.setonClickListener(new TimeAdapter.onClickListener() {
            @Override
            public void onClick(AppointmentModel category) {
                deleteTime(category);
            }
        });

        ibCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediatorInterface != null) {
                    mMediatorInterface.onBackPressed();
                }

            }
        });
        ibTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeDialog();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAnAppointment();
            }
        });


        if (mService != null) {
            service.setText(mService.getServiceTitle());
            specialist.setText(mService.getServiceSpecialist());
            price.setText(mService.getServicePrice());
        }
        ibLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lineralayoutManager.findFirstVisibleItemPosition() > 0) {
                    recyclerView.smoothScrollToPosition(lineralayoutManager.findFirstVisibleItemPosition() - 1);
                } else {
                    recyclerView.smoothScrollToPosition(0);
                }

            }
        });

        ibRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(lineralayoutManager.findLastVisibleItemPosition() + 1);
            }
        });

        return parentView;
    }

    private void deleteTime(AppointmentModel category) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference dbRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid()).child(Constants.Salon_Category)
                .child(mService.getIdCategory()).child(Constants.Salon_Service).child(mService.getServiceId()).child(Constants.Service_Appointment).child(category.getRecordId());
        dbRef.removeValue();
    }


    private void addAnAppointment() {
        appointmentModel = new AppointmentModel();
        appointmentModel.setAppointmentDate(pickedDate.getText().toString().trim());
        appointmentModel.setPickedTime(pickedTime.getText().toString().trim());
        appointmentModel.setCategoryId(mService.getIdCategory());
        appointmentModel.setOwnerId(mService.getOwnerId());
        appointmentModel.setServiceId(mService.getServiceId());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid()).child(Constants.Salon_Category)
                .child(mService.getIdCategory()).child(Constants.Salon_Service).child(mService.getServiceId()).child(Constants.Service_Appointment);
        String recordID = dbRef.push().getKey();
        appointmentModel.setRecordId(recordID);
        appointmentModel.setIsChosen("no");
        dbRef.child(Objects.requireNonNull(recordID)).setValue(appointmentModel);


    }

    public void setAddAppointmentFragment(ServiceModel service) {
        mService = service;
    }

    private void showDateDialog() {

        DatePickerDialog.OnDateSetListener selectListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                pickedDate.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
                sDay = String.valueOf(selectedDay);
                sMonth = String.valueOf(selectedMonth);
                sYear = String.valueOf(selectedYear);

                showPreviousAppointments();

            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, selectListener, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void getCurrentDate() {

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        String currentDate = sdf.format(calendar.getTime());
        pickedDate.setText(currentDate);
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

//		returnDate = Calendar.getInstance();
//		returnDate.set(returnYear, returnMonth, returnDay);
    }

    private void showTimeDialog() {

        TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectMinute) {
                String time;
                int h;
                String hour;
                String minute;
                //to convert hours from 24-hours to 12-hours system
                if (selectedHour >= 13) {
                    time = "PM";
                    h = selectedHour - 12;
                } else {
                    time = "AM";
                    h = selectedHour;
                }

                // add zero to select hour if it is equal or less than 9
                if (h <= 9) {
                    hour = "0" + h;
                } else {
                    hour = h + "";
                }

                // add zero to select minute if it is equal or less than 9
                if (selectMinute <= 9) {
                    minute = "0" + selectMinute;
                } else {
                    minute = selectMinute + "";
                }
                pickedTime.setText(hour + ":" + minute + " " + time);
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, R.style.MyTimePickerWidgetStyle, timeListener, hour, minutes, false);
        WindowManager.LayoutParams windowManager = new WindowManager.LayoutParams();
        windowManager.width = WindowManager.LayoutParams.WRAP_CONTENT;
        windowManager.height = WindowManager.LayoutParams.WRAP_CONTENT;
        timePickerDialog.getWindow().setAttributes(windowManager);
        timePickerDialog.show();
    }

    private void getCurrentTime() {
        SimpleDateFormat serverFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String timeNow = serverFormat.format(Calendar.getInstance().getTime());
        pickedTime.setText(timeNow);
    }

    private void showPreviousAppointments() {
        String datePicked = pickedDate.getText().toString().trim();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef;
        myRef = (DatabaseReference) database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid()).child(Constants.Salon_Category).child(mService.getIdCategory()).child(Constants.Salon_Service)
                .child(mService.getServiceId()).child(Constants.Service_Appointment);


        myRef.orderByChild("appointmentDate").equalTo(datePicked).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                timeList.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    AppointmentModel category = d.getValue(AppointmentModel.class);
                    timeList.add(category);

                }
                mAdapter.update(timeList);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

    }


    private void setupRecyclerView(RecyclerView recyclerView) {

        lineralayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(lineralayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

}