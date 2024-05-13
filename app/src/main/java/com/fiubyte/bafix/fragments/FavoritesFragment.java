package com.fiubyte.bafix.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fiubyte.bafix.R;
import com.fiubyte.bafix.adapters.ServiceFinderListAdapter;
import com.fiubyte.bafix.entities.ServiceData;
import com.fiubyte.bafix.entities.ServiceTab;
import com.fiubyte.bafix.models.DataViewModel;
import com.fiubyte.bafix.preferences.SharedPreferencesManager;
import com.fiubyte.bafix.utils.RecylcerViewInterface;
import com.fiubyte.bafix.utils.ServicesAPIManager;
import com.fiubyte.bafix.utils.ServicesDataDeserializer;

import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class FavoritesFragment extends Fragment implements RecylcerViewInterface,
                                                           Observer<ArrayList<ServiceData>> {
    private TextView noFavoritesText;
    private RecyclerView recyclerView;
    private DataViewModel dataViewModel;
    private ServiceFinderListAdapter adapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
                            ) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dataViewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);

        noFavoritesText = view.findViewById(R.id.no_favorites_text);
        recyclerView = view.findViewById(R.id.favoriteServicesRecylcerView);
        initializeListView();

        try {
            retrieveServices(
                    SharedPreferencesManager.getStoredToken(requireActivity()),
                    dataViewModel.getCurrentLocation().getValue());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        dataViewModel.getFavoriteServices().observe(requireActivity(), this);
    }

    private void initializeListView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(),
                                                              LinearLayoutManager.VERTICAL, false
        ));
        adapter = new ServiceFinderListAdapter(
                requireContext(),
                getFavoriteServicesList(dataViewModel.getCurrentServices().getValue()), this
        );
        recyclerView.setAdapter(adapter);

        updateCurrentView(getFavoriteServicesList(dataViewModel.getCurrentServices().getValue()));
    }

    private ArrayList<ServiceData> getFavoriteServicesList(ArrayList<ServiceData> services) {
        return services.stream()
                .filter(ServiceData::isServiceFaved)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void updateCurrentView(ArrayList<ServiceData> favoriteServices) {
        if (favoriteServices.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            noFavoritesText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noFavoritesText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(int position) {
        FavoritesFragmentDirections.ActionFavoritesFragmentToServiceFragment action =
                FavoritesFragmentDirections.actionFavoritesFragmentToServiceFragment(0, dataViewModel.getFavoriteServices().getValue().get(position), ServiceTab.INFORMATION);
        Navigation
                .findNavController(requireView())
                .navigate(action);
    }

    private void retrieveServices(String token, Map<String, Double> userLocation) throws UnsupportedEncodingException {
        Map<String,String> emptyFilters = new HashMap<>();
        emptyFilters.put("distance", "99999999");
        emptyFilters.put("filterByAvailability", "false");

        ServicesAPIManager.retrieveServices(token, emptyFilters, userLocation,
                                            new ServicesAPIManager.ServicesListCallback() {
                                                @Override
                                                public void onServicesListReceived(String servicesList) {
                                                    getActivity().runOnUiThread(() -> {
                                                        try {
                                                            dataViewModel.updateFavoriteServices(
                                                                    getFavoriteServicesList(
                                                                            ServicesDataDeserializer.deserialize(servicesList)));
                                                        } catch (JSONException e) {
                                                            throw new RuntimeException(e);
                                                        } catch (IOException e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onError() {
                                                    dataViewModel.isBackendDown().postValue(true);
                                                }
                                            });
    }

    @Override
    public void onChanged(ArrayList<ServiceData> favoriteServices) {
        adapter.updateList(favoriteServices);
        updateCurrentView(favoriteServices);
    }
}