package com.fiubyte.bafix.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.StrictMode;

import com.fiubyte.bafix.entities.LoginResult;
import com.fiubyte.bafix.fragments.RegisterFragment;
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
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthManager {

    private static final String serverClientId =
            "404201273327-m5d62vn42rvkupj1utg0akvqrghb0vft.apps.googleusercontent.com";

    public static final int RC_SIGN_IN = 100;
    static GoogleSignInClient googleSignInClient;

    static FirebaseAuth firebaseAuth;

    public static void signInWithGoogle(RegisterFragment registerFragment) {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(serverClientId)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(registerFragment.requireActivity(), googleSignInOptions);

        firebaseAuth = FirebaseAuth.getInstance();

        Intent intent = googleSignInClient.getSignInIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        registerFragment.startActivityForResult(intent, RC_SIGN_IN);
        registerFragment.requireActivity().overridePendingTransition(0, 0);
    }

    public static void handleSuccessFullGoogleSignIn(
            Task<GoogleSignInAccount> signInAccountTask
            , Activity activity) {
        try {
            GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);
            if (googleSignInAccount != null) {
                AuthManager.authenticateWithFirebase(googleSignInAccount, activity);
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    public static boolean userAlreadySignedIn(Activity activity) {
        return GoogleSignIn.getLastSignedInAccount(activity) != null;
    }

    public static void authenticateWithFirebase(GoogleSignInAccount googleSignInAccount, Activity activity) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(authCredential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                        mUser.getIdToken(true)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        String idToken = task1.getResult().getToken();
                                        String email = (String) task1.getResult().getClaims().get("email");
                                        exchangeGoogleTokenForCustomToken(email, idToken);
                                    } else {
                                        // Handle error -> task.getException();
                                    }
                                });
                    } else {
                        // When task is unsuccessful display Toast
                        displayToast(activity, "Authentication Failed :" + task.getException().getMessage());
                    }
                });
    }

    private static void exchangeGoogleTokenForCustomToken(String email, String idToken) {
        // This is where you handle exchanging the Google token with your server
        // You can make this method async if needed
        // For now, I'll keep it synchronous for simplicity
        try {
            // FIXME: this is really bad, it blocks the main thread. Do it async
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            String json = String.format("{\"email\": \"%s\", \"google_id_token\": \"%s\"}", email, idToken);
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url("https://bafix-api.onrender.com/auth/login")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                Gson gson = new Gson();
                LoginResult token = gson.fromJson(response.body().string(), LoginResult.class);
                System.out.println("Token: " + token); // Use this as header Authorization: Bearer <token>
                // TODO: redirect to somewhere nice
                // startActivity(new Intent(MainActivity.this, ProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void displayToast(Activity activity, String message) {
        // Display Toast
    }
}
