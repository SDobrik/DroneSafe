package com.example.owner.dronesafe;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback,
         NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_no_fly) {
            // Handle the camera action
        } else if (id == R.id.nav_obstructions) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("current_location", new LatLng(1,1));
            WeatherFragment weatherFragment = new WeatherFragment();
            weatherFragment.setArguments(bundle);


        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        double Latitude_Marker =43.8;
        double Longitude_Marker =-80.52;
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            GPSTracker gps = new GPSTracker(this);
            if(gps.canGetLocation()){
                Latitude_Marker = gps.getLatitude();
                Longitude_Marker = gps.getLongitude();
            }
        } else {
            // TODO: fix the permissions request, ask using a dialog.
//            ActivityCompat.requestPermissions(th*isActivity,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//            // Show rationale and request permission.
        }


        LatLng Phone_Location = new LatLng(Latitude_Marker,Longitude_Marker);// set it equal to current location
        mMap.animateCamera(CameraUpdateFactory.zoomTo(100));
        mMap.addMarker(new MarkerOptions().position(Phone_Location).title("Where you want to fly"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Phone_Location));
    }
}
