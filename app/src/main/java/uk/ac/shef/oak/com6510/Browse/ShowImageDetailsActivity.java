/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510.Browse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileDescriptor;
import java.io.IOException;

import uk.ac.shef.oak.com6510.R;

public class ShowImageDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expand_image);

        Bundle b = getIntent().getExtras();
        int position = -1;
        Uri uri;

        Log.i("debug", "ShowImageDetailsActivity: onCreate()");

        if(b != null) {
            position = b.getInt("position");
            uri = Uri.parse(b.getString("uri"));

            if (position != -1){
                ImageView imageView = (ImageView) findViewById(R.id.image);
                Bitmap bmp = null;
                try {
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
            }
        }
    }
}
