package com.lujaina.ldbeauty.SP;

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
import android.widget.CalendarView;
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
import com.lujaina.ldbeauty.Adapters.EventsAdapter;
import com.lujaina.ldbeauty.Adapters.SalonAppointmentAdapter;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Dialogs.AppointmentDialog;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.AppointmentModel;
import com.lujaina.ldbeauty.Models.ClientsAppointmentModel;
import com.lujaina.ldbeauty.R;

import java.util.ArrayList;
import java.util.Calendar;

public class SpCalenderFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;

    private MediatorInterface mMediatorInterface;

    private ArrayList<ClientsAppointmentModel> eventList;
    private EventsAdapter mAdapter;
    TextView noAppointment;
    RecyclerView recyclerView;
    public SpCalenderFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
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
        View parentView =  inflater.inflate(R.layout.fragment_sp_calender, container, false);
        ImageButton back = parentView.findViewById(R.id.ib_back);
         noAppointment = parentView.findViewById(R.id.textView24);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        CalendarView calendarView = parentView.findViewById(R.id.calendarView);
        calendarView.setFirstDayOfWeek(Calendar.SATURDAY);
         recyclerView = parentView.findViewById(R.id.rv_events);

        eventList = new ArrayList<>();
        mAdapter = new EventsAdapter(getContext());
        recyclerView.setAdapter(mAdapter);
        setupRecyclerView(recyclerView);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediatorInterface != null){
                    mMediatorInterface.onBackPressed();
                }
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


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                int month1 = month+1;
                String date = dayOfMonth+"/"+ month1+"/"+year;
                showEvents(date);
                Toast.makeText(getContext(), date, Toast.LENGTH_SHORT).show();
            }
        });
        return parentView;
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

        myRef.orderByChild("appointmentDate").equalTo(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventList.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    ClientsAppointmentModel appointmentModel = d.getValue(ClientsAppointmentModel.class);
                    eventList.add(appointmentModel);
                    if (eventList.isEmpty()) {
                        noAppointment.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);


                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        noAppointment.setVisibility(View.GONE);

                    }
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