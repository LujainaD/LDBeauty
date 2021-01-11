package com.lujaina.ldbeauty.SP;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.applandeo.materialcalendarview.utils.DateUtils;
import com.applandeo.materialcalendarview.utils.DayColorsUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.lujaina.ldbeauty.Adapters.AppointmentAdapter;
import com.lujaina.ldbeauty.Adapters.EventsAdapter;
import com.lujaina.ldbeauty.Adapters.SalonAppointmentAdapter;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Dialogs.AppointmentDialog;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.AppointmentModel;
import com.lujaina.ldbeauty.Models.ClientsAppointmentModel;
import com.lujaina.ldbeauty.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static java.util.Collections.unmodifiableList;

public class SpCalenderFragment extends Fragment {
    public static final String DATE_FORMAT    = "d/MM/yyyy";

    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;


    private ArrayList<ClientsAppointmentModel> eventList;
    private EventsAdapter mAdapter;
    TextView noAppointment;
    RecyclerView recyclerView;
    List<EventDay> events;
    com.applandeo.materialcalendarview.CalendarView calendarView;
    public SpCalenderFragment() {
        // Required empty public constructor
    }/*
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MediatorInterface) {
            mMediatorInterface = (MediatorInterface) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement MediatorInterface");
        }
    }*/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_sp_calender, container, false);
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        ImageButton back = parentView.findViewById(R.id.ib_back);
        noAppointment = parentView.findViewById(R.id.textView24);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        calendarView = parentView.findViewById(R.id.calendarView);
        //calendarView.setFirstDayOfWeek(Calendar.SATURDAY);
        recyclerView = parentView.findViewById(R.id.rv_events);

        eventList = new ArrayList<>();
        mAdapter = new EventsAdapter(getContext());
        recyclerView.setAdapter(mAdapter);
        setupRecyclerView(recyclerView);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef;
        myRef = (DatabaseReference) database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mAuth.getUid())
                .child(Constants.History_Order);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventList.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    String date = d.child("appointmentDate").getValue(String.class);
                    try {
                        markeDatesOnCalander(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        events = new ArrayList();

       calendarView.setOnDayClickListener(new OnDayClickListener() {
           @Override
           public void onDayClick(EventDay eventDay) {

               Calendar date = eventDay.getCalendar();
               SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
               String currentDate = sdf.format(date.getTime());
               showEvents(currentDate);
           }
       });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.popBackStack();
            }
        });

        mAdapter.setupOnItemClickListener(new EventsAdapter.onItemClickListener() {
            @Override
            public void onItemClick(ClientsAppointmentModel salonsDetails) {
                AppointmentDialog dialog = new AppointmentDialog();
                dialog.setInfo(salonsDetails);
                dialog.show(getChildFragmentManager(), AppointmentDialog.class.getSimpleName());
            }
        });

        return parentView;
    }

    private void markeDatesOnCalander(String appdate) throws ParseException {
        String strDate = appdate;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        //String currentDate = sdf.format(cal.getTime());
        cal.setTime(sdf.parse(strDate));
        events.add(new EventDay(cal,R.drawable.dot));
        calendarView.setEvents(events);

    }

        private void setupRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager lineralayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(lineralayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void showEvents(String date) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef;
        myRef = (DatabaseReference) database.getReference(Constants.Users).child(Constants.Salon_Owner).child(mAuth.getUid())
                .child(Constants.History_Order);

        noAppointment.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        myRef.orderByChild("appointmentDate").equalTo(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventList.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    ClientsAppointmentModel appointmentModel = d.getValue(ClientsAppointmentModel.class);
                    eventList.add(appointmentModel);

                        recyclerView.setVisibility(View.VISIBLE);
                        noAppointment.setVisibility(View.GONE);


                }
                mAdapter.update(eventList);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }
}