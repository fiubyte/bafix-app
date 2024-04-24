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
import com.fiubyte.bafix.models.FiltersViewModel;

public class ServiceFinderFragment extends Fragment implements View.OnClickListener {

    private DataViewModel dataViewModel;
    private FiltersViewModel filtersViewModel;
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

        recyclerView = view.findViewById(R.id.serviceFinderRecyclerView);
        dataViewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);
        filtersViewModel = new ViewModelProvider(requireActivity()).get(FiltersViewModel.class);
        noServicesOffered = view.findViewById(R.id.no_services_offered);
        noServicesAvailable = view.findViewById(R.id.services_not_available_cardview);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(),
                                                              LinearLayoutManager.VERTICAL, false
        ));
        Log.d("DEBUGGING", "setiando lista");
        ServiceFinderListAdapter adapter = new ServiceFinderListAdapter(
                requireContext(),
                dataViewModel.getCurrentServices().getValue()
        );
        recyclerView.setAdapter(adapter);

        if(dataViewModel.getCurrentServices().getValue().isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            noServicesOffered.setVisibility(View.VISIBLE);
            noServicesAvailable.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noServicesOffered.setVisibility(View.GONE);
            noServicesAvailable.setVisibility(View.GONE);
        }

        if(dataViewModel.isBackendDown().getValue()) {
            recyclerView.setVisibility(View.GONE);
            noServicesOffered.setVisibility(View.GONE);
            noServicesAvailable.setVisibility(View.VISIBLE);
        } else {
            noServicesAvailable.setVisibility(View.GONE);
        }

        filterButton = view.findViewById(R.id.filters_button);
        filterButton.setOnClickListener(this);

        mapsButton = view.findViewById(R.id.maps_button);
        mapsButton.setOnClickListener(this);
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
                Log.d("DEBUGGING", "maps");
                break;
        }
    }
}