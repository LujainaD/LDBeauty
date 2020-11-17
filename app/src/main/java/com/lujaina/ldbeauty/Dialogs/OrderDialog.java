package com.lujaina.ldbeauty.Dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lujaina.ldbeauty.Adapters.CartServicesAdapter;
import com.lujaina.ldbeauty.Adapters.OrderAdapter;
import com.lujaina.ldbeauty.Adapters.ServiceAdapter;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.HomeActivity;
import com.lujaina.ldbeauty.Models.ClientsAppointmentModel;
import com.lujaina.ldbeauty.Models.PayPalModel;
import com.lujaina.ldbeauty.Models.ServiceModel;
import com.lujaina.ldbeauty.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class OrderDialog extends DialogFragment {
    FirebaseUser mFirebaseUser;
    private DatabaseReference myRef;

    private OrderAdapter serviceAdapter;
    ArrayList<ClientsAppointmentModel> serviceArray;

    public OrderDialog() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.WRAP_CONTENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);

        }
    }

  @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      View parentView = inflater.inflate(R.layout.fragment_order_dialog, container, false);
      Button btn_dismiss = parentView.findViewById(R.id.btn_dismiss);
      TextView tv_date = parentView.findViewById(R.id.tv_date);
      TextView tv_time = parentView.findViewById(R.id.tv_time);

      FirebaseAuth mAuth= FirebaseAuth.getInstance();
      mFirebaseUser = mAuth.getCurrentUser();
      RecyclerView recyclerView = parentView.findViewById(R.id.rv_order);
      serviceArray = new ArrayList<>();
      serviceAdapter = new OrderAdapter(getContext());
      recyclerView.setAdapter(serviceAdapter);
      setupRecyclerView(recyclerView);
      readClientOrderFromFirebaseDB();

      tv_time.setText(getCurrentTime());
      tv_date.setText(currentDate());
      btn_dismiss.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              saveOrderInHistory();
              Intent i = new Intent(getContext(), HomeActivity.class);
              startActivity(i);
              dismiss();
          }
      });

        return parentView;
    }

    private void saveOrderInHistory() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid()).child(Constants.History_Order);

    }

    private void readClientOrderFromFirebaseDB() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid()).child(Constants.Clients_Appointments);


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                serviceArray.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    ClientsAppointmentModel appointment = d.getValue(ClientsAppointmentModel.class);
                    serviceArray.add(appointment);
                }
                serviceAdapter.update(serviceArray);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

    }

    private String currentDate() {
        //Get current date of device
        Calendar c = Calendar.getInstance();

        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        return mDay + "/" + (mMonth + 1) + "/" + mYear;
    }

    private String  getCurrentTime() {
        SimpleDateFormat serverFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String timeNow = serverFormat.format(Calendar.getInstance().getTime());
        return timeNow;
    }
    private void setupRecyclerView(RecyclerView recyclerView) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), layoutManager.getOrientation());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

}