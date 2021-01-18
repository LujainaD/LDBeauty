package com.lujaina.ldbeauty.SP;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import static android.view.View.VISIBLE;


public class AddOffersAppointmentFragment extends Fragment {
    public static final String DATE_FORMAT = "d/M/yyyy";
    RecyclerView recyclerView;
    GridLayoutManager lineralayoutManager;
    String timeNew;
    AppointmentModel appointmentModel;
    String sDay;
    String sMonth;
    String sYear;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    private Context mContext;
   // private ServiceModel mService;
    private TextView pickedDate;
    //  private TextView pickedTime;
    TextView startTime;
    TextView endTime ;
    private Calendar calendar;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int hour;
    private int minutes;
    private ArrayList<AppointmentModel> timeList;
    private TimeAdapter mAdapter;
    TextView empty;
    Button btnAdd;
    private OfferModel mOffer;
    private String salonName;
    EditText duration;
    NavController navController;

    public AddOffersAppointmentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
       /* if (context instanceof MediatorInterface) {
            mMediatorInterface = (MediatorInterface) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement MediatorInterface");
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_add_offers_appointment, container, false);
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        mOffer = (OfferModel) getArguments().getSerializable("OfferModel");

        empty = parentView.findViewById(R.id.tv_empty);

        TextView offerTitle 	= parentView.findViewById(R.id.tv_service);
        TextView services = parentView.findViewById(R.id.tv_specialist);
        TextView curPrice 		= parentView.findViewById(R.id.tv_price);
        ImageButton ibCalendar = parentView.findViewById(R.id.ib_calender);
        // ImageButton ibTimeButton = parentView.findViewById(R.id.ib_time);
        ImageButton ibTimeStart = parentView.findViewById(R.id.ib_srartTime);
        ImageButton ibTimeEnd = parentView.findViewById(R.id.ib_endTime);

        ImageButton ibBack = parentView.findViewById(R.id.ib_back);
        ImageButton ibLeft = parentView.findViewById(R.id.btn_left);
        ImageButton ibRight = parentView.findViewById(R.id.btn_right);
        recyclerView = parentView.findViewById(R.id.rv_time);
        btnAdd = parentView.findViewById(R.id.btn_addTime);
         duration = parentView.findViewById(R.id.tv_duration);
        startTime = parentView.findViewById(R.id.tv_start);
        endTime = parentView.findViewById(R.id.tv_endTime);

        pickedDate = parentView.findViewById(R.id.et_date);
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
                navController.popBackStack();


            }
        });
        ibTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeStartDialog();
            }
        });

        ibTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeEndDialog();
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String dur = duration.getText().toString().trim();
                String start = startTime.getText().toString().trim();
                String end = endTime.getText().toString().trim();
                if (dur.isEmpty()) {
                    duration.setError("you need to add duration in minutes");
                } else if (end.isEmpty()) {
                    endTime.setError("you need to add end time of services");
                } else {
               SimpleDateFormat inFormat = new SimpleDateFormat("hh:mm aa");
               SimpleDateFormat outFormat = new SimpleDateFormat("HH:mm");

               String beginTime24 = null;
               String endTime24= null;
               try {
                   beginTime24 = outFormat.format(inFormat.parse(start));
                   endTime24 = outFormat.format(inFormat.parse(end));

               } catch (ParseException e) {
                   e.printStackTrace();
               }
               String endtimeHours = endTime24.substring(0, 2);
               int endTimeIn24hoursFormat = Integer.parseInt(endtimeHours);
               SimpleDateFormat sdf = new SimpleDateFormat("HH:MM");
               try {
                   Calendar startCalendar = Calendar.getInstance();
                   startCalendar.setTime(sdf.parse(beginTime24));
                   Calendar endCalendar = Calendar.getInstance();
                   endCalendar.setTime(startCalendar.getTime());
                   endCalendar.add(Calendar.HOUR_OF_DAY, endTimeIn24hoursFormat - startCalendar.get(Calendar.HOUR_OF_DAY));
                   endCalendar.clear(Calendar.MINUTE);
                   endCalendar.clear(Calendar.SECOND);
                   endCalendar.clear(Calendar.MILLISECOND);
                   SimpleDateFormat slotTime = new SimpleDateFormat("hh:mm a");
                   while (endCalendar.after(startCalendar)) {
                       startCalendar.add(Calendar.MINUTE, Integer.parseInt(dur));
                       String Timeslots = slotTime.format(startCalendar.getTime());
                       addTimeAppointment(dur, start, end, Timeslots);

                       // System.err.println(Timeslots);
                   }
               } catch (ParseException e) {
                   // date in wrong format
               }
           }
                        }
        });


        if(mOffer != null ){
            offerTitle.setText(mOffer.getTitle());
            services.setText(mOffer.getServices());
            curPrice.setText(mOffer.getCurrentPrice());
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

    private void showTimeStartDialog() {

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
                startTime.setText(hour + ":" + minute + " " + time);
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
        startTime.setText(timeNow);
    }

    private void showTimeEndDialog() {

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
                endTime.setText(hour + ":" + minute + " " + time);
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, R.style.MyTimePickerWidgetStyle, timeListener, hour, minutes, false);
        WindowManager.LayoutParams windowManager = new WindowManager.LayoutParams();
        windowManager.width = WindowManager.LayoutParams.WRAP_CONTENT;
        windowManager.height = WindowManager.LayoutParams.WRAP_CONTENT;
        timePickerDialog.getWindow().setAttributes(windowManager);
        timePickerDialog.show();
    }


    private void getCurrentEndTime() {
        SimpleDateFormat serverFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String timeNow = serverFormat.format(Calendar.getInstance().getTime());
        endTime.setText(timeNow);
    }


    private void deleteTime(AppointmentModel category) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference dbRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid()).child(Constants.Salon_Offers)
                .child(mOffer.getOfferId()).child(Constants.Service_Appointment).child(category.getRecordId());
        dbRef.removeValue();
    }

    private void addTimeAppointment(String dur, String start, String end, String timeslots) {
        appointmentModel = new AppointmentModel();
        appointmentModel.setAppointmentDate(pickedDate.getText().toString().trim());
        appointmentModel.setOfferId(mOffer.getOfferId());
        appointmentModel.setOwnerId(mOffer.getSalonOwnerId());
        appointmentModel.setDuration(dur);
        appointmentModel.setPickedTime(timeslots);
        appointmentModel.setAppointmentStartTime(start);
        appointmentModel.setAppointmentEndTime(end);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid()).child(Constants.Salon_Offers)
                .child(mOffer.getOfferId()).child(Constants.Service_Appointment);
        String recordID = dbRef.push().getKey();
        appointmentModel.setRecordId(recordID);
        appointmentModel.setSalonName(mOffer.getSalonName());


        dbRef.child(Objects.requireNonNull(recordID)).setValue(appointmentModel);


    }

    private void showDateDialog() {

        DatePickerDialog.OnDateSetListener selectListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                pickedDate.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
                sDay = String.valueOf(selectedDay);
                sMonth= String.valueOf(selectedMonth);
                sYear= String.valueOf(selectedYear);

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


    private void showPreviousAppointments() {
        String datePicked = pickedDate.getText().toString().trim();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef;
        myRef = (DatabaseReference) database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid())
                .child(Constants.Salon_Offers).child(mOffer.getOfferId()).child(Constants.Service_Appointment);

        recyclerView.setVisibility(View.GONE);
        empty.setVisibility(View.VISIBLE);
        btnAdd.setEnabled(true);
        duration.setEnabled(true);

        btnAdd.getBackground().setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent), PorterDuff.Mode.MULTIPLY);

        myRef.orderByChild("appointmentDate").equalTo(datePicked).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                timeList.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    AppointmentModel category = d.getValue(AppointmentModel.class);
                    timeList.add(category);


                    recyclerView.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);

                    if(recyclerView.getVisibility() == VISIBLE){
                        btnAdd.setEnabled(false);
                        duration.setEnabled(false);
                        btnAdd.getBackground().setColorFilter(ContextCompat.getColor(mContext, R.color.lightGray), PorterDuff.Mode.MULTIPLY);
                    }

                }
               // mAdapter.update(timeList);
                checkIfTimeAlreadyPicked();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

    }

    private void checkIfTimeAlreadyPicked() {
        final String datePicked = pickedDate.getText().toString().trim();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef;

        myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid()).child(Constants.History_Order);

        myRef.orderByChild("appointmentDate").equalTo(datePicked).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String time = snapshot.child("appointmentTime").getValue(String.class);
                    //checkFromSalonServiceAppointment(time);
                    for(AppointmentModel model: timeList){
                        if(model.getPickedTime().equals(time)){
                            int positon = timeList.indexOf(model);
                            model.setBooked(true);
                            timeList.set(positon,model);
                            // checkFromSalonServiceAppointment(time);
                        }
                    }
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
        lineralayoutManager = new GridLayoutManager(mContext, 2);
        recyclerView.setLayoutManager(lineralayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }


}