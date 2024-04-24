package com.fiubyte.bafix.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class FiltersFragment extends Fragment {

    TextView cancelButton;
    TextView clearFiltersButton;
    DataViewModel dataViewModel;
    FiltersViewModel filtersViewModel;
    MaterialCardView availableNowButton, applyButton;
    Slider distanceSlider;
    TextView distanceText;
    boolean availableNowButtonSelected = true;
    String maxDistance = "";

    LinearLayout categoriesLayout;
    ArrayList<Integer> selectedCategories = new ArrayList<>();

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
        categoriesLayout = view.findViewById(R.id.categories_layout);

        cancelButton = view.findViewById(R.id.cancel_button);
        clearFiltersButton = view.findViewById(R.id.clean_button);

        updateAvailableButton();
        updateMaxDistanceSlider();
        updateCategoryButtons();

        availableNowButton.setOnClickListener(v -> handleAvailableButtonClicked());

        applyButton.setOnClickListener(v -> {
            try {
                handleApplyButtonClicked(v);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
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
        setCategoriesListeners();

        cancelButton.setOnClickListener(v-> {
            clearFilters();
            Navigation.findNavController(requireView()).navigate(R.id.action_filtersFragment_to_serviceFinderFragment);
        });

        clearFiltersButton.setOnClickListener(v-> {
            clearFilters();
        });
    }

    private void clearFilters() {
        Map<String,String> emptyFilters = new HashMap<>();
        emptyFilters.put("distance", "15");

        filtersViewModel.getFilters().setValue(emptyFilters);
    }

    private void updateCategoryButtons() {
        Log.d("CATEGORY", filtersViewModel.getFilters().getValue().toString());
        String categories = filtersViewModel.getFilters().getValue().get("categories");

        if (categories == "" || categories ==  null) {
            selectedCategories = new ArrayList<>();
            for (int i = 0; i < categoriesLayout.getChildCount(); i++) {
                LinearLayout rowLayout = (LinearLayout) categoriesLayout.getChildAt(i);

                for(int j = 0; j < rowLayout.getChildCount(); j++) {
                    int finalJ = j;
                    int finalI = i;
                    unselectCategoryButton(finalI, finalJ);
                }
            }
            return;
        }
        selectedCategories = (ArrayList<Integer>) Arrays.stream(categories.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        for (int i = 0; i < categoriesLayout.getChildCount(); i++) {
            LinearLayout rowLayout = (LinearLayout) categoriesLayout.getChildAt(i);

            for(int j = 0; j < rowLayout.getChildCount(); j++) {
                MaterialCardView categoryCard = (MaterialCardView) rowLayout.getChildAt(j);

                int finalJ = j;
                int finalI = i;
                String categoryName = ((TextView)categoryCard.getChildAt(0)).getText().toString();
                if(selectedCategories.contains(getIdFromCategoryName(categoryName))) {
                    selectCategoryButton(finalI, finalJ);
                } else {
                    unselectCategoryButton(finalI, finalJ);
                }
            }
        }

        Log.d("CATEGORY", "updated: " + selectedCategories.toString());
    }

    private void setCategoriesListeners() {
        for (int i = 0; i < categoriesLayout.getChildCount(); i++) {
            LinearLayout rowLayout = (LinearLayout) categoriesLayout.getChildAt(i);

            for(int j = 0; j < rowLayout.getChildCount(); j++) {
                MaterialCardView categoryCard = (MaterialCardView) rowLayout.getChildAt(j);

                int finalJ = j;
                int finalI = i;
                categoryCard.setOnClickListener(view -> {
                    String categoryName = ((TextView)categoryCard.getChildAt(0)).getText().toString();
                    Log.d("CATEGORY", categoryName);
                    updateCategoryButton(getIdFromCategoryName(categoryName), finalI, finalJ);
                });
            }
        }
    }

    private void updateCategoryButton(int categoryId, int i, int j) {
        Log.d("CATEGORY", "" + categoryId);
        Log.d("CATEGORY", selectedCategories.toString());
        if(!selectedCategories.contains(categoryId)) {
            selectCategoryButton(i, j);
        } else {
            unselectCategoryButton(i,j);
        }
    }

    // FIXME: Refactor code and fix bug issue when unselecting
    private void unselectCategoryButton(int i, int j) {
        LinearLayout rowLayout = (LinearLayout) categoriesLayout.getChildAt(i);
        MaterialCardView categoryCard = (MaterialCardView) rowLayout.getChildAt(j);
        categoryCard.setStrokeColor(getResources().getColor(R.color.stoke_color));
        ((TextView)categoryCard.getChildAt(0)).setTextColor(getResources().getColor(R.color.title_color));

        int category_id = getIdFromCategoryName(((TextView)categoryCard.getChildAt(0)).getText().toString());
        for (int k=0;k<selectedCategories.size();k++) {
            if (selectedCategories.get(k) == category_id) {
                selectedCategories.remove(k);
            }
        }
        Log.d("CATEGORY", "unselect: " + selectedCategories);
    }

    private void selectCategoryButton(int i, int j) {
        LinearLayout rowLayout = (LinearLayout) categoriesLayout.getChildAt(i);
        MaterialCardView categoryCard = (MaterialCardView) rowLayout.getChildAt(j);
        categoryCard.setStrokeColor(getResources().getColor(R.color.selected_color));
        ((TextView)categoryCard.getChildAt(0)).setTextColor(getResources().getColor(R.color.selected_color));

        int category_id = getIdFromCategoryName(((TextView)categoryCard.getChildAt(0)).getText().toString());
        if(!selectedCategories.contains(category_id)){
            selectedCategories.add(getIdFromCategoryName(((TextView)categoryCard.getChildAt(0)).getText().toString()));
        }
        Log.d("CATEGORY", "select: " + selectedCategories);
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

    private void handleApplyButtonClicked(View view) throws UnsupportedEncodingException {
        Map<String, String> filters = new HashMap<>();
        filters.put("filterByAvailability", String.valueOf(availableNowButtonSelected));
        filters.put("distance", maxDistance);

        String categories = "";
        for(int i = 0; i < selectedCategories.size(); i++) {
            categories = categories + selectedCategories.get(i);
            if(i != selectedCategories.size() - 1) {
                categories = categories + ",";
            }
        }
        filters.put("categories", categories);

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

    private int getIdFromCategoryName(String categoryName) {
        Map<String, Integer> categories = new HashMap<>();
        categories.put("Plomeria", 1);
        categories.put("Pinturería", 2);
        categories.put("Albañilería", 3);
        categories.put("Gasista", 4);
        categories.put("Carpintería", 5);
        categories.put("Mecánico", 6);
        categories.put("Electricista", 7);
        categories.put("Reparación de electrodomésticos", 8);
        categories.put("Cerrajería", 9);
        categories.put("Instalación de aires acondicionados", 10);
        categories.put("Jardinería/Paisajista", 11);
        categories.put("Decoración de interiores", 12);
        categories.put("Arquitectura", 13);
        categories.put("Pedicuría/Manicuría", 14);
        categories.put("Peluquería", 15);
        categories.put("Abogados", 16);

        return categories.get(categoryName);
    }

    private void retrieveServices(String token, Map<String, String> filters,
                                  Map<String, Double> userLocation) throws UnsupportedEncodingException {
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
                                                 public void onError() {
                                                     Log.d("BACK", "caido filters");
                                                     dataViewModel.isBackendDown().postValue(true);
                                                     requireActivity().runOnUiThread(() -> Navigation.findNavController(requireView()).navigate(R.id.action_filtersFragment_to_serviceFinderFragment));
                                                 }
                                             }
                                            );
    }
}