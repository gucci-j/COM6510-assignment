/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510.Browse;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import uk.ac.shef.oak.com6510.R;
import uk.ac.shef.oak.com6510.database.TripData;

public class PathAdapter extends RecyclerView.Adapter<PathAdapter.Trip_Holder> {
    private List<TripData> paths = new ArrayList<>();
    private OnItemClickListener listener;

    // single data design
    @NonNull
    @Override
    public Trip_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_browse_path, parent, false);
        return new Trip_Holder(itemView);
    }

    // overall data design
    @Override
    public void onBindViewHolder(@NonNull Trip_Holder holder, int position) {
        TripData currentData = paths.get(position);
        holder.textViewTitle.setText(currentData.getTitle());
        holder.textViewDate.setText(currentData.getDate());
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }

    public void setPaths(List<TripData> paths) {
        this.paths = paths;
        notifyDataSetChanged(); // Need to replace later!
    }

    class Trip_Holder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewDate;

        public Trip_Holder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_path_title);
            textViewDate = itemView.findViewById(R.id.text_path_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(paths.get(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(TripData data);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}