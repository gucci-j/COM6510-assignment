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

// import oak.shef.ac.uk.livedata.database.NumberData;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class MyView extends AppCompatActivity {
    // LiveData<NumberData> stringToDisplay;
    private MyViewModel myViewModel;

    // for camera
    private Activity activity;
    static private Context context;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 2987;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 7829;

    private final static int RESULT_CAMERA = 1001;
    private final static int REQUEST_PERMISSION = 1002;
    private Uri cameraURI;
    private File cameraFILE;
    private String timeStamp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        // Get a new or existing ViewModel from the ViewModelProvider.
        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);
        /*
        // Add an observer on the LiveData. The onChanged() method fires
        // when the observed data changes and the activity is
        // in the foreground.
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
        initEasyImage();
        if (checkCameraHardware(getApplicationContext()) == true) {
            // A camera button will be visible
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_camera);
            myViewModel.setCameraButton(fab);

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


        // for gallery
        FloatingActionButton fabGallery = (FloatingActionButton) findViewById(R.id.fab_gallery);
        fabGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EasyImage.openGallery(getActivity(), 0);
            }
        });


        // for browsing
        // ref: https://ideacloud.co.jp/dev/android_studio_intent.html
        Button buttonBrowse = findViewById(R.id.button2);
        buttonBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyView.this, ShowImageView.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("IMG", (Serializable)myViewModel.getPhotos());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }


    public Activity getActivity() {
        return activity;
    }

    private void initEasyImage() {
        EasyImage.configuration(this)
                .setImagesFolderName("EasyImage sample")
                .setCopyTakenPhotosToPublicGalleryAppFolder(true)
                .setCopyPickedImagesToPublicGalleryAppFolder(false)
                .setAllowMultiplePickInGallery(true);
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
     * Desc:
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
     * Desc: This function is for adding images to the external storage or uploading images from the gallery.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("debug","onActivityResult()");

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
                e.printStackTrace();
            }

            @Override
            public void onImagesPicked(List<File> imageFiles, EasyImage.ImageSource source, int type) {
                onPhotosReturned(imageFiles);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(getActivity());
                    if (photoFile != null) photoFile.delete();
                }
            }
        });

        if (requestCode == RESULT_CAMERA) {
            if(cameraURI != null){
                registerExternalDatabase(cameraFILE);
                onURIReturned();
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
    private void onURIReturned() {
        myViewModel.registerPhoto(cameraURI.toString(), timeStamp);
    }

    /**
     * onPhotosReturned
     * Desc: save to the picturelist
     * @param returnedPhotos
     */
    private void onPhotosReturned(List<File> returnedPhotos) {
        myViewModel.saveImage(returnedPhotos);
        // mAdapter.notifyDataSetChanged();
        // mRecyclerView.scrollToPosition(returnedPhotos.size() - 1);
    }
}

