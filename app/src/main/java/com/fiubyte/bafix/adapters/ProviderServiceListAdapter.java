package com.fiubyte.bafix.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.fiubyte.bafix.R;
import com.fiubyte.bafix.entities.ServiceData;
import com.fiubyte.bafix.utils.RecylcerViewInterface;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProviderServiceListAdapter extends RecyclerView.Adapter<ProviderServiceListAdapter.MyViewHolder> {
    private final RecylcerViewInterface recylcerViewInterface;
    Context context;
    ArrayList<ServiceData> services;

    public ProviderServiceListAdapter(RecylcerViewInterface recylcerViewInterface, Context context, ArrayList<ServiceData> services) {
        this.recylcerViewInterface = recylcerViewInterface;
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
        View view = inflater.inflate(R.layout.provider_service_list_item, parent, false);
        return new ProviderServiceListAdapter.MyViewHolder(view, recylcerViewInterface);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ProviderServiceListAdapter.MyViewHolder holder,
            int position
                                ) {
        final ServiceData serviceListItem = services.get(position);

        holder.serviceTitleTextView.setText(serviceListItem.getTitle());

        if (serviceListItem.getRatingAverage() == null) {
            holder.noOpinionsText.setVisibility(View.VISIBLE);
            holder.ratingLayout.setVisibility(View.GONE);
        } else {
            holder.noOpinionsText.setVisibility(View.GONE);
            holder.ratingLayout.setVisibility(View.VISIBLE);
            holder.ratingAverageText.setText(String.valueOf(serviceListItem.getRatingAverage()));
            holder.ratingAverageBar.setRating(serviceListItem.getRatingAverage().floatValue());
            holder.ratingReviewsAmount.setText("(" + serviceListItem.getOpinions().size() + ")");
        }

        if(serviceListItem.isAvailable()){
            holder.serviceNotAvailableCardview.setVisibility(View.GONE);
            holder.serviceNotAvailableLayout.setVisibility(View.GONE);
        } else {
            holder.serviceNotAvailableCardview.setVisibility(View.VISIBLE);
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
        CardView serviceNotAvailableCardview;
        ConstraintLayout serviceNotAvailableLayout;

        TextView ratingAverageText, ratingReviewsAmount, noOpinionsText;
        RatingBar ratingAverageBar;
        LinearLayout ratingLayout;

        public MyViewHolder(@NonNull View itemView, RecylcerViewInterface recylcerViewInterface) {
            super(itemView);

            serviceImageView = itemView.findViewById(R.id.service_picture);
            serviceTitleTextView = itemView.findViewById(R.id.service_title);
            serviceNotAvailableCardview = itemView.findViewById(R.id.not_available_cardview);
            serviceNotAvailableLayout = itemView.findViewById(R.id.service_not_available_layout);
            ratingAverageText = itemView.findViewById(R.id.rating_average_text);
            ratingReviewsAmount = itemView.findViewById(R.id.rating_reviews_amount);
            ratingAverageBar = itemView.findViewById(R.id.rating_average_bar);
            noOpinionsText = itemView.findViewById(R.id.no_opinions_text);
            ratingLayout = itemView.findViewById(R.id.rating_layout);

            itemView.setOnClickListener(view -> {
                if (recylcerViewInterface != null) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        recylcerViewInterface.onItemClick(position);
                    }
                }
            });
        }
    }
}
