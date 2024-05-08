package com.fiubyte.bafix.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fiubyte.bafix.R;
import com.fiubyte.bafix.entities.ServiceOpinionData;
import com.fiubyte.bafix.utils.RatingBarInterface;
import com.fiubyte.bafix.utils.RecylcerViewInterface;
import com.fiubyte.bafix.utils.SvgRatingBar;

import java.util.ArrayList;

public class ServiceOpinionsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_FIRST_ITEM = 0;
    private static final int VIEW_TYPE_NORMAL_ITEM = 1;
    private final RatingBarInterface ratingBarInterface;
    private Context context;
    private ArrayList<ServiceOpinionData> opinions;
    private int currentUserRating;

    public ServiceOpinionsListAdapter(Context context, ArrayList<ServiceOpinionData> opinions, int currentUserRating, RatingBarInterface ratingBarInterface) {
        this.ratingBarInterface = ratingBarInterface;
        this.context = context;
        this.opinions = opinions;
        this.currentUserRating = currentUserRating;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == VIEW_TYPE_FIRST_ITEM) {
            View view = inflater.inflate(R.layout.rating_list_item, parent, false);
            return new FirstItemViewHolder(view, ratingBarInterface);
        } else {
            View view = inflater.inflate(R.layout.opinions_list_item, parent, false);
            return new NormalItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ServiceOpinionData opinionListItem = opinions.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_FIRST_ITEM) {
            ((FirstItemViewHolder) holder).bind(currentUserRating);
        } else {
            ((NormalItemViewHolder) holder).bind(opinionListItem);
        }
    }

    @Override
    public int getItemCount() {
        return opinions.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_FIRST_ITEM : VIEW_TYPE_NORMAL_ITEM;
    }

    private class FirstItemViewHolder extends RecyclerView.ViewHolder {

        SvgRatingBar ratingBar;

        FirstItemViewHolder(@NonNull View itemView, RatingBarInterface ratingBarInterface) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.rating_bar);
        }

        void bind(float currentUserRating) {
            ratingBar.setRating(currentUserRating);

            ratingBar.setOnRatingBarChangeListener((ratingBar, v, b) -> {
                if (ratingBarInterface != null) {
                    ratingBarInterface.onBarClicked((int) v);
                }
            });
        }
    }

    private class NormalItemViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userOpinion;
        RatingBar userRating;

        NormalItemViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            userOpinion = itemView.findViewById(R.id.user_opinion);
            userRating = itemView.findViewById(R.id.user_rating);
        }

        void bind(ServiceOpinionData opinionListItem) {
            userName.setText(opinionListItem.getUserName());
            userRating.setRating(opinionListItem.getUserRating());
            userOpinion.setText(opinionListItem.getUserOpinion());
        }
    }
}