/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package oak.shef.ac.uk.livedata;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.View_Holder> {
    // needs variables for the data
    static private Context context;
    private static List<ImageElement> items = new ArrayList<>();

    public ImageAdapter(List<ImageElement> items) {
        this.items = items;
    }

    public ImageAdapter(Context cont, List<ImageElement> items) {
        super();
        this.items = items;
        context = cont;
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_image,
                parent, false);
        View_Holder holder = new View_Holder(v);
        // context = parent.getContext();
        return holder;
    }


    @Override
    public void onBindViewHolder(final View_Holder holder, final int position) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the
        // current row on the RecyclerView
        if (holder!=null && items.get(position)!=null) {
            if (items.get(position).image!=-1) {
                holder.imageView.setImageResource(items.get(position).image);
            } else if (items.get(position).file!=null){
                Bitmap myBitmap = BitmapFactory.decodeFile(items.get(position).file.getAbsolutePath());
                holder.imageView.setImageBitmap(myBitmap);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("Debug", "ImageAdapter: onClick");
                    Intent intent = new Intent(context, ShowImageActivity.class);
                    intent.putExtra("position", position);
                    context.startActivity(intent);
                }
            });
        }
        //animate(holder);
    }


    public class View_Holder extends RecyclerView.ViewHolder  {
        ImageView imageView;

        View_Holder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_item);
        }
    }

    // convenience method for getting data at click position
    ImageElement getItem(int id) {
        return items.get(id);
    }
    @Override
    public int getItemCount() {
        return items.size();
    }
    public static List<ImageElement> getItems() {
        return items;
    }

    public void setItems(List<ImageElement> items) {
        ImageAdapter.items = items;
        notifyDataSetChanged();
    }
}