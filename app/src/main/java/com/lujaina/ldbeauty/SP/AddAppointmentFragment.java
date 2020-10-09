package com.lujaina.ldbeauty.SP;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
import com.lujaina.ldbeauty.Models.ServiceModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class AddAppointmentFragment extends Fragment {
    FirebaseAuth mAuth;
    FirebaseUser mFirebaseUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    private MediatorInterface mMediatorInterface;
    private Context mContext;
    private ServiceModel mService;
    private int mYear;
    private int mMonth;
    private int mDay;
    TextView pickedDate;
    TextView pickedTime;
    String id;
    Calendar c;
    int hour;
    int minutes;
    String timeNew;
    String dateArray[];
    private ArrayList<AppointmentModel> timeList;
    private TimeAdapter mAdapter;
    AppointmentModel model;
    Map map;
    String dateNew;
    String day ;
    String sDay;
    String sMonth;
    String sYear;
    String month;
    String year;
    public AddAppointmentFragment() {
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
        View parentView =  inflater.inflate(R.layout.fragment_add_appointment, container, false);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        readSalonAppointment();
        getCurrentDate();
        getCurrentTime();
        c = Calendar.getInstance();

        final TextView service = parentView.findViewById(R.id.tv_service);
        final TextView specialist = parentView.findViewById(R.id.tv_specialist);
        final TextView price = parentView.findViewById(R.id.tv_price);
        ImageButton calendar = parentView.findViewById(R.id.ib_calender);
        ImageButton timeButton = parentView.findViewById(R.id.ib_time);
        pickedDate =parentView.findViewById(R.id.et_date);
        pickedTime =parentView.findViewById(R.id.et_time);
        Button add = parentView.findViewById(R.id.btn_addTime);
        ImageButton back = parentView.findViewById(R.id.ib_back);
        RecyclerView recyclerView = parentView.findViewById(R.id.rv_time);
        timeList = new ArrayList<>();
        mAdapter = new TimeAdapter(mContext);
        recyclerView.setAdapter(mAdapter);
        setupRecyclerView(recyclerView);
//        readSalonInfoFromFirebaseDB();

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediatorInterface != null){
                    mMediatorInterface.onBackPressed();
                }

            }
        });
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeDialog();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateNew = String.valueOf(pickedDate);
                timeNew = String.valueOf(pickedTime);
                model = new AppointmentModel();
                model.setDay(sDay +"/" + sMonth + "/" + sYear);
                addDateToDB(model);

            }
        });


        if(mService != null ){
            service.setText(mService.getServiceTitle());
            specialist.setText(mService.getServicePrice());
            price.setText(mService.getServicePrice());
        }

        return parentView;
    }

    private void addDateToDB(final AppointmentModel model) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid()).child(Constants.Salon_Category)
                .child(mService.getIdCategory()).child(Constants.Salon_Service).child(mService.getServiceId()).child(Constants.Service_Appointment);
          myRef.child(model.getDay()).setValue(model).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                  Toast.makeText(mContext, "Your service DAy is added successfully ", Toast.LENGTH_SHORT).show();

                  addTimeToDate(model);


              }
          }).addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                  Toast.makeText(mContext, "failed ", Toast.LENGTH_SHORT).show();
              }
          });


    }

    private void addTimeToDate( AppointmentModel model) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid()).child(Constants.Salon_Category)
                .child(mService.getIdCategory()).child(Constants.Salon_Service).child(mService.getServiceId()).child(Constants.Service_Appointment).child(model.getDay());
        String timeId = myRef.push().getKey();

       model.setTimeId(timeId);
       model.setPickedTime(timeNew);
       model.setCategoryId(mService.getIdCategory());
       model.setOwnerId(mFirebaseUser.getUid());
       model.setServiceId(mService.getServiceId());
       model.setPickedDate(dateNew);
        myRef.child(model.getTimeId()).setValue(model).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(mContext, "Your service time is added successfully ", Toast.LENGTH_SHORT).show();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "failed ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setAppointment(ServiceModel service) {
        mService = service;
    }

    private void showDateDialog() {

        DatePickerDialog.OnDateSetListener selectListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                pickedDate.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
                 sDay = String.valueOf(selectedDay);
                 sMonth= String.valueOf(selectedMonth);
                sYear= String.valueOf(selectedYear);

            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, selectListener, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
    private void getCurrentDate() {
        //Get current date of device
        Calendar c = Calendar.getInstance();

        mYear = c.get(Calendar.YEAR);

        mMonth = c.get(Calendar.MONTH);

        mDay = c.get(Calendar.DAY_OF_MONTH);
/*
        pickedDate.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
*/

    }

    private void showTimeDialog() {

        //TODO : to display TimePickerDialog you need to :
        // #1  select Default time. usually current time.
        // #2 Create TimePickerDialog object

        TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectMinute) {

                // time >= 13  PM
                // time < 13 AM

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
        TimePickerDialog timePickerDialog = new TimePickerDialog(mContext,android.R.style.Theme_Holo_Light_DarkActionBar ,timeListener, hour, minutes, true);
        timePickerDialog.getWindow().setBackgroundDrawableResource(R.drawable.inset_rectangle_bg);
        WindowManager.LayoutParams windowManager =  new WindowManager.LayoutParams();
        windowManager.width = WindowManager.LayoutParams.WRAP_CONTENT;
        windowManager.height = WindowManager.LayoutParams.WRAP_CONTENT;
        timePickerDialog.getWindow().setAttributes(windowManager);
        timePickerDialog.show();
    }

    private void getCurrentTime() {
        // get current time
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minutes = c.get(Calendar.MINUTE);

/*
        pickedTime.setText(hour + ":" + minutes);
*/

    }


/*
    public Date getDate(String date){

        String dateArray[] = date.split("/");

        mDay  = Integer.parseInt(dateArray[0]);
        mMonth = Integer.parseInt(dateArray[1]);
        mYear = Integer.parseInt(dateArray[2]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, mDay);
        calendar.set(Calendar.MONTH, mMonth);
        calendar.set(Calendar.YEAR, mYear);


        long time = calendar.getTimeInMillis();

        return new Date(time);
    }
*/

    private void readSalonAppointment() {
        if ((sDay +"/" + sMonth + "/" + sYear) != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mFirebaseUser.getUid()).child(Constants.Salon_Category).child(mService.getIdCategory()).child(Constants.Salon_Service)
                    .child(mService.getServiceId()).child(Constants.Service_Appointment);
            // Read from the mDatabase


            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    timeList.clear();
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        AppointmentModel category = d.getValue(AppointmentModel.class);
                        timeList.add(category);
                        mAdapter.update(timeList);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                }
            });
        }
    }



    private void setupRecyclerView(RecyclerView recyclerView) {

        GridLayoutManager layoutManager = new GridLayoutManager(mContext,3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }
}