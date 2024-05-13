package com.fiubyte.bafix.entities;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fiubyte.bafix.R;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CustomInfoWindow extends MarkerInfoWindow {

    private static final int MAX_LIST_ITEMS = 3;
    ArrayList<TextView> services;
    ArrayList<View> dividers;
    TextView tooManyServicesIndicator;
    TextView ratingAverageText, ratingReviewsAmount, noOpinionsText;
    RatingBar ratingAverageBar;
    LinearLayout ratingLayout;

    ArrayList<ServiceData> servicesList;

    public CustomInfoWindow(MapView mapView, String providerName, ArrayList<ServiceData> servicesList) {
        super(R.layout.maps_info_window, mapView);
        this.servicesList = servicesList;
        initializeProvider(providerName);
        initializeServices();
        initializeDividers();
        tooManyServicesIndicator = mView.findViewById(R.id.too_many_services_indicator);

        updateInfoWindowList();

        ratingAverageText = mView.findViewById(R.id.rating_average_text);
        ratingReviewsAmount = mView.findViewById(R.id.rating_reviews_amount);
        ratingAverageBar = mView.findViewById(R.id.rating_average_bar);
        noOpinionsText = mView.findViewById(R.id.no_opinions_text);
        ratingLayout = mView.findViewById(R.id.rating_layout);

        updateAverageRatingInfo();
    }

    private void initializeProvider(String providerName) {
        ((TextView) mView.findViewById(R.id.provider_name)).setText(providerName);
    }

    private void initializeServices() {
        services = new ArrayList<>();
        services.add(mView.findViewById(R.id.first_service));
        services.add(mView.findViewById(R.id.second_service));
        services.add(mView.findViewById(R.id.third_service));
    }

    private void initializeDividers() {
        dividers = new ArrayList<>();
        dividers.add(mView.findViewById(R.id.first_divider));
        dividers.add(mView.findViewById(R.id.second_divider));
        dividers.add(mView.findViewById(R.id.third_divider));
    }

    private void updateInfoWindowList() {
        for (int i = 0; i < servicesList.size(); i++) {
            if (i == MAX_LIST_ITEMS) {
                dividers.get(MAX_LIST_ITEMS - 2).setVisibility(View.VISIBLE);
                tooManyServicesIndicator.setVisibility(View.VISIBLE);
                break;
            }
            services.get(i).setVisibility(View.VISIBLE);
            services.get(i).setText(servicesList.get(i).getTitle());
            if (i + 1 != servicesList.size()) {
                dividers.get(i).setVisibility(View.VISIBLE);
            }
        }
    }

    private void updateAverageRatingInfo() {
        if (computeUserOpinions() == 0) {
            noOpinionsText.setVisibility(View.VISIBLE);
            ratingLayout.setVisibility(View.GONE);
        } else {
            noOpinionsText.setVisibility(View.GONE);
            ratingLayout.setVisibility(View.VISIBLE);
            ratingAverageText.setText(String.valueOf(computeUserRatingAverage()));
            ratingAverageBar.setRating(computeUserRatingAverage());
            ratingReviewsAmount.setText("(" + computeUserOpinions() + ")");
        }
    }

    private float computeUserRatingAverage() {
        double totalRating = 0;
        int totalRatingsCount = 0;

        for (ServiceData service : servicesList) {
            if (service.getOpinions() != null) {
                for (ServiceOpinionData opinion : service.getOpinions()) {
                    totalRating += opinion.getUserRating();
                    totalRatingsCount++;
                }
            }
        }

        double averageRating = totalRating / totalRatingsCount;

        DecimalFormat twoDForm = new DecimalFormat("#.#");
        return Float.valueOf(twoDForm.format(averageRating));
    }

    private int computeUserOpinions() {
        int totalRatingsCount = 0;

        for (ServiceData service : servicesList) {
            if (service.getOpinions() != null) {
                totalRatingsCount += service.getOpinions().size();
            }
        }
        return totalRatingsCount;
    }

    @Override
    public void onOpen(Object item) {
    }

    @Override
    public void onClose() {
    }

    public void setOnClickListerner(View.OnClickListener onClickListener) {
        mView.findViewById(R.id.provider_name).setOnClickListener(onClickListener);
    }
}
