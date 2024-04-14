package com.fiubyte.bafix.utils;

import androidx.annotation.NonNull;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ServicesListManager {
    private static OkHttpClient client;
    private static final String getServicesURL = "https://bafix-api.onrender.com/services/filter/";

    public static void retrieveServices(String token, ServicesListCallback callback) {
        client = new OkHttpClient();

        HttpUrl.Builder httpBuilder = HttpUrl.parse(getServicesURL).newBuilder();
        Map<String, String> params = new HashMap<>();
        params.put("ordered_by_distance", "true");
        params.put("ordered_by_availability_now", "true");

        if (params != null) {
            for(Map.Entry<String, String> param : params.entrySet()) {
                httpBuilder.addQueryParameter(param.getKey(),param.getValue());
            }
        }
        Request request = new Request.Builder().url(httpBuilder.build())
                .addHeader("Authorization", "Bearer " + token)
                                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    callback.onServicesListReceived(response.body().string());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public interface ServicesListCallback {
        void onServicesListReceived(String response) throws JSONException;

        void onError(Exception e);
    }
}
