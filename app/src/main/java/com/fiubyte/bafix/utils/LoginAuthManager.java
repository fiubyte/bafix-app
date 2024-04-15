package com.fiubyte.bafix.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fiubyte.bafix.R;
import com.fiubyte.bafix.fragments.RegisterFragment;
import com.fiubyte.bafix.preferences.SharedPreferencesManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginAuthManager {
    static FirebaseAuth firebaseAuth;
    public static final int RC_SIGN_IN = 100;
    private static final String serverClientId =
            "404201273327-m5d62vn42rvkupj1utg0akvqrghb0vft.apps.googleusercontent.com";
    private static GoogleSignInClient googleSignInClient;
    private static OkHttpClient okHttpClient;
    private static final String postAuthLoginURL = "https://bafix-api.onrender.com/auth/login";

    public static void signInWithGoogle(RegisterFragment registerFragment) {
        GoogleSignInOptions googleSignInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(serverClientId)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(registerFragment.requireActivity(),
                                                    googleSignInOptions);

        Intent intent = googleSignInClient.getSignInIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        registerFragment.startActivityForResult(intent, RC_SIGN_IN);
        registerFragment.requireActivity().overridePendingTransition(0, 0);
    }

    public static void handleSuccessFullGoogleSignIn(
            Task<GoogleSignInAccount> signInAccountTask
            , Activity activity, View view
                                                    ) {
        try {
            GoogleSignInAccount googleSignInAccount =
                    signInAccountTask.getResult(ApiException.class);
            if (googleSignInAccount != null) {
                Log.d("DEBUGGING", "#1");
                LoginAuthManager.authenticateWithFirebase(googleSignInAccount, activity, view);
            }
        } catch (ApiException e) {
            Log.d("DEBUGGING", "#2");

            e.printStackTrace();
        }
    }

    public static boolean userAlreadySignedIn(Activity activity) {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        return (GoogleSignIn.getLastSignedInAccount(activity) != null) && firebaseUser != null;
    }

    public static String getUserLastSignedInEmail(Activity activity) {
        if (userAlreadySignedIn(activity)) {
            return GoogleSignIn.getLastSignedInAccount(activity).getEmail();
        }
        return "admin@admin@example.com";
    }

    private static void displaySuccessFulLoginToast(Activity activity) {
        activity.runOnUiThread(() -> {
            Toast.makeText(activity, "Successfully logged in", Toast.LENGTH_SHORT).show();
        });
    }

    public static void authenticateWithFirebase(GoogleSignInAccount googleSignInAccount,
                                                Activity activity, View view) {
        AuthCredential authCredential =
                GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(authCredential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                        mUser.getIdToken(true)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {

                                        String idToken = task1.getResult().getToken();
                                        String email =
                                                (String) task1.getResult().getClaims().get("email");
                                        LoginAuthManager.setupBaFixAPI(email, idToken, activity);

                                    } else {
                                        // Handle error -> task.getException();
                                    }
                                });
                    } else {
                        displayAuthenticationFailedMessage(activity, view);
                    }
                });
    }

    public static void displayAuthenticationFailedMessage(Activity activity, View view) {
        ImageView registerFailedView = view.findViewById(R.id.register_failed);
        AnimationManager.showAndFadeOutView(activity.getApplicationContext(), registerFailedView, 3000);
    }

    public static void setupBaFixAPI(String email, String googleIdToken, Activity activity) {
        LoginAuthManager.loginToBaFixAPI(email, googleIdToken,
                                         new LoginAuthManager.TokenCallback() {
            @Override
            public void onTokenReceived(String token) {
                SharedPreferencesManager.savePreferences(token, activity);
                displaySuccessFulLoginToast(activity);
                Log.d("DEBUGGING", "Token received: " + token);
            }

            @Override
            public void onError(Exception e) {
                Log.d("DEBUGGING", "error: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public static void loginToBaFixAPI(String email, String idToken, TokenCallback callback) {
        okHttpClient = new OkHttpClient();

        String json = String.format("{\"email\": \"%s\", \"google_id_token\": \"%s\"}", email,
                                    idToken);

        RequestBody requestBody = RequestBody.create(json, MediaType.get("application/json"));

        Request request = new Request.Builder().url(postAuthLoginURL).post(requestBody).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    Log.d("DEBUGGING", response.toString());
                    JSONObject jsonResponse = new JSONObject(response.body().string());
                    String token = jsonResponse.getString("token");
                    callback.onTokenReceived(token);
                } catch (JSONException e) {
                    callback.onError(e);
                }
            }
        });
    }

    public interface TokenCallback {
        void onTokenReceived(String token);

        void onError(Exception e);
    }
}
