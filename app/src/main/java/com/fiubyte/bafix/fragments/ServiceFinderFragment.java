package com.fiubyte.bafix.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fiubyte.bafix.R;
import com.fiubyte.bafix.adapters.ServiceFinderListAdapter;
import com.fiubyte.bafix.models.DataViewModel;

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

    private ImageView filterButton;

    private TextView noServicesOffered;
    private CardView noServicesAvailable;
    private CardView mapsButton;

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

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.serviceFinderRecyclerView);
        noServicesOffered = view.findViewById(R.id.no_services_offered);
        noServicesAvailable = view.findViewById(R.id.services_not_available_cardview);
        filterButton = view.findViewById(R.id.filters_button);
        mapsButton = view.findViewById(R.id.maps_button);

        views = new HashMap<>();
        views.put(ServicesView.LIST, recyclerView);
        views.put(ServicesView.NO_SERVICES, noServicesOffered);
        views.put(ServicesView.BACKEND_ERROR, noServicesAvailable);

        filterButton.setOnClickListener(this);
        mapsButton.setOnClickListener(this);
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
         } else if (currentView == ServicesView.MAP) {
             mapsButton.setVisibility(View.GONE);
         } else {
             mapsButton.setVisibility(View.GONE);
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
        }
    }
}