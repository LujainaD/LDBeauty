package com.lujaina.ldbeauty.Client;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lujaina.ldbeauty.Adapters.SAppointmentAdapter;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Dialogs.BookingConfirmationDialog;
import com.lujaina.ldbeauty.Models.AppointmentModel;
import com.lujaina.ldbeauty.Models.ClientsAppointmentModel;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.Models.ServiceModel;
import com.lujaina.ldbeauty.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;


public class ServiceAppointmentFragment extends Fragment implements SAppointmentAdapter.onTimePickedListener{
    public static final String DATE_FORMAT    = "d/M/yyyy";

    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    private Context mContext;

    RecyclerView recyclerView;
    LinearLayoutManager lineralayoutManager;
    Button btnConfirm;
    TextView empty;

    private TextView pickedDate;
    private int mYear;
    private int mMonth;
    private int mDay;

    private ArrayList<AppointmentModel> timeList;
    private SAppointmentAdapter mAdapter;

    private String userName;
    private String userPhone;

    private String selectedTime;
    private ServiceModel serviceInfo;
    NavController navController;

    public ServiceAppointmentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        /*if (context instanceof MediatorInterface) {
            mMediatorInterface = (MediatorInterface) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement MediatorInterface");
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_service_appointment, container, false);

        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        serviceInfo = (ServiceModel) getArguments().getSerializable("ServiceModel");

        empty = parentView.findViewById(R.id.tv_empty);

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
                navController.popBackStack();
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
        DatabaseReference clientRef;

        clientRef = (DatabaseReference) database.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid())
                .child(Constants.Client_Cart);
        String appointmentId = clientRef.push().getKey();
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
                showPreviousAppointments();
               //checkIfTimeAlreadyPicked();
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

        recyclerView.setVisibility(View.GONE);
        empty.setVisibility(View.VISIBLE);
        myRef.orderByChild("appointmentDate").equalTo(datePicked).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                timeList.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    AppointmentModel appointmentModel = d.getValue(AppointmentModel.class);

                    timeList.add(appointmentModel);
                    recyclerView.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
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
        myRef = (DatabaseReference) database.getReference(Constants.Users).child(Constants.Salon_Owner).child(serviceInfo.getOwnerId())
                .child(Constants.History_Order);

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

   /* public void setServiceID(ServiceModel service) {
        serviceInfo = service;
    }
*/
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
                   // addSelectItemAsBooked(model);
                    //readTimeFromHistoryOrder(model);

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


}