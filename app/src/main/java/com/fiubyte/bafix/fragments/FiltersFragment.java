package com.fiubyte.bafix.fragments;

import android.os.Bundle;
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
import com.google.android.material.card.MaterialCardView;

public class FiltersFragment extends Fragment {

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
        filtersViewModel.getAvailabilityFilter().setValue(availableNowButtonSelected);
        Navigation
                .findNavController(view)
                .navigate(R.id.action_filtersFragment_to_serviceFinderFragment);
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
}