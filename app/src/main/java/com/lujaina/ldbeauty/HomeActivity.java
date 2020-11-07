package com.lujaina.ldbeauty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lujaina.ldbeauty.Client.ClientProfileFragment;
import com.lujaina.ldbeauty.Dialogs.NoLoginDialogFragment;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.SP.SPProfileFragment;
import com.lujaina.ldbeauty.User.SalonsHomeFragment;
import com.lujaina.ldbeauty.User.SelectedSalonFragment;

public class HomeActivity extends AppCompatActivity implements MediatorInterface, BottomNavigationView.OnNavigationItemSelectedListener {


    private FirebaseAuth mAuth;
    BottomNavigationView bottomNav;
    FirebaseUser user;
    String userRole;
    ProgressDialog progressDialog;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(this);
        changeFragmentTo(new SalonsHomeFragment(), SalonsHomeFragment.class.getSimpleName());
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user != null){
            getUserRole();

        }


    }

    private void getUserRole() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef;
        myRef = (DatabaseReference) database.getReference(Constants.Users).child(Constants.All_Users).child(user.getUid());

        myRef.orderByChild(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                SPRegistrationModel model = snapshot.getValue(SPRegistrationModel.class);
                assert model != null;
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
        progressDialog = new ProgressDialog(HomeActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_bar);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        switch (item.getItemId()) {
            case R.id.nav_search: {
                progressDialog.dismiss();
                changeFragmentTo(new SalonsHomeFragment(), SalonsHomeFragment.class.getSimpleName());
                return true;

            }

            case R.id.nav_profile: {
                if (user != null) {
                    if (userRole != null) {
                       if (userRole.equals("Client")) {
                           progressDialog.dismiss();
                        Toast.makeText(HomeActivity.this, userRole, Toast.LENGTH_SHORT).show();
                           changeFragmentTo(new ClientProfileFragment(), ClientProfileFragment.class.getSimpleName());


                       } else{
                           progressDialog.dismiss();

                           Toast.makeText(HomeActivity.this, userRole, Toast.LENGTH_SHORT).show();
                           changeFragmentTo(new SPProfileFragment(), SPProfileFragment.class.getSimpleName());

                       }
                } else {
                        progressDialog.dismiss();
                        Toast.makeText(HomeActivity.this, "not registered", Toast.LENGTH_SHORT).show();
                        NoLoginDialogFragment dialog = new NoLoginDialogFragment();
                        dialog.show(getSupportFragmentManager(),NoLoginDialogFragment.class.getSimpleName());
                    return true;
                }
            }else {
                    progressDialog.dismiss();
                    Toast.makeText(HomeActivity.this, "not registered", Toast.LENGTH_SHORT).show();
                    NoLoginDialogFragment dialog = new NoLoginDialogFragment();
                    dialog.show(getSupportFragmentManager(),NoLoginDialogFragment.class.getSimpleName());
                    return true;
                }



            }
            case R.id.nav_app: {
                progressDialog.dismiss();
/*
                changeFragmentTo(new AppProfileFragment(), AppProfileFragment.class.getSimpleName());
*/
                return true;
            }


        }
        return false;

    }




    @Override
    public void changeFragmentTo(Fragment fragmentToDisplay, String fragmentTag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl_host, fragmentToDisplay, fragmentTag);
        ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (fm.findFragmentByTag(fragmentTag) == null) {
            ft.addToBackStack(fragmentTag);
        }
        ft.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        FirebaseUser user = mAuth.getCurrentUser();
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.side_menu, menu);
        if (user != null) {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);

        } else {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);

        }
        return super.onCreateOptionsMenu(menu);
/*
        return true;
*/
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
        } else {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }
    // to handle user click on menu item

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        FirebaseUser user = mAuth.getCurrentUser();
        switch (item.getItemId()) {

            case R.id.log_out_menu: {
                changeFragmentTo(new LoginChoicesFragment(), LoginChoicesFragment.class.getSimpleName());
            }
            case R.id.not_login: {
                changeFragmentTo(new LoginChoicesFragment(), LoginChoicesFragment.class.getSimpleName());
            }

        }

        return super.onOptionsItemSelected(item);

    }
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }


}