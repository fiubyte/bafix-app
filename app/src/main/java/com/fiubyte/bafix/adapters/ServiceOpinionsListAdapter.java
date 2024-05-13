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
    private Integer currentUserRating;
    private Boolean userRatingApproved;

    public ServiceOpinionsListAdapter(Context context, ArrayList<ServiceOpinionData> opinions,
                                      Integer currentUserRating, Boolean userRatingApproved,
                                      RatingBarInterface ratingBarInterface) {
        this.ratingBarInterface = ratingBarInterface;
        this.context = context;
        this.opinions = opinions;
        this.currentUserRating = currentUserRating;
        this.userRatingApproved = userRatingApproved;
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
        if (holder.getItemViewType() == VIEW_TYPE_FIRST_ITEM) {
            ((FirstItemViewHolder) holder).bind();
        } else {
            if (position == 1) {
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getRootView().getLayoutParams();
                layoutParams.topMargin = -70;
                holder.itemView.getRootView().setLayoutParams(layoutParams);
            }
            final ServiceOpinionData opinionListItem = opinions.get(position - 1);
            ((NormalItemViewHolder) holder).bind(opinionListItem);
        }
    }

    @Override
    public int getItemCount() {
        return opinions.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_FIRST_ITEM : VIEW_TYPE_NORMAL_ITEM;
    }

    private class FirstItemViewHolder extends RecyclerView.ViewHolder {
        TextView pendingApprovalText;
        SvgRatingBar ratingBar;

        FirstItemViewHolder(@NonNull View itemView, RatingBarInterface ratingBarInterface) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            pendingApprovalText = itemView.findViewById(R.id.pending_approval_text);
        }

        void bind() {
            if((userRatingApproved != null && userRatingApproved) || currentUserRating == null) {
                ratingBar.setVisibility(View.VISIBLE);
                pendingApprovalText.setVisibility(View.GONE);
            } else {
                ratingBar.setVisibility(View.GONE);
                pendingApprovalText.setVisibility(View.VISIBLE);
            }

            if (currentUserRating == null) {
                ratingBar.setRating(0);
            } else {
                ratingBar.setRating(currentUserRating);
            }

            ratingBar.setOnRatingBarChangeListener((ratingBar, rate, fromUser) -> {
                if (ratingBarInterface != null && fromUser) {
                    ratingBarInterface.onBarClicked((int) rate);
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