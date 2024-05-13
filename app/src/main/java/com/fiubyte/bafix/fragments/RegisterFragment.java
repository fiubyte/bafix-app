package com.fiubyte.bafix.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;

import com.fiubyte.bafix.R;
import com.fiubyte.bafix.entities.ServiceData;
import com.fiubyte.bafix.entities.ServiceTab;
import com.fiubyte.bafix.models.DataViewModel;
import com.fiubyte.bafix.utils.LoginAuthManager;
import com.fiubyte.bafix.preferences.SharedPreferencesManager;
import com.fiubyte.bafix.utils.ServicesDataDeserializer;
import com.fiubyte.bafix.utils.ServicesAPIManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private DataViewModel dataViewModel;
    private final Integer NO_REDIRECT_SERVICE_ID = -1;
    private Integer redirectServiceId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
                            ) {

        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String serviceId = RegisterFragmentArgs.fromBundle(getArguments()).getServiceId();
        String token = SharedPreferencesManager.getStoredToken(requireActivity());
        if (!NO_REDIRECT_SERVICE_ID.toString().equals(serviceId)) {
            // Is a redirect from an unauthenticated user
            redirectServiceId = Integer.valueOf(serviceId);
        }

        if (!token.isEmpty() && !NO_REDIRECT_SERVICE_ID.toString().equals(serviceId)) {
            // Is a redirect from an authenticated user
            Map<String, String> emptyFilters = new HashMap<>();
            emptyFilters.put("distance", "99999999");
            emptyFilters.put("filterByAvailability", Boolean.FALSE.toString());
            try {
                ServicesAPIManager.retrieveServices(token, emptyFilters, defaultLocation(),
                        new ServicesAPIManager.ServicesListCallback() {
                            @Override
                            public void onServicesListReceived(String servicesList) {
                                getActivity().runOnUiThread(() -> {
                                    try {
                                        dataViewModel.updateServices(ServicesDataDeserializer.deserialize(servicesList));

                                        if (redirectServiceId != null && !redirectServiceId.equals(NO_REDIRECT_SERVICE_ID)) {
                                            Bundle bundle = new Bundle();
                                            ServiceData serviceData = dataViewModel.getCurrentServices().getValue().stream().filter(s -> s.getServiceId() == redirectServiceId).findFirst().orElseThrow(() -> new RuntimeException("Redirect service id: " + redirectServiceId + " not found in the list of services"));
                                            bundle.putInt("rating", (serviceData.getOwnRating() != null) ? serviceData.getOwnRating() : 0);
                                            bundle.putParcelable("serviceData", serviceData);
                                            bundle.putParcelable("currentTab", ServiceTab.INFORMATION);
                                            redirectServiceId = null;
                                            Navigation.findNavController(requireView()).navigate(R.id.action_registerFragment_to_serviceFragment, bundle);
                                        }
                                    } catch (JSONException | IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                            }

                            @Override
                            public void onError() {
                                Log.d("BACK", "caido register");
                                dataViewModel.isBackendDown().postValue(true);

                                requireActivity().runOnUiThread(() -> Navigation.findNavController(requireView()).navigate(R.id.action_registerFragment_to_serviceFinderFragment));
                            }
                        });
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        SharedPreferencesManager.registerOnChangedListerner(this);

        dataViewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);

        ImageView googleSignInButton = view.findViewById(R.id.google_sign_in_button);
        googleSignInButton.setOnClickListener(v -> {
            LoginAuthManager.signInWithGoogle(this);
        });
    }

    private static Map<String, Double> defaultLocation() {
        Map<String, Double> currentLocation = new HashMap<>();
        currentLocation.put("latitude", -34.603722);
        currentLocation.put("longitude", -58.381592);
        return currentLocation;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LoginAuthManager.RC_SIGN_IN) {
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (signInAccountTask.isSuccessful()) {
                Log.d("DEBUGGING", "Google SignIn was successful");
                LoginAuthManager.handleSuccessFullGoogleSignIn(signInAccountTask, requireActivity(), requireView());
            } else {
                LoginAuthManager.displayAuthenticationFailedMessage(requireActivity(), requireView());
                Log.d("DEBUGGING", "not success");
            }
        }
    }

    private void retrieveServices(String token, Map<String, Double> userLocation, Boolean filterByAvailability) throws UnsupportedEncodingException {
        Map<String,String> emptyFilters = new HashMap<>();
        emptyFilters.put("distance", "99999999");
        emptyFilters.put("filterByAvailability", filterByAvailability.toString());

        ServicesAPIManager.retrieveServices(token, emptyFilters, userLocation,
                                            new ServicesAPIManager.ServicesListCallback() {
                                                 @Override
                                                 public void onServicesListReceived(String servicesList) {
                                                     getActivity().runOnUiThread(() -> {
                                                         try {
                                                             dataViewModel.updateServices(ServicesDataDeserializer.deserialize(servicesList));

                                                             if (redirectServiceId != null && !redirectServiceId.equals(NO_REDIRECT_SERVICE_ID)) {
                                                                 Bundle bundle = new Bundle();
                                                                 ServiceData serviceData = dataViewModel.getCurrentServices().getValue().stream().filter(s -> s.getServiceId() == redirectServiceId).findFirst().orElseThrow(() -> new RuntimeException("Redirect service id: " + redirectServiceId + " not found in the list of services"));
                                                                 bundle.putInt("rating", (serviceData.getOwnRating() != null) ? serviceData.getOwnRating() : 0);
                                                                 bundle.putParcelable("serviceData", serviceData);
                                                                 bundle.putParcelable("currentTab", ServiceTab.INFORMATION);
                                                                 redirectServiceId = null;
                                                                 Navigation.findNavController(requireView()).navigate(R.id.action_registerFragment_to_serviceFragment, bundle);
                                                             } else {
                                                                 Navigation.findNavController(requireView()).navigate(R.id.action_registerFragment_to_serviceFinderFragment);
                                                             }
                                                         } catch (JSONException | IOException e) {
                                                             throw new RuntimeException(e);
                                                         }
                                                     });
                                                 }

                                                 @Override
                                                 public void onError() {
                                                     Log.d("BACK", "caido register");
                                                     dataViewModel.isBackendDown().postValue(true);

                                                     requireActivity().runOnUiThread(() -> Navigation.findNavController(requireView()).navigate(R.id.action_registerFragment_to_serviceFinderFragment));
                                                 }
                                             });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String s) {
        try {
            if (!SharedPreferencesManager.getStoredToken(requireActivity()).isEmpty()) {
                Boolean filterByAvailability = Boolean.TRUE;
                if (redirectServiceId != null && !redirectServiceId.equals(NO_REDIRECT_SERVICE_ID)) {
                    // For the external intent we need to gather all the services
                    filterByAvailability = Boolean.FALSE;
                }
                retrieveServices(SharedPreferencesManager.getStoredToken(requireActivity()),
                        dataViewModel.getCurrentLocation().getValue(), filterByAvailability);
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}