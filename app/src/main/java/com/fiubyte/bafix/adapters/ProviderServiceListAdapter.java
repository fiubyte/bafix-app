package com.fiubyte.bafix.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.fiubyte.bafix.R;
import com.fiubyte.bafix.entities.ServiceData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProviderServiceListAdapter extends RecyclerView.Adapter<ProviderServiceListAdapter.MyViewHolder> {
    Context context;
    ArrayList<ServiceData> services;

    public ProviderServiceListAdapter(Context context, ArrayList<ServiceData> services) {
        this.context = context;
        this.services = services;
    }

    @NonNull
    @Override
    public ProviderServiceListAdapter.MyViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
                                                                   ) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.service_list_item, parent, false);
        return new ProviderServiceListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ProviderServiceListAdapter.MyViewHolder holder,
            int position
                                ) {
        final ServiceData serviceListItem = services.get(position);

        holder.serviceTitleTextView.setText(serviceListItem.getTitle());

        if(serviceListItem.isAvailable()){
            holder.serviceNotAvailableLayout.setVisibility(View.GONE);
        } else {
            holder.serviceNotAvailableLayout.setVisibility(View.VISIBLE);
        }

        Picasso.with(context).load(serviceListItem.getServicePhotoURL()).resize(600, 600).centerCrop().into(holder.serviceImageView);
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView serviceImageView;
        TextView serviceTitleTextView;

        ConstraintLayout serviceNotAvailableLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            serviceImageView = itemView.findViewById(R.id.service_picture);
            serviceTitleTextView = itemView.findViewById(R.id.service_title);
            serviceNotAvailableLayout = itemView.findViewById(R.id.service_not_available_layout);
        }
    }
}
