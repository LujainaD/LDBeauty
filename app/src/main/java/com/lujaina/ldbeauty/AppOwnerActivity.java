package com.lujaina.ldbeauty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AppOwnerActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener  {

    BottomNavigationView bottomNav;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_owner);
        bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(this);
        bottomNav.setSelectedItemId(R.id.nav_profile);


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
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
                return true;

            }

            case R.id.nav_profile: {
                progressDialog.dismiss();
                return true;
            }
            case R.id.nav_app: {
                progressDialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), AboutAppActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
        }
        return false;

    }
}