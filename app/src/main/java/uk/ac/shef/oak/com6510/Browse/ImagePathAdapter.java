/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510.Browse;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uk.ac.shef.oak.com6510.MyViewModel;
import uk.ac.shef.oak.com6510.R;
import uk.ac.shef.oak.com6510.database.PhotoData;
import uk.ac.shef.oak.com6510.database.TripData;
import uk.ac.shef.oak.com6510.database.callbacks.QueryGetPhotosByTripIDWAdapterCallback;


// parent adapter
public class ImagePathAdapter extends RecyclerView.Adapter<ImagePathAdapter.View_Holder> implements QueryGetPhotosByTripIDWAdapterCallback {
    private List<TripData> paths = new ArrayList<>();
    private Context context;

    private QueryGetPhotosByTripIDWAdapterCallback callback;
    private MyViewModel myViewModel;

    // for formatting date information in the database
    private static SimpleDateFormat parser = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
    private static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US);


    public ImagePathAdapter(Context context, MyViewModel myViewModel) {
        // Get a new or existing ViewModel from the ViewModelProvider.
        this.context = context;
        // Get a new or existing ViewModel from the ViewModelProvider.
        this.myViewModel = myViewModel;
    }

    // single data design
    @NonNull
    @Override
    public View_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_browse_sortedbypath, parent, false);
        return new ImagePathAdapter.View_Holder(itemView);
    }

    // overall data design
    @Override
    public void onBindViewHolder(@NonNull View_Holder holder, int position) {
        TripData currentData = paths.get(position);
        holder.textViewTitle.setText(currentData.getTitle());
        String formattedDate = currentData.getDate();

        // Format date
        try {
            Date date = parser.parse(formattedDate);
            formattedDate = formatter.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.textViewDate.setText(formattedDate);

        Log.i("debug", "ImagePathAdapter: onBindViewHolder(): "+currentData.getId()
                +" "+currentData.getTitle()+" "+formattedDate);

        // Set an image adapter to the recyclerview
        final ImageAdapter mAdapter = new ImageAdapter(context);
        holder.imageRecyclerView.setAdapter(mAdapter);

        // Set up the RecyclerView
        int numberOfColumns = 4;
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, numberOfColumns);
        holder.imageRecyclerView.setLayoutManager(mLayoutManager);
        holder.imageRecyclerView.setHasFixedSize(true);

        callback = this;
        myViewModel.getPhotosByTripIdWAdapter(currentData.getId(), callback, mAdapter);
    }

    @Override
    public void onRetrieveFinished(List<PhotoData> data, ImageAdapter mAdapter) {
        Log.i("debug", "ImagePathAdapter: onRetrieveFinished(): (adapter info) "+mAdapter);
        mAdapter.setPhotos(data, context);
        mAdapter.notifyDataSetChanged();
    }


    public void setPaths(List<TripData> paths) {
        this.paths = paths;
        notifyDataSetChanged(); // Need to replace later!
    }


    @Override
    public int getItemCount() {
        return paths.size();
    }

    public class View_Holder extends RecyclerView.ViewHolder  {
        private TextView textViewTitle;
        private TextView textViewDate;
        RecyclerView imageRecyclerView;

        View_Holder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.recycler_path_title);
            textViewDate = itemView.findViewById(R.id.recycler_path_date);
            imageRecyclerView = itemView.findViewById(R.id.recycler_image);
        }
    }
}
