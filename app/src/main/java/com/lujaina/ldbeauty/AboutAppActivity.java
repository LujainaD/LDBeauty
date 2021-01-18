package com.lujaina.ldbeauty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;

public class AboutAppActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    BottomNavigationView bottomNav;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    String userRole;
    NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(this);
        bottomNav.setSelectedItemId(R.id.nav_app);

        String forSelect = getIntent().getStringExtra("APP");

        if (forSelect!=null) {
            Toast.makeText(this, forSelect, Toast.LENGTH_SHORT).show();
            if(forSelect.equals("APP")){
                bottomNav.getMenu().findItem(R.id.nav_search).setChecked(true);

            }
        }

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user != null) {
            getUserRole();
        }
    }

    private void getUserRole() {
        //userRole = SPRegistrationModel.getInstance().getUserType();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef;
        myRef = (DatabaseReference) database.getReference(Constants.Users).child(Constants.All_Users).child(user.getUid());

        myRef.orderByChild(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SPRegistrationModel model = snapshot.getValue(SPRegistrationModel.class);
                model.getUserType();

                userRole = model.getUserType();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_bar);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        item.setCheckable(true);
        switch (item.getItemId()) {
            case R.id.nav_search: {

                progressDialog.dismiss();
                //  changeFragmentTo(new SalonsHomeFragment(), SalonsHomeFragment.class.getSimpleName());
                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(intent);
                finish();
                return true;

            }
            case R.id.nav_profile: {
                if (user != null) {
                    if (userRole != null) {
                        if (userRole.equals("Client")) {
                            if (user.isEmailVerified()) {
                                progressDialog.dismiss();
                                // changeFragmentTo(new ClientProfileFragment(), ClientProfileFragment.class.getSimpleName());
                                Intent intent = new Intent(getApplicationContext(), ClientActivity.class);
                                startActivity(intent);
                                finish();
                                ;
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Please verify your email address", Toast.LENGTH_SHORT).show();
                            }


                            return true;

                        } else if (userRole.equals("Salon Owner")) {
                            if (user.isEmailVerified()) {
                                progressDialog.dismiss();
                                Intent intent = new Intent(getApplicationContext(),SalonOwnerActivity.class);
                                startActivity(intent);
                                finish();
                               // navController.navigate(R.id.action_salonsHomeFragment2_to_SPProfileFragment2);
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Please verify your email address", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                    }
                }
            }
            case R.id.nav_app: {
                progressDialog.dismiss();
                return true;
            }
        }
        return false;


    }
}