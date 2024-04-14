package com.fiubyte.bafix.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.fiubyte.bafix.R;
import com.fiubyte.bafix.models.DataViewModel;
import com.fiubyte.bafix.models.FiltersViewModel;
import com.fiubyte.bafix.preferences.SharedPreferencesManager;
import com.fiubyte.bafix.utils.ServicesDataDeserializer;
import com.fiubyte.bafix.utils.ServicesListManager;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONException;

import java.io.IOException;
import java.util.Map;

public class FiltersFragment extends Fragment {

    DataViewModel dataViewModel;
    FiltersViewModel filtersViewModel;
    MaterialCardView availableNowButton;
    MaterialCardView applyButton;

    boolean availableNowButtonSelected = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
                            ) {
        return inflater.inflate(R.layout.fragment_filters, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dataViewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);

        filtersViewModel = new ViewModelProvider(requireActivity()).get(FiltersViewModel.class);

        availableNowButton = view.findViewById(R.id.available_now_button);
        applyButton = view.findViewById(R.id.apply_button);

        updateAvailableButton();

        availableNowButton.setOnClickListener(v -> {
            handleAvailableButtonClicked();
        });

        applyButton.setOnClickListener(v -> {
            handleApplyButtonClicked(v);
        });
    }

    private void updateAvailableButton() {
        if(filtersViewModel.getAvailabilityFilter().getValue() == false){
            deselectButton();
        } else {
            selectButton();
        }
    }

    private void handleAvailableButtonClicked() {
        if(availableNowButtonSelected == false){
            selectButton();
        } else {
            deselectButton();
        }
    }

    private void handleApplyButtonClicked(View view) {
        Log.d("DEBUGGING", "filter: " + availableNowButtonSelected);
        filtersViewModel.getAvailabilityFilter().setValue(availableNowButtonSelected);

        retrieveServices(
                SharedPreferencesManager.getStoredToken(requireActivity()),
                availableNowButtonSelected,
                dataViewModel.getCurrentLocation().getValue());
    }

    private void deselectButton() {
        availableNowButton.setStrokeColor(getResources().getColor(R.color.stoke_color));
        ((TextView)availableNowButton.getChildAt(0)).setTextColor(getResources().getColor(R.color.title_color));
        availableNowButtonSelected = false;
    }

    private void selectButton() {
        availableNowButton.setStrokeColor(getResources().getColor(R.color.selected_color));
        ((TextView)availableNowButton.getChildAt(0)).setTextColor(getResources().getColor(R.color.selected_color));
        availableNowButtonSelected = true;
    }

    private void retrieveServices(String token, boolean orderByAvailability, Map<String, Double> userLocation) {
        ServicesListManager.retrieveServices(token, orderByAvailability, userLocation,
         new ServicesListManager.ServicesListCallback() {
             @Override
             public void onServicesListReceived(String servicesList) {
                 getActivity().runOnUiThread(() -> {
                     try {
                         dataViewModel.updateServices(ServicesDataDeserializer.deserialize(servicesList));
                         Navigation
                                 .findNavController(requireView())
                                 .navigate(R.id.action_filtersFragment_to_serviceFinderFragment);
                     } catch (JSONException e) {
                         throw new RuntimeException(e);
                     } catch (IOException e) {
                         throw new RuntimeException(e);
                     }
                 });
             }

             @Override
             public void onError(Exception e) {
                 e.printStackTrace();
             }
         }
        );
    }
}