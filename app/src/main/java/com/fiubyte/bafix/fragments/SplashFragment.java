package com.fiubyte.bafix.fragments;

import android.animation.Animator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.fiubyte.bafix.R;
import com.fiubyte.bafix.models.DataViewModel;
import com.fiubyte.bafix.utils.LoginAuthManager;
import com.fiubyte.bafix.utils.ServicesListManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SplashFragment extends Fragment {
    private DataViewModel dataViewModel;
    private static OkHttpClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dataViewModel = new ViewModelProvider(this).get(DataViewModel.class);

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
                Log.d("DEBUGGING", "Token received: " + token);
                retrieveServices(token);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void retrieveServices(String token) {
        ServicesListManager.retrieveServices(token, new ServicesListManager.ServicesListCallback() {
            @Override
            public void onServicesListReceived(String servicesList) {
                Log.d("DEBUGGING", "Services list received: " + servicesList);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}