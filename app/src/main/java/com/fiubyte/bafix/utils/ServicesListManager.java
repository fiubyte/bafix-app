package com.fiubyte.bafix.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ServicesListManager {
    private static OkHttpClient client;
    private static String getServicesURL = "https://bafix-api.onrender.com/services";

    public interface ServicesListCallback {
        void onServicesListReceived(String response);
        void onError(Exception e);
    }

    public static void retrieveServices(String token, ServicesListCallback callback) {
        client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(getServicesURL)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                callback.onServicesListReceived(response.body().string());
            }
        });
    }
}
