/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510.Browse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.List;

import uk.ac.shef.oak.com6510.MyViewModel;
import uk.ac.shef.oak.com6510.R;
import uk.ac.shef.oak.com6510.database.PhotoData;
import uk.ac.shef.oak.com6510.database.callbacks.QueryGetFullPathByTripIdCallback;
import uk.ac.shef.oak.com6510.database.callbacks.QueryGetPhotosByTripIDCallback;
import uk.ac.shef.oak.com6510.database.callbacks.QueryGetTitleByTripIdCallback;


/**
 * ShowImageDetailsActivity
 * This is for showing a detailed information on a selected image.
 */
public class ShowImageDetailsActivity extends AppCompatActivity implements QueryGetTitleByTripIdCallback,QueryGetFullPathByTripIdCallback,OnMapReadyCallback, QueryGetPhotosByTripIDCallback {
    private MyViewModel myViewModel;
    private String tripTitle;
    private String fullPath;
    private QueryGetTitleByTripIdCallback mCallBack;
    private QueryGetFullPathByTripIdCallback FullPathCallBack;
    private QueryGetPhotosByTripIDCallback mGetPhotoByTripIDCallback;
    private GoogleMap mMap;
    private PolylineOptions mPolylineOptions;
    private Polyline mPolyline;
    // The photo's parameters
    private int position = -1;
    private Uri uri;
    private int tripId;
    private Float pressureValue;
    private Float temperatureValue;
    private double GPSLatitude;
    private double GPSLongitude;
    private String timeValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expand_image);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapShow);
        mapFragment.getMapAsync(this);
        Bundle b = getIntent().getExtras();

        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        if(b != null) {
            // get the parameters for photo
            position = b.getInt("position");
            uri = Uri.parse(b.getString("uri"));
            tripId=b.getInt("tripId");
            pressureValue=b.getFloat("pressureValue");
            temperatureValue=b.getFloat("temperatureValue");
            timeValue=b.getString("Time");
            GPSLatitude=b.getDouble("Latitude");
            GPSLongitude=b.getDouble("Longitude");

            // define the call backs
            mCallBack = ShowImageDetailsActivity.this;
            FullPathCallBack=ShowImageDetailsActivity.this;
            mGetPhotoByTripIDCallback = ShowImageDetailsActivity.this;

            // retrieve data
            myViewModel.getTripTitle(tripId,mCallBack);
            myViewModel.getFullPath(tripId,FullPathCallBack);
            myViewModel.getPhotosByTripId(tripId, mGetPhotoByTripIDCallback);

            if (position != -1){
                ImageView imageView = (ImageView) findViewById(R.id.image);
                Bitmap bmp = null;
                try {
                    // display image by using Bitmap
                    ParcelFileDescriptor pfDescriptor = null;
                    pfDescriptor = ShowImageDetailsActivity.this.getContentResolver().openFileDescriptor(uri, "r");
                    if (pfDescriptor != null) {
                        FileDescriptor fileDescriptor = pfDescriptor.getFileDescriptor();
                        bmp = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                        pfDescriptor.close();
                    }imageView.setImageBitmap(bmp);
                } catch (IOException e) {
                    Log.i("debug", "ShowImageDetailsActivity: Error");
                    e.printStackTrace();
                }
                // display pressure value
                TextView pressureView=(TextView) findViewById(R.id.pressureValue);
                pressureView.setText(pressureValue.toString());

                // display temperature value
                TextView temperatureView=(TextView) findViewById(R.id.temperatureValue);
                temperatureView.setText(temperatureValue.toString());
            }
        }
    }

    /**
     * The map ready initialize the parameters for map display
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap){
        mMap=googleMap;
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.addMarker(new MarkerOptions().position(new LatLng(GPSLatitude, GPSLongitude))
                .title(timeValue));
        mPolylineOptions=new PolylineOptions();
    }

    /**
     * The call back for retrieve trip title by using the trip id
     * @param tripTitle
     */
    @Override
    public void onRetrieveFinished(String tripTitle) {
        this.tripTitle=tripTitle;
        TextView titleView=(TextView) findViewById(R.id.tripTitle);
        titleView.setText(tripTitle);
    }

    /**
     * The call back for retrieve the totalPath by using the trip id
     * @param fullPath
     */
    @Override
    public void onRetrieveFullPathFinished(String fullPath){
        this.fullPath=fullPath;
        Boolean isLatitude=true;
        double Latitude=0;
        double Longitude=0;

        // split the path string, assigned to latitude and longitude and draw the total path step by step
        String[] fullPathSplit=fullPath.split("\\s+");
        for(String s:fullPathSplit){
            if(isLatitude) {
                System.out.println("The Latitude"+s);
                Latitude=Double.parseDouble(s);
                isLatitude=false;
            }else{
                System.out.println("The Longitude"+s);
                Longitude=Double.parseDouble(s);
                isLatitude=true;
                mPolyline = mMap.addPolyline(mPolylineOptions
                        .add(new LatLng(Latitude, Longitude))
                        .width(12)
                        .color(Color.BLUE));
            }

        }
        // move the camera to the suitable place with a constant zoom value
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Latitude, Longitude),14));
    }

    /**
     * This call back retrieve all the photoData in order to get the locations for each photos
     * @param data
     */
    @Override
    public void onRetrieveFinished(List<PhotoData> data) {
        // for each photo date, get the parameters(time, latitude and longitude)
        for (PhotoData photo: data) {
            String tempTimeValue=photo.getTime();
            double tempGPSLatitude=photo.getGPSLatitude();
            double tempGPSLongitude=photo.getGPSLongitude();
            // if the location of this photo is not the current one, then add the marker in map with a half transparent value.
            if(tempGPSLatitude!=GPSLatitude&&tempGPSLongitude!=GPSLongitude) {
                mMap.addMarker(new MarkerOptions().position(new LatLng(tempGPSLatitude, tempGPSLongitude))
                            .title(tempTimeValue)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                            .alpha(0.5f));

            }
        }
    }
}
