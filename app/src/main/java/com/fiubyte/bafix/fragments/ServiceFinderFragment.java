package com.fiubyte.bafix.fragments;

import android.app.Service;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fiubyte.bafix.R;
import com.fiubyte.bafix.adapters.ServiceFinderListAdapter;
import com.fiubyte.bafix.entities.ProviderData;
import com.fiubyte.bafix.entities.ServicesView;
import com.fiubyte.bafix.models.DataViewModel;
import com.fiubyte.bafix.utils.OpenStreetMapManager;
import com.fiubyte.bafix.utils.ProvidersDataGenerator;
import com.fiubyte.bafix.utils.RecylcerViewInterface;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServiceFinderFragment extends Fragment implements View.OnClickListener, RecylcerViewInterface {

    private ArrayList<ProviderData> providers;
    private ServicesView currentView = ServicesView.LIST;
    private Map<ServicesView, View> views;
    private DataViewModel dataViewModel;
    private RecyclerView recyclerView;
    private CardView mapCardView;
    private ImageView filterButton;
    private TextView noServicesOffered;
    private CardView noServicesAvailable;
    private CardView mapsButton;
    private CardView listButton;
    private MapView mapView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
                            ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_service_finder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            ServiceFinderFragmentArgs args = ServiceFinderFragmentArgs.fromBundle(getArguments());
            currentView = args.getServicesView();
        } catch (IllegalArgumentException e) {
            currentView = ServicesView.LIST;
        }

        initializeViews(view);
        setupViewModels();
        initializeListView();
        initializeMap();

        updateServicesView();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.serviceFinderRecyclerView);
        mapCardView = view.findViewById(R.id.map_cardview);
        noServicesOffered = view.findViewById(R.id.no_services_offered);
        noServicesAvailable = view.findViewById(R.id.services_not_available_cardview);
        filterButton = view.findViewById(R.id.filters_button);
        mapsButton = view.findViewById(R.id.maps_button);
        listButton = view.findViewById(R.id.list_button);
        mapView = view.findViewById(R.id.map);

        views = new HashMap<>();
        views.put(ServicesView.LIST, recyclerView);
        views.put(ServicesView.MAP, mapCardView);
        views.put(ServicesView.NO_SERVICES, noServicesOffered);
        views.put(ServicesView.BACKEND_ERROR, noServicesAvailable);

        filterButton.setOnClickListener(this);
        mapsButton.setOnClickListener(this);
        listButton.setOnClickListener(this);
    }

    private void setupViewModels() {
        dataViewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);
    }

    private void updateServicesView() {
        updateCurrentView();

        views.forEach((viewType, view) -> {
            if (viewType == currentView) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
        });

        updateViewSwitcherButtons();
    }

    private void updateViewSwitcherButtons() {
        if (currentView == ServicesView.LIST) {
            mapsButton.setVisibility(View.VISIBLE);
            listButton.setVisibility(View.GONE);
        } else if (currentView == ServicesView.MAP) {
            mapsButton.setVisibility(View.GONE);
            listButton.setVisibility(View.VISIBLE);
        } else {
            mapsButton.setVisibility(View.GONE);
            listButton.setVisibility(View.GONE);
        }
    }

    private void updateCurrentView() {
        if (dataViewModel.isBackendDown().getValue()) {
            currentView = ServicesView.BACKEND_ERROR;
        } else if (dataViewModel.getCurrentServices().getValue().isEmpty()) {
            currentView = ServicesView.NO_SERVICES;
        }
    }

    private void initializeListView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(),
                                                              LinearLayoutManager.VERTICAL, false
        ));
        ServiceFinderListAdapter adapter = new ServiceFinderListAdapter(
                requireContext(),
                dataViewModel.getCurrentServices().getValue(), this
        );
        recyclerView.setAdapter(adapter);
    }

    private void initializeMap() {
        Log.d("DEBUGGING", "initializeMap");
        OpenStreetMapManager.initializeMap(
                mapView,
                ResourcesCompat.getDrawable(getResources(), R.drawable.map_location_icon, null),
                new GeoPoint(dataViewModel.getCurrentLocation().getValue().get("latitude"),
                             dataViewModel.getCurrentLocation().getValue().get("longitude"))
                                          );

        providers = ProvidersDataGenerator.generateProvidersList(dataViewModel.getCurrentServices().getValue());
        OpenStreetMapManager.addMarkers(providers, this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.filters_button:
                Navigation
                        .findNavController(view)
                        .navigate(R.id.action_serviceFinderFragment_to_filtersFragment);
                break;
            case R.id.maps_button:
                currentView = ServicesView.MAP;
                updateServicesView();
                break;
            case R.id.list_button:
                currentView = ServicesView.LIST;
                updateServicesView();
                break;
            case R.id.provider_name:
                handleOnProviderClicked(view);
                break;
        }
    }

    private void handleOnProviderClicked(View view) {
       String providerName = ((TextView)view.findViewById(R.id.provider_name)).getText().toString();

       ProviderData providerData = providers.stream()
                .filter(provider -> provider.getName() == providerName)
                .findFirst().orElse(null);

        ServiceFinderFragmentDirections.ActionServiceFinderFragmentToProviderFragment action =
                ServiceFinderFragmentDirections.actionServiceFinderFragmentToProviderFragment(providerData);

        Navigation
                .findNavController(view)
                .navigate(action);
    }

    @Override
    public void onItemClick(int position) {
        ServiceFinderFragmentDirections.ActionServiceFinderFragmentToServiceFragment action =
                ServiceFinderFragmentDirections.actionServiceFinderFragmentToServiceFragment(dataViewModel.getCurrentServices().getValue().get(position));
        Navigation
                .findNavController(requireView())
                .navigate(action);
    }
}

