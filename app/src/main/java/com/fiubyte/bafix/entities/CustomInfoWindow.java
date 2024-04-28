package com.fiubyte.bafix.entities;

import android.view.View;
import android.widget.TextView;

import com.fiubyte.bafix.R;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

import java.util.ArrayList;

public class CustomInfoWindow extends MarkerInfoWindow {

    private static final int MAX_LIST_ITEMS = 3;
    ArrayList<TextView> services;
    ArrayList<View> dividers;
    TextView tooManyServicesIndicator;

    public CustomInfoWindow(MapView mapView, String providerName, ArrayList<ServiceData> servicesList) {
        super(R.layout.maps_info_window, mapView);
        initializeProvider(providerName);
        initializeServices();
        initializeDividers();
        tooManyServicesIndicator = mView.findViewById(R.id.too_many_services_indicator);

        updateInfoWindowView(servicesList);
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

    private void updateInfoWindowView(ArrayList<ServiceData> servicesList) {
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
