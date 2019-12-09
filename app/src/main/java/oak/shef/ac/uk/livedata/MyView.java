/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package oak.shef.ac.uk.livedata;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.provider.MediaStore;
import android.widget.Toast;

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
    private static final int READ_REQUEST_CODE = 1003;
    private Uri cameraURI;
    private File cameraFILE;
    private String timeStamp;

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
                        checkPermission();
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
                // EasyImage.openGallery(getActivity(), 0); -> to be removed

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
                intent.setType("image/*");

                startActivityForResult(intent, READ_REQUEST_CODE);
            }
        });


        // for browsing
        // Ref: https://ideacloud.co.jp/dev/android_studio_intent.html
        Button buttonBrowse = findViewById(R.id.button2);
        buttonBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyView.this, ShowImageView.class);
                startActivity(intent);
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
    private void checkPermission(){
        Log.d("debug","checkPermission()");
        // If already got permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            cameraIntent();
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
                    "Need permission for saving images!",
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
                cameraIntent();

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
     * Ref: https://stackoverflow.com/questions/42992989/storing-image-resource-id-in-sqlite-database-and-retrieving-it-in-int-array
     *      https://developer.android.com/training/camera/photobasics#TaskPath
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


    /**
     * onActivityResult
     * Desc: This function is for adding images to the external storage
     *       or uploading images from the gallery.
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
                timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
                onURIReturned(uri, timeStamp);
            }
        }


        // To save images
        if (requestCode == RESULT_CAMERA) {
            if(cameraURI != null){
                registerExternalDatabase(cameraFILE);
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
     */
    private void registerExternalDatabase(File file) {
        ContentValues contentValues = new ContentValues();
        ContentResolver contentResolver = MyView.this.getContentResolver();
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        contentValues.put("_data", file.getAbsolutePath());
        contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
    }


    /**
     * onURIReturned
     * Desc: This is for registering the uri/timestamp of a Photo to a photo database.
     */
    private void onURIReturned(Uri uri, String timeStamp) {
        myViewModel.registerPhoto(uri.toString(), timeStamp);
    }
}

