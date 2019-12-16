/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510.Browse;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.ac.shef.oak.com6510.R;
import uk.ac.shef.oak.com6510.database.PhotoData;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.View_Holder> {
    // needs variables for the data
    static private Context context;
    private List<ImageElement> items;


    // single data design
    @Override
    public View_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_browse, parent, false);
        View_Holder holder = new View_Holder(v);
        return holder;
    }


    // overall data design
    @Override
    public void onBindViewHolder(final View_Holder holder, final int position) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the
        // current row on the RecyclerView

        if (holder != null && items.get(position) != null) {
            Log.i("debug", "onBindViewHolder "+items.get(position)+" "+items.get(position).image);
            if (items.get(position).image != -1) {
                holder.imageView.setImageResource(items.get(position).image);
            } else if (items.get(position).file != null) {
                Log.i("debug", "onBindViewHolder: items.get(position).file != null");
                holder.imageView.setImageBitmap(items.get(position).file);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("debug", "ImageAdapter: onClick()");
                    Intent intent = new Intent(context, ShowImageDetailsActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("uri", items.get(position).uri.toString());
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * setPhotos
     * Desc: This function set photo data to show in a recyclerview.
     *       Photo data will be stored by a thumbnail to reduce file size.
     * Ref: https://developer.android.com/reference/android/media/ThumbnailUtils.html
     * @param photos
     */
    public void setPhotos(List<PhotoData> photos, Context cont) {
        List<ImageElement> temp = new ArrayList<>();
        Log.i("debug", "setPhotos");
        try {
            for (PhotoData photo: photos) {
                ParcelFileDescriptor pfDescriptor = null;
                Uri photoUri = Uri.parse(photo.getFilename());
                Log.i("debug", "setPhotos: (photoUri) "+photoUri);
                pfDescriptor = cont.getContentResolver().openFileDescriptor(photoUri, "r");
                if (pfDescriptor != null) {

                    FileDescriptor fileDescriptor = pfDescriptor.getFileDescriptor();
                    Bitmap bmp = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                    Bitmap bitmap = ThumbnailUtils.extractThumbnail(bmp, 300, 300,
                                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                    pfDescriptor.close();

                    ImageElement element = new ImageElement(bitmap, photoUri);
                    temp.add(element);
                    Log.i("debug", "setPhotos: (element) "+element);
                }
            }
        } catch (IOException e) {
            Log.i("debug", "setPhotos: Error");
            e.printStackTrace();
        } finally {
            Log.i("debug", "setPhotos: finally");
            this.items = temp;
        }
    }


    public class View_Holder extends RecyclerView.ViewHolder  {
        ImageView imageView;

        View_Holder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_item);
        }
    }


    public ImageAdapter(Context cont) {
        this.items = new ArrayList<>();
        this.context = cont;
    }

    /*
    public ImageAdapter(Context cont, List<ImageElement> items) {
        super();
        this.items = items;
        context = cont;
    }
     */


    // convenience method for getting data at click position
    ImageElement getItem(int id) {
        return items.get(id);
    }

    /*
    public static List<ImageElement> getItems() {
        return items;
    }

     */
}
