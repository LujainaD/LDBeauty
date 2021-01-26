package com.lujaina.ldbeauty.Client;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.os.HandlerCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.lujaina.ldbeauty.Adapters.OrderAdapter;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.HomeActivity;
import com.lujaina.ldbeauty.Models.ClientsAppointmentModel;
import com.lujaina.ldbeauty.R;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class OrderFragment extends Fragment  {
    FirebaseUser mFirebaseUser;
    private DatabaseReference myRef;

    private OrderAdapter serviceAdapter;
    ArrayList<ClientsAppointmentModel> serviceArray;
    private String ownerId;
    NavController navController;

    private BroadcastReceiver broadcastReceiver;
    public OrderFragment() {
        // Required empty public constructor
    }


  @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      View parentView = inflater.inflate(R.layout.order_confirm3, container, false);
      NavHostFragment navHostFragment =
              (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
      navController = navHostFragment.getNavController();
      ownerId = getArguments().getString("ownerId");

      Button btn_dismiss = parentView.findViewById(R.id.btn_dismiss);

      FirebaseAuth mAuth= FirebaseAuth.getInstance();
      mFirebaseUser = mAuth.getCurrentUser();
      serviceArray = new ArrayList<>();
      serviceAdapter = new OrderAdapter(getContext());
     readClientOrderFromFirebaseDB();

      btn_dismiss.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent i = new Intent(getContext(), HomeActivity.class);
              startActivity(i);
              getActivity().finish();
          }
      });

        return parentView;
    }

    private void saveOrderInHistory() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference clientOrderRef = database.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid()).child(Constants.History_Order);

        for(ClientsAppointmentModel model : serviceArray){

            String id = clientOrderRef.push().getKey();
            model.setAppointmentID(id);

            DatabaseReference salonOrderRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(model.getOwnerId()).child(Constants.History_Order);

            /*if(model.getServiceType().equals("Service")){
                clientOrderRefService.child(id).setValue(model);
                salonOrderRefService.child(id).setValue(model);
            }else{*/
                clientOrderRef.child(id).setValue(model);
                salonOrderRef.child(id).setValue(model);
           // }
            DatabaseReference cartRef = database.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid()).child(Constants.Client_Cart);

            cartRef.setValue(null);
            //readSalonOwnerToken(ownerId);
            //readClientToken(model.getOwnerId());
            readSalonOwnerToken(model.getOwnerId());
        }


    }

    private String currentDate() {
        //Get current date of device
        Calendar c = Calendar.getInstance();

        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        return mDay + "/" + (mMonth + 1) + "/" + mYear;
    }


    private void readClientToken(final String ownerId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(Constants.Users).child("UsersToken").child(mFirebaseUser.getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tokenId = snapshot.child("tokenId").getValue(String.class);
                //readSalonOwnerToken(ownerId,tokenId);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readSalonOwnerToken(String ownerId/*, final String clientToken*/) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(Constants.Users).child("UsersToken").child(ownerId);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String salonOwnerToken = snapshot.child("tokenId").getValue(String.class);
                try {
                    sendTokenToServer(salonOwnerToken/*, clientToken*/);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendTokenToServer(final String salonOwnerToken/*, String clientToken*/) throws IOException, JSONException {

        String server_key = "AAAA2PC7CGg:APA91bG7S-cAxwrBJRingUxNiveAooaNiA09O7HRCMQRf-1AGEh6A-GCPgr7j_4nSbkmUdVAXXnCuEqlnjtSh5AM2AOklqRxVMh3rG0lcHQviaRNBF7zWr-91CAWuuXOdgR92nZPEzLM";

        final HttpURLConnection httpcon = (HttpURLConnection) ((new URL("https://fcm.googleapis.com/fcm/send").openConnection()));
        httpcon.setDoOutput(true);
        httpcon.setRequestProperty("Content-Type", "application/json");
        httpcon.setRequestProperty("Authorization", "key="+server_key);
        httpcon.setRequestMethod("POST");

        //httpcon.connect();

        final JSONObject root = new JSONObject();
        JSONObject notification = new JSONObject();
        notification.put("body", "a new Client booked an appointment");
        notification.put("title", "LD Beauty");

        JSONObject data = new JSONObject();
        data.put("senderToken", salonOwnerToken);
        data.put("messageId", "messageId");

        root.put("notification", notification);
        root.put("data", data);
        root.put("to", salonOwnerToken);

        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    // Your implementation
                    httpcon.connect();
                    byte[] postData = (root.toString()).getBytes();
/*
                    byte[] outputBytes = ("{\"to\":" + salonOwnerToken + "," +
                            "\"data\":{\"some\":\"thing\"}," +
                            "\"notification\":{\"LD Beauty\":\"Help\",\"a new Client booked an appointment\":\"me\",\"icon\":\"me\",\"sound\":\"default\",\"badge\":\"18\"}," +
                            "\"priority\":\"high\"," +
                            "\"content_available\":true}").getBytes("UTF-8");*/
                    OutputStream os = httpcon.getOutputStream();
                    os.write(postData);
                    os.close();
                    InputStream input = httpcon.getInputStream();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                        for (String line; (line = reader.readLine()) != null;) {
                           // System.out.println(line);
                        }
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();


    }

    private void readClientOrderFromFirebaseDB() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid()).child(Constants.Client_Cart);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                serviceArray.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    ClientsAppointmentModel appointment = d.getValue(ClientsAppointmentModel.class);
                    serviceArray.add(appointment);
                }
                serviceAdapter.update(serviceArray);
                saveOrderInHistory();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

    }

}