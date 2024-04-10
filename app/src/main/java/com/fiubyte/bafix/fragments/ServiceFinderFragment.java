package com.fiubyte.bafix.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fiubyte.bafix.R;
import com.fiubyte.bafix.adapters.ServiceFinderListAdapter;
import com.fiubyte.bafix.entities.ProviderData;

import java.util.ArrayList;
import java.util.Arrays;

public class ServiceFinderFragment extends Fragment {

    private final ArrayList<ProviderData> providers = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupProviders();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_service_finder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.serviceFinderRecyclerView);
        ServiceFinderListAdapter adapter = new ServiceFinderListAdapter(this.getContext(), providers);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(),
                LinearLayoutManager.VERTICAL, false));
    }

    private void setupProviders() {
        providers.add(
                new ProviderData("Julia Benavidez",
                                0.8,
                                new ArrayList<>(Arrays.asList("Pedicuria/Manicuria", "Peluqueria")),
                                R.drawable.julia));

        providers.add(
                new ProviderData("Mario Delgado",
                        2.5,
                        new ArrayList<>(Arrays.asList("Plomeria", "Pintureria", "Albanileria", "Carpinteria", "Gasista", "Mecanico")),
                        R.drawable.mario));

        providers.add(
                new ProviderData("Augusto Molina",
                        11,
                        new ArrayList<>(Arrays.asList("Abogados")),
                        R.drawable.augusto));

        providers.add(
                new ProviderData("Nadia Perez",
                        1,
                        new ArrayList<>(Arrays.asList("Electricista", "Cerrajeria", "Reparacion de electrodomesticos")),
                        R.drawable.nadia));

        providers.add(
                new ProviderData("Carla Pasaft",
                        2.5,
                        new ArrayList<>(Arrays.asList("Electricista", "Cerrajeria", "Reparacion de electrodomesticos")),
                        R.drawable.carla));
    }
}