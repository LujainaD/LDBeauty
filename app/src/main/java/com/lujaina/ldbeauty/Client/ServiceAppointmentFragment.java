package com.lujaina.ldbeauty.Client;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lujaina.ldbeauty.Adapters.AppointmentAdapter;
import com.lujaina.ldbeauty.Adapters.SAppointmentAdapter;
import com.lujaina.ldbeauty.Adapters.TimeAdapter;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.AppointmentModel;
import com.lujaina.ldbeauty.Models.ClientsAppointmentModel;
import com.lujaina.ldbeauty.Models.OfferModel;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.Models.ServiceModel;
import com.lujaina.ldbeauty.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;


public class ServiceAppointmentFragment extends Fragment implements SAppointmentAdapter.onTimePickedListener{
    public static final String DATE_FORMAT    = "dd/MM/yyyy";

    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    private MediatorInterface mMediatorInterface;

    private Context mContext;

    RecyclerView recyclerView;
    LinearLayoutManager lineralayoutManager;
    Button btnConfirm;

    private TextView pickedDate;
    private TextView pickedTime;
    private Calendar calendar;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int hour;
    private int minutes;

    String timeNew;

    private ArrayList<AppointmentModel> timeList;
    private SAppointmentAdapter mAdapter;

    String sDay;
    String sMonth;
    String sYear;

    private SPRegistrationModel ownerId;
    private OfferModel offerID;
    private String userName;
    private String userPhone;

    String selectedTime;
    private ServiceModel serviceInfo;


    public ServiceAppointmentFragment() {
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
        View parentView = inflater.inflate(R.layout.fragment_service_appointment, container, false);

        TextView serviceTitle 	= parentView.findViewById(R.id.tv_service);
        TextView specialist = parentView.findViewById(R.id.tv_specialist);
        TextView price 		= parentView.findViewById(R.id.tv_price);
        ImageButton ibCalendar		= parentView.findViewById(R.id.ib_calender);
        ImageButton ibBack 			= parentView.findViewById(R.id.ib_back);
        ImageButton ibLeft 			= parentView.findViewById(R.id.btn_left);
        ImageButton ibRight			= parentView.findViewById(R.id.btn_right);
        recyclerView 	= parentView.findViewById(R.id.rv_time);
        btnConfirm 	=	parentView.findViewById(R.id.btn_confirm);
        pickedDate = parentView.findViewById(R.id.et_date);
        pickedTime = parentView.findViewById(R.id.et_time);


        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();

        getUserInfo();
        getCurrentDate();
        showPreviousAppointments();



        timeList = new ArrayList<>();
        mAdapter = new SAppointmentAdapter(mContext, this);
        recyclerView.setAdapter(mAdapter);
        setupRecyclerView(recyclerView);


        ibCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediatorInterface != null){
                    mMediatorInterface.onBackPressed();
                }

            }
        });



        if(serviceInfo != null ){
            serviceTitle.setText(serviceInfo.getServiceTitle());
            specialist.setText(serviceInfo.getServiceSpecialist());
            price.setText(serviceInfo.getServicePrice());
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



    private void getUserInfo() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef;

        userRef = database.getReference(Constants.Users).child(Constants.All_Users).child(mFirebaseUser.getUid());
        userRef.orderByChild(mFirebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SPRegistrationModel model = dataSnapshot.getValue(SPRegistrationModel.class);
                model.getUserName();
                userName = model.getUserName();
                userPhone = model.getPhoneNumber();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

    }

    private void addAppointmentToDB(AppointmentModel model) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference salonRef;

        salonRef =  database.getReference(Constants.Users).child(Constants.Salon_Owner).child(serviceInfo.getOwnerId())
                .child(Constants.Clients_Appointments);
        DatabaseReference clientRef;

        clientRef = (DatabaseReference) database.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid())
                .child(Constants.Clients_Appointments);
        String appointmentId = salonRef.push().getKey();
        ClientsAppointmentModel clientsAppointment = new ClientsAppointmentModel();
        clientsAppointment.setAppointmentID(appointmentId);
        clientsAppointment.setUserId(mFirebaseUser.getUid());
        clientsAppointment.setOwnerId(serviceInfo.getOwnerId());
        clientsAppointment.setSalonName(serviceInfo.getSalonName());
        clientsAppointment.setClientName(userName);
        clientsAppointment.setOrderDate(currentDate());
        clientsAppointment.setAppointmentTime(selectedTime);
        clientsAppointment.setAppointmentDate(pickedDate.getText().toString().trim());
        clientsAppointment.setServiceId(serviceInfo.getServiceId());
        clientsAppointment.setAppointmentStatus("not confirmed yet");
        clientsAppointment.setSpecialList(serviceInfo.getServiceSpecialist());
        clientsAppointment.setServiceTitle(serviceInfo.getServiceTitle());
        clientsAppointment.setServiceType("Service");
        clientsAppointment.setPrice(serviceInfo.getServicePrice());
        clientsAppointment.setClientPhone(userPhone);

        salonRef.child(appointmentId).setValue(clientsAppointment);
        clientRef.child(appointmentId).setValue(clientsAppointment);

        showConfirmationDialog();

    }

    private void showConfirmationDialog() {
        BookingConfirmationDialog dialog = new BookingConfirmationDialog();
        dialog.show(getChildFragmentManager(), BookingConfirmationDialog.class.getSimpleName());
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

    private void showPreviousAppointments() {
        String datePicked = pickedDate.getText().toString().trim();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef;
        myRef = (DatabaseReference) database.getReference(Constants.Users).child(Constants.Salon_Owner).child(serviceInfo.getOwnerId())
                .child(Constants.Salon_Category).child(serviceInfo.getIdCategory()).child(Constants.Salon_Service).child(serviceInfo.getServiceId()).child(Constants.Service_Appointment);


        myRef.orderByChild("appointmentDate").equalTo(datePicked).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                timeList.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    AppointmentModel appointmentModel = d.getValue(AppointmentModel.class);
                    timeList.add(appointmentModel);
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

        lineralayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(lineralayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    private String currentDate() {
        //Get current date of device
        Calendar c = Calendar.getInstance();

        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        return mDay + "/" + (mMonth + 1) + "/" + mYear;
    }

    private void getCurrentDate() {

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        String currentDate = sdf.format(calendar.getTime());
        pickedDate.setText(currentDate);
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

    }

    public void setServiceID(ServiceModel service) {
        serviceInfo = service;
    }

    @Override
    public void onItemSelected(int position, int previousSelectedPosition, final AppointmentModel model) {
        timeList.get(position).setSelected(!timeList.get(position).isSelected());
        boolean isNeedtoEnable =  timeList.get(position).isSelected();
        if(isNeedtoEnable){
            btnConfirm.setEnabled(true);
            btnConfirm.getBackground().setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    addAppointmentToDB(model);
                    addSelectItemAsBooked(model);
                }
            });
        }else{
            btnConfirm.setEnabled(false);
            btnConfirm.getBackground().setColorFilter(ContextCompat.getColor(mContext, R.color.lightGray), PorterDuff.Mode.MULTIPLY);
        }
        if(position!=previousSelectedPosition) {
            timeList.get(previousSelectedPosition).setSelected(false);
        }
        selectedTime = model.getPickedTime();
        mAdapter.notifyDataSetChanged();
    }

    private void addSelectItemAsBooked(AppointmentModel model) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef;
        myRef = (DatabaseReference) database.getReference(Constants.Users).child(Constants.Salon_Owner).child(serviceInfo.getOwnerId())
                .child(Constants.Salon_Category).child(serviceInfo.getIdCategory()).child(Constants.Salon_Service).child(serviceInfo.getServiceId()).child(Constants.Service_Appointment).child(model.getRecordId());


   myRef.child("isSelected").setValue(model.isSelected());
   myRef.child("appointmentDate").setValue(pickedDate.getText().toString());
   myRef.child("categoryId").setValue(serviceInfo.getIdCategory());
   myRef.child("ownerId").setValue(serviceInfo.getOwnerId());
   myRef.child("pickedTime").setValue(selectedTime);
   myRef.child("recordId").setValue(model.getRecordId());
   myRef.child("serviceId").setValue(serviceInfo.getServiceId());
   myRef.child("isChosen").setValue("yes");

    }
}