package com.lujaina.ldbeauty.Client;

import android.annotation.SuppressLint;
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
import com.lujaina.ldbeauty.Adapters.AppointmentAdapter;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Dialogs.BookingConfirmationDialog;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.AppointmentModel;
import com.lujaina.ldbeauty.Models.ClientsAppointmentModel;
import com.lujaina.ldbeauty.Models.OfferModel;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class OfferAppointmentFragment extends Fragment implements AppointmentAdapter.onTimePickedListener {
    public static final String DATE_FORMAT = "d/M/yyyy";
    private static final String TAG = "OfferAppointmentFragmen";

    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;

    private MediatorInterface mMediatorInterface;
    private Context mContext;

    private ArrayList<AppointmentModel> timeList;
    private AppointmentAdapter mAdapter;
    private OfferModel offerID;
    private String userName;
    private String userPhone;

    RecyclerView recyclerView;
    LinearLayoutManager lineralayoutManager;
    TextView empty;

    Button btnConfirm;
    String sDay;
    String sMonth;
    String sYear;
    String selectedTime;

    private TextView pickedDate;
    private int mYear;
    private int mMonth;
    private int mDay;
    NavController navController;

    public OfferAppointmentFragment() {
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

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_offer_appointment, container, false);
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        offerID = (OfferModel) getArguments().getSerializable("OfferModel");

        empty = parentView.findViewById(R.id.tv_empty);

        TextView offerTitle = parentView.findViewById(R.id.tv_service);
        TextView services = parentView.findViewById(R.id.tv_specialist);
        TextView curPrice = parentView.findViewById(R.id.tv_price);
        ImageButton ibCalendar = parentView.findViewById(R.id.ib_calender);
        ImageButton ibBack = parentView.findViewById(R.id.ib_back);
        ImageButton ibLeft = parentView.findViewById(R.id.btn_left);
        ImageButton ibRight = parentView.findViewById(R.id.btn_right);
        recyclerView = parentView.findViewById(R.id.rv_time);
        btnConfirm = parentView.findViewById(R.id.btn_confirm);
        pickedDate = parentView.findViewById(R.id.et_date);

        btnConfirm.setClickable(false);
        btnConfirm.setEnabled(false);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        getUserInfo();
        getCurrentDate();
        showPreviousAppointments();


        timeList = new ArrayList<>();
        mAdapter = new AppointmentAdapter(mContext, this);
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


        if (offerID != null) {
            offerTitle.setText(offerID.getTitle());
            services.setText(offerID.getServices());
            curPrice.setText(offerID.getCurrentPrice());
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
/*
        salonRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(offerID.getSalonOwnerId())
                .child(Constants.Client_Cart);*/
        DatabaseReference clientRef;

        clientRef = (DatabaseReference) database.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid())
                .child(Constants.Client_Cart);
        String appointmentId = clientRef.push().getKey();
        ClientsAppointmentModel clientsAppointment = new ClientsAppointmentModel();
        clientsAppointment.setAppointmentID(appointmentId);
        clientsAppointment.setUserId(mFirebaseUser.getUid());
        clientsAppointment.setOwnerId(offerID.getSalonOwnerId());
        clientsAppointment.setSalonName(offerID.getSalonName());
        clientsAppointment.setClientName(userName);
        clientsAppointment.setOrderDate(currentDate());
        clientsAppointment.setAppointmentTime(selectedTime);
        clientsAppointment.setAppointmentDate(pickedDate.getText().toString().trim());
        clientsAppointment.setOfferServices(offerID.getServices());
        clientsAppointment.setOfferId(offerID.getOfferId());
        clientsAppointment.setOfferTitle(offerID.getTitle());
        clientsAppointment.setServiceType("Offer");
        clientsAppointment.setAppointmentStatus("not confirmed yet");
        clientsAppointment.setPrice(offerID.getCurrentPrice());
        clientsAppointment.setClientPhone(userPhone);
        clientsAppointment.setRecordId(model.getRecordId());

        //salonRef.child(appointmentId).setValue(clientsAppointment);
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

    private void showPreviousAppointments() {
        String datePicked = pickedDate.getText().toString().trim();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef;
        myRef = (DatabaseReference) database.getReference(Constants.Users).child(Constants.Salon_Owner).child(offerID.getSalonOwnerId())
                .child(Constants.Salon_Offers).child(offerID.getOfferId()).child(Constants.Service_Appointment);

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
        myRef = (DatabaseReference) database.getReference(Constants.Users).child(Constants.Salon_Owner).child(offerID.getSalonOwnerId())
                .child(Constants.History_Order);

        myRef.orderByChild("appointmentDate").equalTo(datePicked).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String time = snapshot.child("appointmentTime").getValue(String.class);
                    //checkFromSalonServiceAppointment(time);
                    String service = snapshot.child("serviceType").getValue(String.class);
                    String serviceId = snapshot.child("offerId").getValue(String.class);

                    if (service.equals("Offer")) {
                        for (AppointmentModel model : timeList) {
                            if(model.getOfferId().equals(serviceId)){

                                if (model.getPickedTime().equals(time)) {
                                    int positon = timeList.indexOf(model);
                                    model.setBooked(true);
                                    timeList.set(positon, model);
                                }
                            }
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

    public void setOfferId(OfferModel offerModel) {
        offerID = offerModel;
    }

    private String currentDate() {
        //Get current date of device
        Calendar c = Calendar.getInstance();

        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        return mDay + "/" + (mMonth + 1) + "/" + mYear;
    }

    @Override
    public void onItemSelected(final int position, int previousSelectedposition, final AppointmentModel model) {

        timeList.get(position).setSelected(!timeList.get(position).isSelected());
        boolean isNeedtoEnable = timeList.get(position).isSelected();
        if (isNeedtoEnable) {
            btnConfirm.setEnabled(true);
            btnConfirm.getBackground().setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent), PorterDuff.Mode.MULTIPLY);

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    addAppointmentToDB(model);


                }
            });
        } else {
            btnConfirm.setEnabled(false);
            btnConfirm.getBackground().setColorFilter(ContextCompat.getColor(mContext, R.color.lightGray), PorterDuff.Mode.MULTIPLY);
        }
        if (position != previousSelectedposition) {
            timeList.get(previousSelectedposition).setSelected(false);
        }
        selectedTime = model.getPickedTime();
        mAdapter.notifyDataSetChanged();
    }

}
