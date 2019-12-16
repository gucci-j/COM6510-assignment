/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.util.Date;

public class mapService extends Service {
    private Location mCurrentLocation;
    private String mLastUpdateTime;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("EXIT", "In Service!");
        if (LocationResult.hasResult(intent)) {
            LocationResult locResults = LocationResult.extractResult(intent);
            if (locResults != null) {
                for (Location location : locResults.getLocations()) {
                    if (location == null) {
                        Log.i("EXITNew Location", "Can not get the location" );
                        continue;
                    }
                    //do something with the location
                    Log.i("EXITNew Location", "Current location: " + location);
                    mCurrentLocation = location;
                    mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                    Log.i("MAP", "EXITnew location in Location Service" + mCurrentLocation.toString());
                    // check if the activity has not been closed in the meantime
                    if (Maps.getActivity()!=null){
                        // any modification of the user interface must be done on the UI Thread. The Intent Service is running
                        // in its own thread, so it cannot communicate with the UI.
                        Maps.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    if (Maps.getMap() != null)
                                        Maps.getMap().addMarker(new MarkerOptions().position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                                                .title(mLastUpdateTime)
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
                                    // it centres the camera around the new location
                                    Maps.getMap().moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())));
                                    // it moves the camera to the selected zoom
                                    Maps.getMap().animateCamera(zoom);
                                } catch (Exception e ){
                                    Log.e("LocationService", "Error cannot write on map "+e.getMessage());
                                }
                            }
                        });
                    }else{

                    }
                }

            }

        }
        return super.onStartCommand(intent, flags, startId);
    }
}