package com.fiubyte.bafix.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.fiubyte.bafix.R;
import com.fiubyte.bafix.preferences.SharedPreferencesManager;
import com.fiubyte.bafix.utils.LoginAuthManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;

public class RegisterFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(LoginAuthManager.userAlreadySignedIn(requireActivity())) {
            Log.d("DEBUGGING", "User already logged with " +
                    LoginAuthManager.getUserLastSignedInEmail(requireActivity()) + ", continuing");
            Log.d("DEBUGGING", "token: " + SharedPreferencesManager.getStoredToken(requireActivity()));
            Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_serviceFinderFragment);
            return;
        }

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
                Navigation.findNavController(requireView()).navigate(R.id.action_registerFragment_to_serviceFinderFragment);
            }
        }
    }

    private void displaySuccessFulLoginToast() {
        Toast.makeText(requireActivity(), "Successfully logged in", Toast.LENGTH_SHORT).show();
    }
}