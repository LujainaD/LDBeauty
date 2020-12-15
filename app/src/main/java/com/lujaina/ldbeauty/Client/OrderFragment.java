package com.lujaina.ldbeauty.Client;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class OrderFragment extends Fragment  {
    FirebaseUser mFirebaseUser;
    private DatabaseReference myRef;

    private OrderAdapter serviceAdapter;
    ArrayList<ClientsAppointmentModel> serviceArray;
    private String ownerId;

    private BroadcastReceiver broadcastReceiver;
    public OrderFragment() {
        // Required empty public constructor
    }


  @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      View parentView = inflater.inflate(R.layout.order_confirm3, container, false);

      Button btn_dismiss = parentView.findViewById(R.id.btn_dismiss);

      FirebaseAuth mAuth= FirebaseAuth.getInstance();
      mFirebaseUser = mAuth.getCurrentUser();
      serviceArray = new ArrayList<>();
      serviceAdapter = new OrderAdapter(getContext());
     readClientOrderFromFirebaseDB();

      btn_dismiss.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              saveOrderInHistory();
              Intent i = new Intent(getContext(), HomeActivity.class);
              startActivity(i);
          }
      });

        return parentView;
    }

    private void saveOrderInHistory() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference clientOrderRef = database.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid()).child(Constants.History_Order);
        DatabaseReference salonOrderRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(ownerId).child(Constants.History_Order);

        for(ClientsAppointmentModel model : serviceArray){

            String id = clientOrderRef.push().getKey();
            model.setAppointmentID(id);
            clientOrderRef.child(id).setValue(model);
            salonOrderRef.child(id).setValue(model);

        }
        DatabaseReference cartRef = database.getReference(Constants.Users).child(Constants.Client).child(mFirebaseUser.getUid()).child(Constants.Client_Cart);

        cartRef.setValue(null);
        readSalonOwnerToken(ownerId);

    }

    private void readSalonOwnerToken(String ownerId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(Constants.Users).child("UsersToken").child(ownerId);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tokenId = snapshot.child("tokenId").getValue(String.class);
                sendTokenToServer(tokenId);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendTokenToServer(final String salonOwnerToken) {
        String firebaseURL = "https://fcm.googleapis.com/fcm/send";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, firebaseURL, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("token", salonOwnerToken);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

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

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}