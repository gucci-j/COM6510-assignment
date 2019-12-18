/*
 * Copyright (c) 2019. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510;

/*
 * Copyright (c) 2018. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import java.util.concurrent.TimeUnit;

import uk.ac.shef.oak.com6510.database.PhotoData;
import uk.ac.shef.oak.com6510.database.TripData;

import static java.lang.String.valueOf;

public class Maps extends AppCompatActivity implements OnMapReadyCallback {

    private MyViewModel myViewModel;
    private GoogleMap mMap;
    private List<LatLng> mLatLng;
    private Polyline mPolyline;
    private PolylineOptions mPolylineOptions;
    private static final int ACCESS_FINE_LOCATION = 123;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private View mapView;
    private Button mButtonStart;
    private Button mButtonEnd;
    // for taking & uploading a photo
    private final static int RESULT_CAMERA = 1001;
    private final static int REQUEST_PERMISSION = 1002;
    private static final int READ_REQUEST_CODE = 42;
    private Uri cameraURI;
    private File cameraFILE;
    private String timeStamp;
    private boolean type;
    private Barometer barometer;
    private Thermometer thermometer;
    private Float currentPressureValue;
    private Float currentTemperatureValue;
    private Location mCurrentLocation;
    private Marker mCurrentLocationMarker;
    private Float currentZoomLevel;
    private String mLastUpdateTime;
    private int tripId;
    private Boolean isTheFirstLocation;
    private String totalPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        tripId = intent.getIntExtra("EXTRA_TRIP_ID", -1);
        // if -1: cannot proceed!

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);
        mapFragment.getView();
      
        // Get a new or existing ViewModel from the ViewModelProvider.
        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);
        // Add an observer on the LiveData. The onChanged() method fires
        // when the observed data changes and the activity is
        // in the foreground.

        // Click 'stop' to stop tracking and then turn back to the MyView
        mButtonEnd = (Button) findViewById(R.id.trackingStop);
        mButtonEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLocationUpdates();
                myViewModel.update(tripId,totalPath);
                Intent intent = new Intent(Maps.this, MyView.class);
                startActivity(intent);
            }
        });

        //for Barometer and Thermometer
        barometer = new Barometer(this);
        thermometer = new Thermometer(this);

        // for taking a photo
        if (checkCameraHardware(getApplicationContext()) == true) {
            // A camera button will be visible
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_camera);
            Log.i("debug/MyView", "Make the camera button visible");
            if (fab.getVisibility() != View.VISIBLE) {
               fab.setVisibility(View.VISIBLE);
            }

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        type = true;
                        checkPermission(type);
                    }
                    else {
                        cameraIntent();
                    }
                }
            });
        }

        // for uploading images from the gallery
        // Ref: https://developer.android.com/guide/topics/providers/document-provider
        FloatingActionButton fabGallery = (FloatingActionButton) findViewById(R.id.fab_gallery);
        fabGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    type = false;
                    checkPermission(type);
                } else {
                    uploadIntent();
                }
            }
        });

    }

    /**
     * Check camera hardware
     * Desc: This function checks whether a device has a camera or not.
     * Ref: https://developer.android.com/guide/topics/media/camera
     *      https://developer.android.com/training/camera/photobasics#java
     * @param context
     * @return true or false
     */
    private boolean checkCameraHardware(final Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            Log.i("debug/MyView", "checkCameraHardware: Has a camera");
            return true;
        } else {
            // no camera on this device
            Log.i("debug/MyView", "checkCameraHardware: No camera");
            return false;
        }
    }

    /**
     * Check Permission
     * Desc: This function is for checking permission to get photos.
     * Ref: https://developer.android.com/guide/topics/permissions/overview
     */
    private void checkPermission(boolean flag){
        Log.d("debug/MyView","checkPermission()");
        // If already got permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            if (flag == true) {
                cameraIntent();
            } else {
                uploadIntent();
            }

        }
        // If permission is denied
        else{
            requestPermission();
        }
    }

    /**
     * Request permission
     * Desc: This function requests permissions for storing images in external storage.
     * Ref: https://developer.android.com/reference/android/support/v4/app/ActivityCompat.html#shouldShowRequestPermissionRationale(android.app.Activity,%20java.lang.String)
     */
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(
                    Maps.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        } else {
            Toast toast = Toast.makeText(
                    this,
                    "Need permission for saving and uploading images!",
                    Toast.LENGTH_SHORT);
            toast.show();

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,},
                    REQUEST_PERMISSION);
        }
    }

    /**
     * StartLocationUpdates
     * Desc: start update the location
     *       first is to check the permission
     *       then request for the location callback, tracking started
     */
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null /* Looper */);
    }
  
    /**
     * cameraIntent
     * Desc: This is for creating an intent to take a photo.
     * Ref: https://developer.android.com/training/camera/photobasics#TaskPath
     */
    private void cameraIntent(){
        // designate an external storage folder
        File saveFolder = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        // obtain a timestamp for a photo
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        // define file name
        String fileName = String.format("COM6510_%s.jpg", timeStamp);

        cameraFILE = new File(saveFolder, fileName);
        cameraURI = FileProvider.getUriForFile(
                Maps.this,
                getApplicationContext().getPackageName() + ".fileprovider",
                cameraFILE);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraURI);
        startActivityForResult(intent, RESULT_CAMERA);

    }
    /**
     * uploadIntent
     * Desc: This is for creating an intent to upload a photo from the gallery.
     */
    private void uploadIntent() {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }
    /**
     * it stops the location updates
     */

    private void stopLocationUpdates(){
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    /**
     *
     */
    @Override
    protected void onResume() {
        super.onResume();
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        startLocationUpdates();
        //for sensors restart
        barometer.startSensingPressure();
        thermometer.startSensingTemperature();
    }
    @Override
    protected void onPause() {
        super.onPause();
        barometer.stopBarometer();
        thermometer.stopBarometer();
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            mCurrentLocation = locationResult.getLastLocation();
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            Log.i("MAP", "new location " + mCurrentLocation.toString());
            //  pathLatitude.add(mCurrentLocation.getLatitude());
            //  pathLongitude.add(mCurrentLocation.getLongitude());

            if (mCurrentLocationMarker != null) {
                mCurrentLocationMarker.remove();
            }
            if (mMap != null) {
                mCurrentLocationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                        .title("Current Position")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                // Move the camera to the current location at the start time
                if (mMap.getCameraPosition().zoom > 14.0f) {
                    currentZoomLevel = mMap.getCameraPosition().zoom;
                    moveCameraToCurrentLocation(currentZoomLevel);
                } else {
                    currentZoomLevel = 14.0f;
                    moveCameraToCurrentLocation(currentZoomLevel);
                }
            }

            if (isTheFirstLocation == false) {
                // Add the polyline if the location is not just start
                mPolyline = mMap.addPolyline(mPolylineOptions
                        .add(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                        .width(12)
                        .color(Color.BLUE));
                totalPath=totalPath.concat(valueOf(mCurrentLocation.getLatitude()));
                totalPath=totalPath.concat(" ");
                totalPath=totalPath.concat(valueOf(mCurrentLocation.getLongitude()));
                totalPath=totalPath.concat(" ");

            } else {
                CircleOptions circleOptions = new CircleOptions();
                // Specifying the center of the circle
                circleOptions.center(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
                // Radius of the circle
                circleOptions.radius(30);
                // Border color of the circle
                circleOptions.strokeColor(Color.BLUE);
                // Fill color of the circle
                circleOptions.fillColor(Color.BLUE);
                // Border width of the circle
                circleOptions.strokeWidth(2);
                // Adding the circle to the GoogleMap
                mMap.addCircle(circleOptions);
                moveCameraToCurrentLocation(currentZoomLevel);
                isTheFirstLocation=false;
                totalPath=valueOf(mCurrentLocation.getLatitude());
                totalPath=totalPath.concat(" ");
                totalPath=totalPath.concat(valueOf(mCurrentLocation.getLongitude()));
                totalPath=totalPath.concat(" ");
            }
        }
    };

    private void moveCameraToCurrentLocation(Float currentZoomLevel){
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), currentZoomLevel));
    }

    /**
     * onRequestPermissionsResult
     * Desc: This function deals with the camera permission.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                            mLocationCallback, null /* Looper */);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case REQUEST_PERMISSION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (type == true) {
                        cameraIntent();
                    } else {
                        uploadIntent();
                    }
                } else {
                    Toast toast = Toast.makeText(this,
                            "Need permission for saving images!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        currentZoomLevel=14.0f;
        isTheFirstLocation=true;
        mPolylineOptions=new PolylineOptions();
    }

    /**
     * onActivityResult
     * Desc: This function is for adding images to the external storage
     *       or uploading images from the gallery.
     * Ref: https://stackoverflow.com/questions/50542966/permission-denial-accessing-picture-uri-on-app-restart
     *      https://developer.android.com/guide/topics/providers/document-provider#permissions
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("debug/MyView","onActivityResult()");

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using data.getData().
            Uri uri = null;
            if (data != null) {
                Log.d("debug/MyView","Got data from the gallery");
                uri = data.getData();
                getContentResolver().takePersistableUriPermission(uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
                currentPressureValue =  barometer.getCurrentPressureValue();
                currentTemperatureValue = thermometer.getCurrentTemperatureValue();
                onURIReturned(uri, timeStamp);
                displayPhotoLocation();
            }
        }


        // To save images
        if (requestCode == RESULT_CAMERA) {
            if(cameraURI != null && resultCode == RESULT_OK) {
                Uri uri = registerExternalDatabase(cameraFILE);
                currentPressureValue =  barometer.getCurrentPressureValue();
                currentTemperatureValue = thermometer.getCurrentTemperatureValue();
                Log.i("debug/MyView", "cameraURI: "+cameraURI);
                onURIReturned(cameraURI, timeStamp);
                displayPhotoLocation();
            }
            else{
                Log.d("debug/MyView","onActivityResult: cameraURI is null or cancelled.");
            }
        }
    }
    /**
     * registerDatabase
     * Desc: This function is for registering a photo to the external storage.
     *       Not related to the room database!
     * @param file
     * @return uri Uri
     */
    private Uri registerExternalDatabase(File file) {
        ContentValues contentValues = new ContentValues();
        ContentResolver contentResolver = Maps.this.getContentResolver();
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        contentValues.put("_data", file.getAbsolutePath());
        Uri uri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Log.i("debug/MyView", "registerExternalDatabase: "+uri);

        return uri;
    }
    /**
     * onURIReturned
     * Desc: This is for registering the uri/timestamp/sensor data of a Photo to a photo database.
     * @param uri
     * @param timeStamp
     */
    private void onURIReturned(Uri uri, String timeStamp) {
        Log.i("debug/Maps", "onURIReturned (tripId): "+tripId);
        myViewModel.insertPhoto(uri.toString(), tripId, timeStamp, currentPressureValue, currentTemperatureValue, mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());


    }

    /**
     * DisplayPhotoLocation
     * Desc: add mark to the location where a photo taken or a picture uploaded to the database from gallery
     */
    private void displayPhotoLocation(){
        // update the location of this photo
        if (mMap != null)
            mMap.addMarker(new MarkerOptions().position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                    .title(mLastUpdateTime));
    }
}
