package com.fiubyte.bafix.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fiubyte.bafix.R;
import com.fiubyte.bafix.entities.ServiceData;

import java.util.ArrayList;

public class ServiceFinderListAdapter extends RecyclerView.Adapter<ServiceFinderListAdapter.MyViewHolder> {
    Context context;
    ArrayList<ServiceData> services;

    public ServiceFinderListAdapter(Context context, ArrayList<ServiceData> services) {
        this.context = context;
        this.services = services;
    }

    @NonNull
    @Override
    public ServiceFinderListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                    int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.provider_list_item, parent, false);
        return new ServiceFinderListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceFinderListAdapter.MyViewHolder holder,
                                 int position) {
        holder.serviceImageView.setImageDrawable(services.get(position).getServicePicture());
        holder.serviceTitleTextView.setText(services.get(position).getTitle());
        holder.maxDistanceTextView.setText("A " + services.get(position).getMaxDistance() + " km");
        holder.providerNameTextView.setText(services.get(position).getProviderName());
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView serviceImageView;
        TextView providerNameTextView, maxDistanceTextView, serviceTitleTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            serviceImageView = itemView.findViewById(R.id.service_picture);
            serviceTitleTextView = itemView.findViewById(R.id.service_title);
            maxDistanceTextView = itemView.findViewById(R.id.max_distance);
            providerNameTextView = itemView.findViewById(R.id.provider_name);
        }
    }
}
