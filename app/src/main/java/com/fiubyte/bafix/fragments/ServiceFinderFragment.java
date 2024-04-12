package com.fiubyte.bafix.fragments;

import android.os.Bundle;
import android.util.Log;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ServiceFinderFragment extends Fragment {

    private final ArrayList<ProviderData> providers = new ArrayList<>();
    private OkHttpClient client;
    private String getServicesURL = "https://bafix-api.onrender.com/service-categories/";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = new OkHttpClient();

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
        Request request = new Request.Builder()
                                .url(getServicesURL)
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3MTI4OTkxNDgsImlhdCI6MTcxMjg3MDM0OCwic3ViIjoiYWRtaW5AZXhhbXBsZS5jb20iLCJyb2xlcyI6IkFETUlOIn0.AEgqdirUrtgCIrAX1N2VxdyCM2pOOYfzpCoLmJ2WfKc")
                                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("DEBUGGING", e.toString());
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("DEBUGGING", response.body().string());
            }
        });


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
                        new ArrayList<>(Arrays.asList("Decoracion de interiores")),
                        R.drawable.carla));
    }
}