package com.fiubyte.bafix.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
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

import com.fiubyte.bafix.BuildConfig;
import com.fiubyte.bafix.R;
import com.fiubyte.bafix.adapters.ServiceFinderListAdapter;
import com.fiubyte.bafix.entities.CustomInfoWindow;
import com.fiubyte.bafix.models.DataViewModel;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServiceFinderFragment extends Fragment implements View.OnClickListener {

    enum ServicesView {
        LIST,
        MAP,
        NO_SERVICES,
        BACKEND_ERROR
    }

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

        initializeViews(view);
        setupViewModels();
        initializeListView();
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

        initializeMap();
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
        if(dataViewModel.isBackendDown().getValue()) {
            currentView = ServicesView.BACKEND_ERROR;
        }
        else if(dataViewModel.getCurrentServices().getValue().isEmpty()){
            currentView = ServicesView.NO_SERVICES;
        }
    }

    private void initializeListView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(),
                                                              LinearLayoutManager.VERTICAL, false
        ));
        ServiceFinderListAdapter adapter = new ServiceFinderListAdapter(
                requireContext(),
                dataViewModel.getCurrentServices().getValue()
        );
        recyclerView.setAdapter(adapter);
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
        }
    }

    private void initializeMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();
        mapController.setCenter(new GeoPoint(-34.6037, -58.3816));
        mapController.setZoom(14);

        addMarkers();

        Marker startMarker = new Marker(mapView);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(startMarker);

        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
    }

    private void addMarkers() {
        double[][] locations = {
                {-34.6037, -58.3816},
                {-34.5829, -58.4109},
                {-34.6083, -58.3700}
        };

        Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.map_location_icon, null);

        ArrayList<String> servicesList = new ArrayList<String>();
        servicesList.add("Reparación de cañerías de agua caliente");
        servicesList.add("Pintura de interiores");
        servicesList.add("Reparación de grietas en el techo");
        servicesList.add("Fabricación de muebles a medida para cocina");

        CustomInfoWindow customInfoWindow = new CustomInfoWindow(mapView, "Mario Delgado", servicesList);

        for (double[] location : locations) {
            Marker marker = new Marker(mapView);
            marker.setPosition(new GeoPoint(location[0], location[1]));
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setIcon(d);
            marker.setInfoWindow(customInfoWindow);
            mapView.getOverlays().add(marker);
        }
    }

}

