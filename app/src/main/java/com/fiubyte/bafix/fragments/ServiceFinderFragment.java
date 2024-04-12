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

        providers.add(
                new ServiceData(R.drawable.julia,
                            "Pedicura completa con esmaltado permanente",
                                0.8,
                                "Julia Benavidez"));

        providers.add(
                new ServiceData(R.drawable.mario,
                        "Reparación de cañerías de agua caliente",
                        2.5,
                        "Mario Delgado"));
    }
}