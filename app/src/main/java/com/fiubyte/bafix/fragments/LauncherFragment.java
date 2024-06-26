package com.fiubyte.bafix.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fiubyte.bafix.R;
import com.fiubyte.bafix.models.DataViewModel;

import java.util.Map;

public class LauncherFragment extends Fragment implements Observer<Map<String, Double>> {

    private DataViewModel dataViewModel;

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
        return inflater.inflate(R.layout.fragment_launcher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dataViewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);
        dataViewModel.getCurrentLocation().observe(requireActivity(), this);
    }

    @Override
    public void onChanged(Map<String, Double> stringDoubleMap) {
        try {
            // This is required for some reason when attempting to redirect to ServiceFragment form MainActivity on the external browser intent
            Navigation.findNavController(requireView()).navigate(R.id.action_launcherFragment_to_splashFragment);
        } catch (Exception e) {
            Log.w("WARN", "Warning on onChanged from LauncherFragment");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        }
    }
}