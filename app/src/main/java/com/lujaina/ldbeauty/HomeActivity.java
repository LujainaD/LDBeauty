package com.lujaina.ldbeauty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.SP.SPProfileFragment;
import com.lujaina.ldbeauty.SP.SalonsHomeFragment;

public class HomeActivity extends AppCompatActivity implements MediatorInterface, BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNav;
    FirebaseUser mFirebaseUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(this);
        changeFragmentTo(new SalonsHomeFragment(), SalonsHomeFragment.class.getSimpleName());

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FirebaseUser user = mAuth.getCurrentUser();

        switch (item.getItemId()) {
            case R.id.nav_search: {
                changeFragmentTo(new SalonsHomeFragment(), SalonsHomeFragment.class.getSimpleName());
                return true;

            }

            case R.id.nav_profile: {

                if (user != null) {

                    changeFragmentTo(new SPProfileFragment(), SPProfileFragment.class.getSimpleName());
                    return true;
                } else {
                    changeFragmentTo(new LoginChoicesFragment(), LoginChoicesFragment.class.getSimpleName());
                    return true;
                }

            }
            case R.id.nav_app: {
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
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(false);

        } else {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(true);

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
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(false);
        } else {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }
    // to handle user click on menu item

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        FirebaseUser user = mAuth.getCurrentUser();
        switch (item.getItemId()) {
            case R.id.my_profile_menu: {
                if (user != null) {
                    changeFragmentTo(new SPProfileFragment(), SPProfileFragment.class.getSimpleName());
                    break;
                } else {
                    changeFragmentTo(new LoginChoicesFragment(), LoginChoicesFragment.class.getSimpleName());
                }
            }

            case R.id.log_out_menu: {
                changeFragmentTo(new LoginChoicesFragment(), LoginChoicesFragment.class.getSimpleName());
                finish();
            }
            case R.id.choose_menu: {
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
                finish();
            }

        }

        return super.onOptionsItemSelected(item);

    }

}