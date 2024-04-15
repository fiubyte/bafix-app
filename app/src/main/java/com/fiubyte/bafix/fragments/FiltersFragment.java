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
import com.google.android.material.slider.Slider;

import org.json.JSONException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class FiltersFragment extends Fragment {

    DataViewModel dataViewModel;
    FiltersViewModel filtersViewModel;
    MaterialCardView availableNowButton, applyButton;
    Slider distanceSlider;
    TextView distanceText;
    boolean availableNowButtonSelected = false;
    String maxDistance = "";

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

        distanceSlider = view.findViewById(R.id.distance_slider);
        distanceText = view.findViewById(R.id.distance_text);

        updateAvailableButton();
        updateMaxDistanceSlider();

        availableNowButton.setOnClickListener(v -> {
            handleAvailableButtonClicked();
        });

        applyButton.setOnClickListener(v -> {
            handleApplyButtonClicked(v);
        });

        distanceSlider.setLabelFormatter(value -> {
            DecimalFormat twoDForm = new DecimalFormat("#.#");
            return twoDForm.format(value);
        });
        distanceSlider.addOnChangeListener((slider, value, fromUser) -> {
            DecimalFormat twoDForm = new DecimalFormat("#.#");
            maxDistance = twoDForm.format(value);
            distanceText.setText(maxDistance + " km");
        });
    }

    private void updateAvailableButton() {
        if (filtersViewModel.getFilters().getValue().get("filterByAvailability") == "false") {
            Log.d("BUTTON", "deselected");
            deselectButton();
        } else {
            selectButton();
        }
    }

    private void updateMaxDistanceSlider() {
        maxDistance = filtersViewModel.getFilters().getValue().get("distance");
        distanceSlider.setValue(Float.valueOf(filtersViewModel.getFilters().getValue().get(
                "distance")));
        distanceText.setText(filtersViewModel.getFilters().getValue().get("distance") + " km");
    }

    private void handleAvailableButtonClicked() {
        if (availableNowButtonSelected == false) {
            selectButton();
        } else {
            deselectButton();
        }
    }

    private void handleApplyButtonClicked(View view) {
        Map<String, String> filters = new HashMap<>();
        filters.put("filterByAvailability", String.valueOf(availableNowButtonSelected));
        filters.put("distance", maxDistance);

        filtersViewModel.getFilters().setValue(filters);
        Log.d("BUTTON", "Updated filters: " + filters);

        retrieveServices(SharedPreferencesManager.getStoredToken(requireActivity()), filters,
                         dataViewModel.getCurrentLocation().getValue());
    }

    private void deselectButton() {
        availableNowButton.setStrokeColor(getResources().getColor(R.color.stoke_color));
        ((TextView) availableNowButton.getChildAt(0)).setTextColor(getResources().getColor(R.color.title_color));
        availableNowButtonSelected = false;
    }

    private void selectButton() {
        availableNowButton.setStrokeColor(getResources().getColor(R.color.selected_color));
        ((TextView) availableNowButton.getChildAt(0)).setTextColor(getResources().getColor(R.color.selected_color));
        availableNowButtonSelected = true;
    }

    private void retrieveServices(String token, Map<String, String> filters,
                                  Map<String, Double> userLocation) {
        ServicesListManager.retrieveServices(token, filters, userLocation,
                                             new ServicesListManager.ServicesListCallback() {
                                                 @Override
                                                 public void onServicesListReceived(String servicesList) {
                                                     getActivity().runOnUiThread(() -> {
                                                         try {
                                                             dataViewModel.updateServices(ServicesDataDeserializer.deserialize(servicesList));
                                                             Navigation.findNavController(requireView()).navigate(R.id.action_filtersFragment_to_serviceFinderFragment);
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