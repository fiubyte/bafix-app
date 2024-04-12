package com.fiubyte.bafix.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fiubyte.bafix.R;
import com.fiubyte.bafix.entities.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    SignInButton btSignIn;
    GoogleSignInClient googleSignInClient;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setSupportActionBar(findViewById(R.id.toolbar)); // FIXME: this crashes in some android versions
        btSignIn = findViewById(R.id.bt_sign_in);

        // Initialize sign in options the client-id is copied form google-services.json file
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("404201273327-m5d62vn42rvkupj1utg0akvqrghb0vft.apps.googleusercontent.com")
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(MainActivity.this, googleSignInOptions);
        btSignIn.setOnClickListener((View.OnClickListener) view -> {
            Intent intent = googleSignInClient.getSignInIntent();
            startActivityForResult(intent, 100);
        });

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            // When user already sign in redirect to profile activity
            // startActivity(new Intent(MainActivity.this, ProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            // When request code is equal to 100 initialize task
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (signInAccountTask.isSuccessful()) {
                String s = "Google sign in successful";
                displayToast(s);
                try {
                    GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);
                    if (googleSignInAccount != null) {
                        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                        // googleSignInAccount.getEmail()
                        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // task.getResult().getUser().getEmail()
                                    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                                    mUser.getIdToken(true)
                                            .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                                    if (task.isSuccessful()) {
                                                        String idToken = task.getResult().getToken();
                                                        String email = (String) task.getResult().getClaims().get("email");

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
                                                    } else {
                                                        // Handle error -> task.getException();
                                                    }
                                                }
                                            });


                                    displayToast("Firebase authentication successful");
                                } else {
                                    // When task is unsuccessful display Toast
                                    displayToast("Authentication Failed :" + task.getException().getMessage());
                                }
                            }
                        });
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void displayToast(String s) {
        // Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

}