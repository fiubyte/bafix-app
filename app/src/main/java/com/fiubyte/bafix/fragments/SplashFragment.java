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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.airbnb.lottie.LottieAnimationView;
import com.fiubyte.bafix.R;
import com.fiubyte.bafix.models.DataViewModel;
import com.fiubyte.bafix.preferences.SharedPreferencesManager;
import com.fiubyte.bafix.utils.LoginAuthManager;
import com.fiubyte.bafix.utils.ServicesDataDeserializer;
import com.fiubyte.bafix.utils.ServicesListManager;

import org.json.JSONException;

import java.io.IOException;
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
        dataViewModel.getCurrentServices().observe(
                getViewLifecycleOwner(),
                serviceData -> {
                    waitForAnimationToEndToContinue();
                });

        if (LoginAuthManager.userAlreadySignedIn(requireActivity())) {
            Log.d("DEBUGGING", "User already logged with " +
                    LoginAuthManager.getUserLastSignedInEmail(requireActivity()) + ", " +
                    "continuing");
            Log.d(
                    "DEBUGGING",
                    "token: " + SharedPreferencesManager.getStoredToken(requireActivity())
                 );
            retrieveServices(
                    SharedPreferencesManager.getStoredToken(requireActivity()),
                    dataViewModel.getCurrentLocation().getValue()
                            );
            return;
        } else {
            waitForAnimationToEndToContinue();
        }
    }

    private void waitForAnimationToEndToContinue() {
        logoAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                navController.navigate(R.id.action_splashFragment_to_registerFragment);
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
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
         }
        );
    }
}