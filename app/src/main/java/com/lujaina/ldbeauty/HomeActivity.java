package com.lujaina.ldbeauty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.lujaina.ldbeauty.AppOwner.AppOwnerProfileFragment;
import com.lujaina.ldbeauty.Client.CartFragment;
import com.lujaina.ldbeauty.Client.ClientProfileFragment;
import com.lujaina.ldbeauty.Dialogs.NoLoginDialogFragment;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.SP.SPProfileFragment;
import com.lujaina.ldbeauty.User.AboutAppFragment;
import com.lujaina.ldbeauty.User.SalonsHomeFragment;
import com.lujaina.ldbeauty.User.SelectedSalonFragment;

import java.text.Bidi;
import java.util.Locale;

import static androidx.navigation.ui.NavigationUI.setupActionBarWithNavController;

public class HomeActivity extends AppCompatActivity implements  BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "fcm";
    private static final String CHANNEL_ID = "Booking";

    private FirebaseAuth mAuth;
    BottomNavigationView bottomNav;
    FirebaseUser user;
    String userRole;
    ProgressDialog progressDialog;
    NavController navController;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNav = findViewById(R.id.bottom_nav);

        bottomNav.setOnNavigationItemSelectedListener(this);
        bottomNav.getMenu().findItem(R.id.nav_search).setChecked(true);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user != null) {
            getUserRole();
        }
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        // String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, token);

                        if (user != null) {
                            saveTokenToDB(token);
                        }
                    }
                });




    }

    private void saveTokenToDB(String token) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference(Constants.Users).child("UsersToken").child(mAuth.getUid());

        myRef.child("tokenId").setValue(token);
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
        progressDialog = new ProgressDialog(HomeActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_bar);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        item.setCheckable(true);
        switch (item.getItemId()) {
            case R.id.nav_search: {
                progressDialog.dismiss();
              //  changeFragmentTo(new SalonsHomeFragment(), SalonsHomeFragment.class.getSimpleName());
                /*Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(intent);*/
                return true;

            }

            case R.id.nav_profile: {
                //Toast.makeText(this, "profile", Toast.LENGTH_SHORT).show();
                if (user != null) {
                    if (userRole != null) {
                       if (userRole.equals("Client")) {
                           if(user.isEmailVerified()){
                               progressDialog.dismiss();
                              // changeFragmentTo(new ClientProfileFragment(), ClientProfileFragment.class.getSimpleName());
                               Intent intent = new Intent(getApplicationContext(),ClientActivity.class);
                               startActivity(intent);
                               finish();
                           }else{
                               progressDialog.dismiss();
                               Toast.makeText(getApplicationContext(), "Please verify your email address", Toast.LENGTH_SHORT).show();
                           }


                           return true;

                       } else if(userRole.equals("Salon Owner")){
                           if(user.isEmailVerified()){
                               progressDialog.dismiss();
                              // navController.navigate(R.id.action_salonsHomeFragment2_to_SPProfileFragment2);
                               Intent intent = new Intent(getApplicationContext(),SalonOwnerActivity.class);
                               startActivity(intent);
                               finish();
                           }else{
                               progressDialog.dismiss();
                               Toast.makeText(getApplicationContext(), "Please verify your email address", Toast.LENGTH_SHORT).show();
                           }
                           return true;

                       }else {
                           progressDialog.dismiss();
                           Intent intent = new Intent(getApplicationContext(),AppOwnerActivity.class);
                           startActivity(intent);
                           finish();
                           // changeFragmentTo(new AppOwnerProfileFragment(), AppOwnerProfileFragment.class.getSimpleName());
                           return true;
                       }

                } else {
                  Bundle bundle = new Bundle();
                  bundle.putInt("num",1);
                  navController.navigate(R.id.noLoginDialogFragment3,bundle);
                  break;
                }
            }else {
                    progressDialog.dismiss();

                    Bundle bundle = new Bundle();
                    bundle.putInt("num",1);
                   /* NoLoginDialogFragment fragment = new NoLoginDialogFragment();
                    fragment.setArguments(bundle);*/
                    //NavHostFragment.findNavController(new NoLoginDialogFragment());
                    navController.navigate(R.id.noLoginDialogFragment3,bundle);

                    break;
                }

            }
            case R.id.nav_app: {
                progressDialog.dismiss();
               // changeFragmentTo(new AboutAppFragment(), AboutAppFragment.class.getSimpleName());
               // navController.navigate(R.id.aboutAppFragment);
                Intent intent = new Intent(getApplicationContext(),AboutAppActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
        }
        return false;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        FirebaseUser user = mAuth.getCurrentUser();
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.side_menu, menu);
        if (user != null) {
            if(userRole != null){
                if (userRole.equals("Client") && user.isEmailVerified()) {
                    menu.getItem(0).setVisible(true);
                    menu.getItem(1).setVisible(false);
                    menu.getItem(2).setVisible(true);
                }else {
                    menu.getItem(0).setVisible(true);
                    menu.getItem(1).setVisible(false);
                    menu.getItem(2).setVisible(false);
                }

            }

        } else {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(false);

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
            if(userRole != null) {
                if (userRole.equals("Client") && user.isEmailVerified()) {
                    menu.getItem(0).setVisible(true);
                    menu.getItem(1).setVisible(false);
                    menu.getItem(2).setVisible(true);
                } else {
                    menu.getItem(0).setVisible(true);
                    menu.getItem(1).setVisible(false);
                    menu.getItem(2).setVisible(false);
                }
            }

        } else {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(false);

        }
        return super.onPrepareOptionsMenu(menu);
    }
    // to handle user click on menu item
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        FirebaseUser user = mAuth.getCurrentUser();
        switch (item.getItemId()) {

            case R.id.log_out_menu:{
                FirebaseAuth.getInstance().signOut();

            }
            case R.id.not_login: {

                //changeFragmentTo(new LoginChoicesFragment(), LoginChoicesFragment.class.getSimpleName());
               /* FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fl_host, new LoginChoicesFragment(), LoginChoicesFragment.class.getSimpleName());
                ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.remove(new LoginChoicesFragment());
                ft.commit();
                fm.popBackStack();*/

                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.my_cart: {
               // changeFragmentTo(new CartFragment(), CartFragment.class.getSimpleName());
                navController.navigate(R.id.action_salonsHomeFragment2_to_cartFragment2);
                break;

            }
            
            case R.id.arabic:{
                changeToArabicLanguage();
            }
            case R.id.english:{
               changeToEnglishLanguage();
            }

        }

        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }

    private void changeToEnglishLanguage() {

        Configuration configuration = getResources().getConfiguration();
        configuration.setLayoutDirection(new Locale("en"));
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

        Intent i = getIntent();
        startActivity(i);
        finish();
    }


    private void changeToArabicLanguage() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                String app_locale = "ar";
                Locale locale = new Locale(app_locale);
                Locale.setDefault(locale);

                //Configuration to query the current layout direction.
                Configuration config = new Configuration();
                config.locale = locale;
                getResources().updateConfiguration(config,
                        getResources().getDisplayMetrics());
                Bidi bidi = new Bidi(app_locale,
                        Bidi.DIRECTION_DEFAULT_RIGHT_TO_LEFT);
                bidi.isRightToLeft();
                YourGlobalClass.updateLanguage(getApplicationContext(), "ar");

                //Refreshing current fragment

                Intent i = getIntent();
                startActivity(i);
                finish();
                return null;
            }

        }.execute();
    }




    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString("Booking_Channel");
            String description = getString("Client Booked an appointment");
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private String getString(String Booking_Channel) {
        return "Booking Channel";
    }
}