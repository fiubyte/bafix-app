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
import com.fiubyte.bafix.entities.ProviderData;

import java.util.ArrayList;

public class ServiceFinderListAdapter extends RecyclerView.Adapter<ServiceFinderListAdapter.MyViewHolder> {
    Context context;
    ArrayList<ProviderData> providers;

    public ServiceFinderListAdapter(Context context, ArrayList<ProviderData> providers) {
        this.context = context;
        this.providers = providers;
    }

    @NonNull
    @Override
    public ServiceFinderListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.provider_list_item, parent, false);
        return new ServiceFinderListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceFinderListAdapter.MyViewHolder holder, int position) {
        holder.providerProfileImageView.setImageResource(providers.get(position).getProviderProfileImage());
        holder.providerNameTextView.setText(providers.get(position).getProviderName());
        holder.providerMaxDistanceTextView.setText("A " + providers.get(position).getProviderMaxDistance() + " km");

        String categories = "";
        for(int i=0; i < providers.get(position).getProviderCategories().size(); i++){
            categories = categories + providers.get(position).getProviderCategories().get(i);
            if (i != (providers.get(position).getProviderCategories().size() - 1)) {
                categories = categories + " - ";
            }
        }
        holder.providerCategoriesTextView.setText(categories);
    }

    @Override
    public int getItemCount() {
        return providers.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView providerProfileImageView;
        TextView providerNameTextView, providerMaxDistanceTextView, providerCategoriesTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            providerProfileImageView = itemView.findViewById(R.id.profile_image);
            providerNameTextView = itemView.findViewById(R.id.provider_name);
            providerMaxDistanceTextView = itemView.findViewById(R.id.max_distance);
            providerCategoriesTextView = itemView.findViewById(R.id.categories);
        }
    }
}
