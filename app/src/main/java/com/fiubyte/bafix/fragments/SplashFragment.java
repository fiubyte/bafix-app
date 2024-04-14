package com.fiubyte.bafix.fragments;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.fiubyte.bafix.R;
import com.fiubyte.bafix.entities.ServiceData;
import com.fiubyte.bafix.models.DataViewModel;
import com.fiubyte.bafix.utils.LoginAuthManager;
import com.fiubyte.bafix.utils.ServicesDataDeserializer;
import com.fiubyte.bafix.utils.ServicesListManager;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class SplashFragment extends Fragment implements Observer<ArrayList<ServiceData>> {
    private DataViewModel dataViewModel;
    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
                            ) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        dataViewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);
        dataViewModel.getCurrentServices().observe(getViewLifecycleOwner(), this);

        setupBaFixAPI();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    private void setupBaFixAPI() {
        LoginAuthManager.loginToBaFixAPI(new LoginAuthManager.TokenCallback() {
            @Override
            public void onTokenReceived(String token) {
                Log.d("SERVICES", "Token received: " + token);
                retrieveServices(token, dataViewModel.getCurrentLocation().getValue());
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void retrieveServices(String token, Map<String, Double> userLocation) {
        ServicesListManager.retrieveServices(token, userLocation,
                                             new ServicesListManager.ServicesListCallback() {
            @Override
            public void onServicesListReceived(String servicesList) {
                getActivity().runOnUiThread(() -> {
                    try {
                        dataViewModel.updateServices(ServicesDataDeserializer.deserialize(servicesList));
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
        });
    }

    @Override
    public void onChanged(ArrayList<ServiceData> serviceData) {
        navController.navigate(R.id.action_splashFragment_to_registerFragment);
    }
}