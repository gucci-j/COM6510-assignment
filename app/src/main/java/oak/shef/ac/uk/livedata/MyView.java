/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package oak.shef.ac.uk.livedata;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import oak.shef.ac.uk.livedata.database.PhotoData;
import oak.shef.ac.uk.livedata.database.TripData;

public class MyView extends AppCompatActivity {
    private MyViewModel myViewModel;

    // for taking & uploading a photo
    private final static int RESULT_CAMERA = 1001;
    private final static int REQUEST_PERMISSION = 1002;
    private static final int READ_REQUEST_CODE = 42;
    private Uri cameraURI;
    private File cameraFILE;
    private String timeStamp;
    private boolean type;


    // for camera
    private Activity activity;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 2987;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 7829;

    //for barometer and thermometer
    private Barometer barometer;
    private Thermometer thermometer;
    private Float currentPressureValue;
    private Float currentTemperatureValue;

    //for location
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get a new or existing ViewModel from the ViewModelProvider.
        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);
        // Add an observer on the LiveData. The onChanged() method fires
        // when the observed data changes and the activity is
        // in the foreground.
        myViewModel.getAllPhotos().observe(this, new Observer<List<PhotoData>>() {
            @Override
            public void onChanged(@Nullable List<PhotoData> photoData) {
                // Update view here
                Toast.makeText(MyView.this, "onChanged(Photos)",
                        Toast.LENGTH_SHORT).show();
            }
        });
        myViewModel.getAllTrips().observe(this, new Observer<List<TripData>>() {
            @Override
            public void onChanged(@Nullable List<TripData> tripData) {
                // Update view here
                Toast.makeText(MyView.this, "onChanged(Trips)",
                        Toast.LENGTH_SHORT).show();
            }
        });


        /*
        myViewModel.getNumberDataToDisplay().observe(this, new Observer<NumberData>(){
            @Override
            public void onChanged(@Nullable final NumberData newValue) {
                TextView tv= findViewById(R.id.textView);
                // if database is empty
                if (newValue==null)
                    tv.setText("click button");
                else
                    tv.setText(newValue.getNumber()+"");
            }});


        // it generates a request to generate a new random number
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myViewModel.generateNewNumber();
            }
        });
         */

        // Click 'start' then turn to the Maps view
        Button trackingStart=findViewById(R.id.tracking);
        trackingStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent=new Intent(MyView.this,Maps.class);
                startActivity(intent);
            }
        });


        // for taking a photo
        if (checkCameraHardware(getApplicationContext()) == true) {
            // A camera button will be visible
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_camera);
            Log.i("debug", "Make the camera button visible");
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

                }
            }
        });


        // for browsing
        // Ref: https://ideacloud.co.jp/dev/android_studio_intent.html
        Button buttonBrowse = findViewById(R.id.browsing);
        buttonBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyView.this, ShowImageView.class);
                startActivity(intent);
            }
        });

        //for Barometer and Thermometer
        barometer = new Barometer(this);
        thermometer = new Thermometer(this);

        //for location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this,new OnSuccessListener<Location>(){
            @Override
            public void onSuccess(Location location){
                if(location != null){

                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        barometer.startSensingPressure();
        thermometer.startSensingTemperature();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barometer.stopBarometer();
        thermometer.stopBarometer();
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
            Log.i("debug", "checkCameraHardware: Has a camera");
            return true;
        } else {
            // no camera on this device
            Log.i("debug", "checkCameraHardware: No camera");
            return false;
        }
    }


    /**
     * Check Permission
     * Desc: This function is for checking permission to get photos.
     * Ref: https://developer.android.com/guide/topics/permissions/overview
     */
    private void checkPermission(boolean flag){
        Log.d("debug","checkPermission()");
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
                    MyView.this,
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
     * onRequestPermissionsResult
     * Desc: This function deals with the camera permission.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("debug","onRequestPermissionsResult()");
        if (requestCode == REQUEST_PERMISSION) {
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
    }

    /**
     * Camera Intent
     * Desc: TBA
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
                MyView.this,
                getApplicationContext().getPackageName() + ".fileprovider",
                cameraFILE);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraURI);
        startActivityForResult(intent, RESULT_CAMERA);
    }


    private void uploadIntent() {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        //intent.setType("image/*");
        //Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
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
        Log.d("debug","MyView: onActivityResult()");

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
                Log.d("debug","Got data");
                uri = data.getData();
                getContentResolver().takePersistableUriPermission(uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
                currentPressureValue =  barometer.getCurrentPressureValue();
                currentTemperatureValue = thermometer.getCurrentTemperatureValue();
                onURIReturned(uri, timeStamp);
            }
        }


        // To save images
        if (requestCode == RESULT_CAMERA) {
            if(cameraURI != null){
                Uri uri = registerExternalDatabase(cameraFILE);
                currentPressureValue =  barometer.getCurrentPressureValue();
                currentTemperatureValue = thermometer.getCurrentTemperatureValue();
                Log.i("debug", "cameraURI: "+cameraURI);
                onURIReturned(cameraURI, timeStamp);
            }
            else{
                Log.d("debug","cameraURI is null");
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
        ContentResolver contentResolver = MyView.this.getContentResolver();
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        contentValues.put("_data", file.getAbsolutePath());
        Uri uri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Log.i("debug", "registerExternalDatabase: "+uri);

        return uri;
    }

    // content://oak.shef.ac.uk.myapplication.fileprovider/my_images/Android/data/oak.shef.ac.uk.myapplication/files/DCIM/COM6510_20191210_130459.jpg
    // content://com.android.providers.media.documents/document/image%3A129 20191210_130606
    // content://media/external/images/media
    // content://media/external/images/media/131
    // content://com.android.providers.media.documents/document/image%3A131
    // 2019-12-10 22:05:01.228 4663-4663/oak.shef.ac.uk.myapplication I/debug: setPhotos: (photoUri) content://media/external/images/media/148
    // 2019-12-10 22:05:01.239 4663-4663/oak.shef.ac.uk.myapplication I/debug: setPhotos: Error
    // 2019-12-10 22:05:01.240 4663-4663/oak.shef.ac.uk.myapplication W/System.err: java.io.FileNotFoundException: open failed: ENOENT (No such file or directory)

    /**
     * onURIReturned
     * Desc: This is for registering the uri/timestamp of a Photo to a photo database.
     */
    private void onURIReturned(Uri uri, String timeStamp) {
        myViewModel.registerPhoto(uri.toString(), timeStamp, currentPressureValue, currentTemperatureValue);
    }
}

