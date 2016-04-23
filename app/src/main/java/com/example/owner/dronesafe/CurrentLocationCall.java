package com.example.owner.dronesafe;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Spencer Dobrik on 2016-04-23.
 */
public class CurrentLocationCall extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private String mLatitudeText;
    private String mLongitudeText;


    @Override
    public void onConnected(Bundle connectionHint) {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }

        GoogleApiClient base;
        //Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(base.connect());
//        if (mLastLocation != null) {
//             mLatitudeText= (String.valueOf(mLastLocation.getLatitude()));
//             mLongitudeText=(String.valueOf(mLastLocation.getLongitude()));
//        }
//        base.disconnect();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public String CurrentLocationLat(){
        return mLatitudeText;
    }
    public String CurrentLocationLon(){
        return mLongitudeText;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}

