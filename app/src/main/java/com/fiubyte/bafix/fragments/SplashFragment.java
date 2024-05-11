package com.fiubyte.bafix.fragments;

import android.animation.Animator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.airbnb.lottie.LottieAnimationView;
import com.fiubyte.bafix.R;
import com.fiubyte.bafix.models.DataViewModel;
import com.fiubyte.bafix.preferences.SharedPreferencesManager;
import com.fiubyte.bafix.utils.LoginAuthManager;
import com.fiubyte.bafix.utils.ServicesDataDeserializer;
import com.fiubyte.bafix.utils.ServicesAPIManager;

import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class SplashFragment extends Fragment {
    private DataViewModel dataViewModel;
    private NavController navController;
    private LottieAnimationView logoAnimationView;

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

        logoAnimationView = view.findViewById(R.id.bafixLogo);
        navController = Navigation.findNavController(view);

        dataViewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);

        if (LoginAuthManager.userAlreadySignedIn(requireActivity())) {
            Log.d("DEBUGGING", "User already logged with " +
                    LoginAuthManager.getUserLastSignedInEmail(requireActivity()) + ", " +
                    "continuing");
            Log.d(
                    "DEBUGGING",
                    "token: " + SharedPreferencesManager.getStoredToken(requireActivity())
                 );
            try {
                retrieveServices(
                        SharedPreferencesManager.getStoredToken(requireActivity()),
                        dataViewModel.getCurrentLocation().getValue());
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            return;
        } else {
            waitForAnimationToEndToContinue(R.id.action_splashFragment_to_registerFragment);
        }
    }

    private void waitForAnimationToEndToContinue(int destination) {
        logoAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                navController.navigate(destination);
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        }
    }

    private void retrieveServices(String token, Map<String, Double> userLocation) throws UnsupportedEncodingException {
        Map<String,String> emptyFilters = new HashMap<>();
        emptyFilters.put("distance", "99999999");
        emptyFilters.put("filterByAvailability", "true");

        ServicesAPIManager.retrieveServices(token, emptyFilters, userLocation,
                                            new ServicesAPIManager.ServicesListCallback() {
                                                 @Override
                                                 public void onServicesListReceived(String servicesList) {
                                                     getActivity().runOnUiThread(() -> {
                                                         try {
                                                             dataViewModel.updateServices(ServicesDataDeserializer.deserialize(servicesList));
                                                             waitForAnimationToEndToContinue(R.id.action_splashFragment_to_serviceFinderFragment);
                                                         } catch (JSONException e) {
                                                             throw new RuntimeException(e);
                                                         } catch (IOException e) {
                                                             throw new RuntimeException(e);
                                                         }
                                                     });
                                                 }

                                                 @Override
                                                 public void onError() {
                                                     Log.d("BACK", "caido splash");
                                                     dataViewModel.isBackendDown().postValue(true);
                                                     waitForAnimationToEndToContinue(R.id.action_splashFragment_to_serviceFinderFragment);
                                                 }
                                             });
    }
}