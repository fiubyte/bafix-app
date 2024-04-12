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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.widget.Toast;

import com.fiubyte.bafix.R;
import com.fiubyte.bafix.entities.ServiceData;
import com.fiubyte.bafix.models.DataViewModel;
import com.fiubyte.bafix.utils.LoginAuthManager;
import com.fiubyte.bafix.preferences.SharedPreferencesManager;
import com.fiubyte.bafix.utils.ServicesDataDeserializer;
import com.fiubyte.bafix.utils.ServicesListManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class RegisterFragment extends Fragment implements Observer<ArrayList<ServiceData>>, SharedPreferences.OnSharedPreferenceChangeListener {

    private DataViewModel dataViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        }

        dataViewModel = new ViewModelProvider(getActivity()).get(DataViewModel.class);
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

        SharedPreferencesManager.registerOnChangedListerner(this);

        dataViewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);
        dataViewModel.getCurrentServices().observe(requireActivity(), this);

        ImageView googleSignInButton = view.findViewById(R.id.google_sign_in_button);
        googleSignInButton.setOnClickListener(v -> {
            LoginAuthManager.signInWithGoogle(this);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LoginAuthManager.RC_SIGN_IN) {
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (signInAccountTask.isSuccessful()) {
                Log.d("DEBUGGING", "Google SignIn was successful");
                LoginAuthManager.handleSuccessFullGoogleSignIn(signInAccountTask, requireActivity());
                displaySuccessFulLoginToast();
            }
        }
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

    private void displaySuccessFulLoginToast() {
        Toast.makeText(requireActivity(), "Successfully logged in", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChanged(ArrayList<ServiceData> serviceData) {
        Navigation.findNavController(requireView()).navigate(R.id.action_registerFragment_to_serviceFinderFragment);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String s) {
        retrieveServices(SharedPreferencesManager.getStoredToken(requireActivity()), dataViewModel.getCurrentLocation().getValue());
    }
}