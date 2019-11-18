/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package oak.shef.ac.uk.livedata;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

public class ShowImageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expand_image);

        Bundle b = getIntent().getExtras();
        int position = -1;
        Log.i("Debug", "ShowImageActivity: IN");

        if(b != null) {
            position = b.getInt("position");
            if (position!=-1){
                ImageView imageView = (ImageView) findViewById(R.id.image);
                ImageElement element= ImageAdapter.getItems().get(position);
                if (element.image!=-1) {
                    imageView.setImageResource(element.image);
                } else if (element.file!=null) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(element.file.getAbsolutePath());
                    imageView.setImageBitmap(myBitmap);
                }
            }
        }

    }
}
