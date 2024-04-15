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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fiubyte.bafix.R;
import com.fiubyte.bafix.adapters.ServiceFinderListAdapter;
import com.fiubyte.bafix.models.DataViewModel;
import com.fiubyte.bafix.models.FiltersViewModel;
import com.fiubyte.bafix.preferences.SharedPreferencesManager;
import com.fiubyte.bafix.utils.ServicesDataDeserializer;
import com.fiubyte.bafix.utils.ServicesListManager;

import org.json.JSONException;

import java.io.IOException;
import java.util.Map;

public class ServiceFinderFragment extends Fragment implements View.OnClickListener {
    private DataViewModel dataViewModel;
    private FiltersViewModel filtersViewModel;
    private RecyclerView recyclerView;

    private ImageView filterButton;

    private TextView noServicesAvailable;

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
        noServicesAvailable = view.findViewById(R.id.no_services_available);

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
            noServicesAvailable.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noServicesAvailable.setVisibility(View.GONE);
        }

        filterButton = view.findViewById(R.id.filters_button);
        filterButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Navigation
                .findNavController(view)
                .navigate(R.id.action_serviceFinderFragment_to_filtersFragment);
    }
}