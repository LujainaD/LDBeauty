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
        //readSalonOwnerToken(ownerId);
        readClientToken(ownerId);
    }

    private void readClientToken(final String ownerId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(Constants.Users).child("UsersToken").child(mFirebaseUser.getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tokenId = snapshot.child("tokenId").getValue(String.class);
                readSalonOwnerToken(ownerId,tokenId);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readSalonOwnerToken(String ownerId, final String clientToken) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(Constants.Users).child("UsersToken").child(ownerId);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tokenId = snapshot.child("tokenId").getValue(String.class);
                try {
                    sendTokenToServer(tokenId, clientToken);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendTokenToServer(final String salonOwnerToken, String clientToken) throws IOException, JSONException {
     /*   String firebaseURL = "https://fcm.googleapis.com/fcm/send";
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
        requestQueue.add(stringRequest);*/

        String server_key = "AAAA2PC7CGg:APA91bG7S-cAxwrBJRingUxNiveAooaNiA09O7HRCMQRf-1AGEh6A-GCPgr7j_4nSbkmUdVAXXnCuEqlnjtSh5AM2AOklqRxVMh3rG0lcHQviaRNBF7zWr-91CAWuuXOdgR92nZPEzLM";

        final HttpURLConnection httpcon = (HttpURLConnection) ((new URL("https://fcm.googleapis.com/fcm/send").openConnection()));
        httpcon.setDoOutput(true);
        httpcon.setRequestProperty("Content-Type", "application/json");
        httpcon.setRequestProperty("Authorization", server_key);
        httpcon.setRequestMethod("POST");

        //httpcon.connect();

        final JSONObject root = new JSONObject();
        JSONObject notification = new JSONObject();
        notification.put("body", "a new Client booked an appointment");
        notification.put("title", "LD Beauty");

        JSONObject data = new JSONObject();
        data.put("senderToken", clientToken);
        data.put("messageId", "messageId");

        root.put("notification", notification);
        root.put("data", data);
        root.put("to", clientToken);

        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    // Your implementation
                    httpcon.connect();
                    String postData = root.toString();

                    byte[] outputBytes = ("{\"to\":" + salonOwnerToken + "," +
                            "\"data\":{\"some\":\"thing\"}," +
                            "\"notification\":{\"LD Beauty\":\"Help\",\"a new Client booked an appointment\":\"me\",\"icon\":\"me\",\"sound\":\"default\",\"badge\":\"18\"}," +
                            "\"priority\":\"high\"," +
                            "\"content_available\":true}").getBytes("UTF-8");
                    OutputStream os = httpcon.getOutputStream();
                    os.write(Integer.parseInt(postData));
                    os.close();
                    InputStream input = httpcon.getInputStream();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                        for (String line; (line = reader.readLine()) != null;) {
                            System.out.println(line);
                        }
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();

/*
        String server_key = "AAAA2PC7CGg:APA91bG7S-cAxwrBJRingUxNiveAooaNiA09O7HRCMQRf-1AGEh6A-GCPgr7j_4nSbkmUdVAXXnCuEqlnjtSh5AM2AOklqRxVMh3rG0lcHQviaRNBF7zWr-91CAWuuXOdgR92nZPEzLM";

        try{
// Create URL instance.
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
// create connection.
            HttpURLConnection conn;
            conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
//set method as POST or GET
            conn.setRequestMethod("POST");
//pass FCM server key
            conn.setRequestProperty("Authorization","key="+server_key);
//Specify Message Format
            conn.setRequestProperty("Content-Type","application/json");
//Create JSON Object & pass value
            JSONObject infoJson = new JSONObject();

            infoJson.put("title","Alankit");
            infoJson.put("body", "a new clinet booke an appointment");

            JSONObject json = new JSONObject();
            json.put("to",salonOwnerToken.trim());
            json.put("notification", infoJson);

            System.out.println("json :" +json.toString());
            System.out.println("infoJson :" +infoJson.toString());
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(json.toString());
            wr.flush();
            int status = 0;
            if( null != conn ){
                status = conn.getResponseCode();
            }
            if( status != 0){

                if( status == 200 ){
//SUCCESS message
                    BufferedReader reader = new BufferedReader(new
                            InputStreamReader(conn.getInputStream()));
                    System.out.println("Android Notification Response : " + reader.readLine());
                }else if(status == 401){
//client side error
                    System.out.println("Notification Response : TokenId : " + salonOwnerToken + " Error occurred :");
                }else if(status == 501){
//server side error
                    System.out.println("Notification Response : [ errorCode=ServerError ] TokenId : " + salonOwnerToken);
                }else if( status == 503){
//server side error
                    System.out.println("Notification Response : FCM Service is Unavailable TokenId : " + salonOwnerToken);
                }
            }
        }catch(MalformedURLException mlfexception){
// Prototcal Error
            System.out.println("Error occurred while sending push Notification!.." + mlfexception.getMessage());
        }catch(Exception mlfexception){
//URL problem
            System.out.println("Reading URL, Error occurred while sending push Notification!.." + mlfexception.getMessage());
        }
*/



    }

   /* static void send_FCM_Notification(String tokenId, String server_key, String

            message){

         server_key = "AAAA2PC7CGg:APA91bG7S-cAxwrBJRingUxNiveAooaNiA09O7HRCMQRf-1AGEh6A-GCPgr7j_4nSbkmUdVAXXnCuEqlnjtSh5AM2AOklqRxVMh3rG0lcHQviaRNBF7zWr-91CAWuuXOdgR92nZPEzLM";

        try{
// Create URL instance.
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
// create connection.
            HttpURLConnection conn;
            conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
//set method as POST or GET
            conn.setRequestMethod("POST");
//pass FCM server key
            conn.setRequestProperty("Authorization","key="+server_key);
//Specify Message Format
            conn.setRequestProperty("Content-Type","application/json");
//Create JSON Object & pass value
            JSONObject infoJson = new JSONObject();

            infoJson.put("title","Alankit");
            infoJson.put("body", message);

            JSONObject json = new JSONObject();
            json.put("to",tokenId.trim());
            json.put("notification", infoJson);

            System.out.println("json :" +json.toString());
            System.out.println("infoJson :" +infoJson.toString());
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(json.toString());
            wr.flush();
            int status = 0;
            if( null != conn ){
                status = conn.getResponseCode();
            }
            if( status != 0){

                if( status == 200 ){
//SUCCESS message
                    BufferedReader reader = new BufferedReader(new
                            InputStreamReader(conn.getInputStream()));
                    System.out.println("Android Notification Response : " + reader.readLine());
                }else if(status == 401){
//client side error
                    System.out.println("Notification Response : TokenId : " + tokenId + " Error occurred :");
                }else if(status == 501){
//server side error
                    System.out.println("Notification Response : [ errorCode=ServerError ] TokenId : " + tokenId);
                }else if( status == 503){
//server side error
                    System.out.println("Notification Response : FCM Service is Unavailable TokenId : " + tokenId);
                }
            }
        }catch(MalformedURLException mlfexception){
// Prototcal Error
            System.out.println("Error occurred while sending push Notification!.." + mlfexception.getMessage());
        }catch(Exception mlfexception){
//URL problem
            System.out.println("Reading URL, Error occurred while sending push Notification!.." + mlfexception.getMessage());
        }

    }
*/

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