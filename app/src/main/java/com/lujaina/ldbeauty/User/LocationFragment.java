package com.lujaina.ldbeauty.User;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lujaina.ldbeauty.Constants;
import com.lujaina.ldbeauty.Interfaces.MediatorInterface;
import com.lujaina.ldbeauty.Models.LocationModel;
import com.lujaina.ldbeauty.Models.SPRegistrationModel;
import com.lujaina.ldbeauty.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class LocationFragment extends Fragment implements OnMapReadyCallback {
    private static final int KEY_PERMISSION_REQUEST_ID = 100;
    String fullAddress;
    double latDouble;
    double longDouble;
    String latitudeFB;
    String longitudeFB;

    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;
    private MapView mMapView;
    private MediatorInterface mMediatorCallback;

    private FloatingActionButton btnGetUserLocation;
    private Marker selectedLocation;

    private SPRegistrationModel ownerId;

    Location locationOfuser;
    Location locationOfSalon;

    LatLng userLocation;
    LatLng locationFB;

    public LocationFragment() {
        // Required empty public constructor
    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MediatorInterface) {
            mMediatorCallback = (MediatorInterface) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement MediatorInterface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_location, container, false);
        ImageButton ibBack = parentView.findViewById(R.id.ib_back);
         btnGetUserLocation = parentView.findViewById(R.id.fab_info);
        final FloatingActionButton zoomOutUserLocation = parentView.findViewById(R.id.fab_zoomout);

        if (isPermissionGranted()) {  // in order to display map in fragment
            mMapView = parentView.findViewById(R.id.map);
            mMapView.onCreate(savedInstanceState);
            mMapView.onResume();// display map ASAP
            MapsInitializer.initialize(getContext());// initialize map
            mMapView.getMapAsync(LocationFragment.this);// link map view with OnMapReadyCallback

           mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        } else {
            requestRuntimePermission();
        }

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediatorCallback != null) {
                    mMediatorCallback.onBackPressed();
                }

            }
        });

        btnGetUserLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 float zoomIn= 16f;
                mMap.animateCamera(CameraUpdateFactory.zoomTo(zoomIn), 1000, null);
                if(zoomIn==16f){
                    zoomOutUserLocation.setVisibility(View.VISIBLE);
                    btnGetUserLocation.setVisibility(View.GONE);

                }
            }
        });
        zoomOutUserLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final float zoomIn= 8f;
                mMap.animateCamera(CameraUpdateFactory.zoomTo(zoomIn), 1000, null);
                if(zoomIn==8f){
                    btnGetUserLocation.setVisibility(View.VISIBLE);
                    zoomOutUserLocation.setVisibility(View.GONE);
                }
            }
        });

        return parentView;
    }


    private boolean isPermissionGranted() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void getLastLocation() {
        if (isLocationEnabled()) {
            //get user last location
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();// get last location

                    if (location == null) {
                        // request new location Data
                        requestNewLocationData();
                    } else {
                        getLocationName(location.getLatitude(),location.getLongitude() );
                    }


                }
            });

        } else {
            Toast.makeText(getContext(), "Please enable GPS", Toast.LENGTH_SHORT).show();

            //take the user to gps enabling screen
            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(i);
            showSalonLocation();

        }
    }

    private void requestRuntimePermission() {
        String permissions[] = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        ActivityCompat.requestPermissions(getActivity(), permissions, KEY_PERMISSION_REQUEST_ID);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    }

    private void requestNewLocationData() {
        //request new location data
        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(0);
        request.setFastestInterval(0);
        request.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.requestLocationUpdates(request, myLocationCallback(), Looper.myLooper());

    }

    private LocationCallback myLocationCallback() {
        LocationCallback callback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                getLocationName(location.getLatitude(),location.getLongitude());
            }
        };

        return callback;
    }

    private void showSalonLocation() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(Constants.Users).child(Constants.Salon_Owner).child(ownerId.getUserId()).child(Constants.Salon_Location);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LocationModel u = dataSnapshot.getValue(LocationModel.class);
                if (u != null) {
                    latitudeFB = Double.toString(u.getLatitude());
                    longitudeFB = Double.toString(u.getLongitude());

                    latDouble = Double.parseDouble(latitudeFB);
                    longDouble = Double.parseDouble(longitudeFB);

                    setSalonLocationPen(latDouble,longDouble,u.getLocationName());
                } else {

                    new AlertDialog.Builder(getContext())
                            .setTitle(ownerId.getSalonName()+ " Location")
                            .setMessage("The Location is not added yet").setIcon(R.drawable.ic_baseline_priority_high_24).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

    }

    private void getLocationName(double lang, double latt) {

        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            //get address name
            List<Address> addresses = geocoder.getFromLocation(lang, latt, 1);
            if(addresses.size()>0) {
                fullAddress = addresses.get(0).getAddressLine(0);
            }
            setUserLocationPen(lang, latt, fullAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setSalonLocationPen(double salonLat , double lang, String fullAddress ) {
        //put marker depend on lat and lon from firebase data
         locationFB = new LatLng(salonLat, lang);

        locationOfSalon = new Location(latitudeFB);
        locationOfSalon.setLatitude(salonLat);
        locationOfSalon.setLongitude(lang);

        Marker salonLocation = mMap.addMarker(new MarkerOptions().position(new LatLng(salonLat, lang)).title(ownerId.getSalonName()));// add marker non the location
        mMap.moveCamera(CameraUpdateFactory.newLatLng(locationFB)); //move camera up,bottom, left, right
        //to zoom to selected location
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16f), 1000, null);

    }

    private void setUserLocationPen(final double lat , final double lang, final String fullAddress ) {
         userLocation = new LatLng(lat, lang);
        if (selectedLocation != null) {
            selectedLocation.remove();
        }
        locationOfuser = new Location(String.valueOf(userLocation));
        locationOfuser.setLatitude(lat);
        locationOfuser.setLongitude(lang);

        selectedLocation = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lang)).title(fullAddress).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        ;// add marker non the location
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation)); //move camera up,bottom, left, right
        //to zoom to selected location


        if(btnGetUserLocation.getVisibility() ==View.VISIBLE){
            mMap.animateCamera(CameraUpdateFactory.zoomTo(8f), 1000, null);
        }

        showDistance();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // default location
        showSalonLocation();
        getLastLocation();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //latLng is the location  that the user clicked
                getLocationName(latLng.latitude,latLng.longitude );
            }
        });
    }

    public void showDistance(){
        float distanceInMeters = locationOfSalon.distanceTo(locationOfuser);

    }

    public void setSalonLocation(SPRegistrationModel section) {
        ownerId = section;
    }
}