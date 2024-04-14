package com.fiubyte.bafix.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.fiubyte.bafix.R;
import com.google.android.material.card.MaterialCardView;

public class FiltersFragment extends Fragment {

    MaterialCardView availableNowButton;
    MaterialCardView applyButton;
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

        availableNowButton = view.findViewById(R.id.available_now_button);
        applyButton = view.findViewById(R.id.apply_button);

        availableNowButton.setOnClickListener(v -> {
            handleAvailableButtonClicked();
        });

        applyButton.setOnClickListener(v -> {
            handleApplyButtonClicked(v);
        });
    }

    private void handleAvailableButtonClicked() {
        availableNowButton.setStrokeColor(getResources().getColor(R.color.selected_color));
        ((TextView)availableNowButton.getChildAt(0)).setTextColor(getResources().getColor(R.color.selected_color));
    }

    private void handleApplyButtonClicked(View view) {
        Navigation
                .findNavController(view)
                .navigate(R.id.action_filtersFragment_to_serviceFinderFragment);
    }
}