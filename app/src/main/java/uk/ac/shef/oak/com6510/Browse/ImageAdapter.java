/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510.Browse;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.ac.shef.oak.com6510.MyViewModel;
import uk.ac.shef.oak.com6510.R;
import uk.ac.shef.oak.com6510.database.PhotoData;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.View_Holder> {
    // needs variables for the data
    static private Context context;
    private List<ImageElement> items;
    private MyViewModel myViewModel;


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
                    intent.putExtra("tripId", items.get(position).tripId);
                    intent.putExtra("pressureValue", items.get(position).pressureValue);
                    intent.putExtra("temperatureValue", items.get(position).temperatureValue);
                    intent.putExtra("Time", items.get(position).timeValue);
                    intent.putExtra("Latitude", items.get(position).GPSLatitude);
                    intent.putExtra("Longitude", items.get(position).GPSLongitude);
                    context.startActivity(intent);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // Show alert to choose delete or not
                    // Ref: https://developer.android.com/reference/android/app/AlertDialog
                    new AlertDialog.Builder(context)
                            .setTitle("Confirmation")
                            .setMessage("Do you want to delete this photo?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // If Yes is clicked
                                    myViewModel.deletePhoto(items.get(position).photoId);
                                    Toast.makeText(context, "The selected photo has been deleted!", Toast.LENGTH_SHORT).show();
                                    items.remove(position);
                                    notifyItemRemoved(position); // because we did not pass ImageElement
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                    return true; // for avoiding collision with onClick
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
                int tripId = photo.getTripId();
                int photoId = photo.getId();
                Float pressureValue  = photo.getPressureValue();
                Float temperatureValue= photo.getTemperatureValue();
                String timeValue=photo.getTime();
                double GPSLatitude=photo.getGPSLatitude();
                double GPSLongitude=photo.getGPSLongitude();
                Log.i("debug", "setPhotos: (photoUri) "+photoUri);
                pfDescriptor = cont.getContentResolver().openFileDescriptor(photoUri, "r");
                if (pfDescriptor != null) {

                    FileDescriptor fileDescriptor = pfDescriptor.getFileDescriptor();
                    Bitmap bmp = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                    Bitmap bitmap = ThumbnailUtils.extractThumbnail(bmp, 300, 300,
                                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                    pfDescriptor.close();

                    ImageElement element = new ImageElement(bitmap, photoUri, tripId, photoId, pressureValue,temperatureValue,timeValue,GPSLatitude,GPSLongitude);
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


    public ImageAdapter(Context cont, MyViewModel myViewModel) {
        this.items = new ArrayList<>();
        this.context = cont;
        this.myViewModel = myViewModel;
    }
}
