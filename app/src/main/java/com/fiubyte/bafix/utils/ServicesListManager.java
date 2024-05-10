package com.fiubyte.bafix.utils;

import static java.lang.String.format;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServicesListManager {
    private static OkHttpClient client = new OkHttpClient();
    private static final String getServicesURL = "https://bafix-api.onrender.com/services/filter/";
    private static final String FAV_SERVICE_URL = "https://bafix-api.onrender.com/services/%s/fav";
    private static final String UNFAV_SERVICE_URL = "https://bafix-api.onrender.com/services/%s/unfav";

    public static void retrieveServices(String token, Map<String,String> filters, Map<String, Double> userLocation, ServicesListCallback callback) throws UnsupportedEncodingException {
        HttpUrl.Builder httpBuilder = HttpUrl.parse(getServicesURL).newBuilder();

        httpBuilder.addQueryParameter("ordered_by_distance", "true");
        httpBuilder.addQueryParameter("ordered_by_availability_now", "true");
        httpBuilder.addQueryParameter("user_lat", userLocation.get("latitude").toString());
        httpBuilder.addQueryParameter("user_long", userLocation.get("longitude").toString());
        httpBuilder.addQueryParameter("availability_filter", String.valueOf(Boolean.parseBoolean(filters.get("filterByAvailability"))));
        httpBuilder.addQueryParameter("distance_filter", String.valueOf(Float.parseFloat(filters.get("distance"))));

        if (filters.get("categories") != null && !filters.get("categories").isEmpty()) {
            List<String> categoryIds = Arrays.stream(filters.get("categories").split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());

            // Agregar cada valor individual al parámetro category_ids
            for (String categoryId : categoryIds) {
                httpBuilder.addQueryParameter("category_ids", categoryId);
            }
        }

        Request request = new Request.Builder().url(httpBuilder.build())
                .addHeader("Authorization", "Bearer " + token)
                                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("DEBUGGING", "Failure");
                callback.onError();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    try {
                        callback.onServicesListReceived(response.body().string());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    callback.onError();
                    Log.d("DEBUGGING", "not succesful response");
                    Log.d("DEBUGGING", "response: " + response);
                }
            }
        });
    }

    public static void favService(String token, int serviceId, ServiceRateCallback callback) {
        HttpUrl.Builder httpBuilder = HttpUrl.parse(format(FAV_SERVICE_URL, serviceId)).newBuilder();
        Request request = new Request.Builder().url(httpBuilder.build())
                .addHeader("Authorization", "Bearer " + token)
                .post(RequestBody.create(null, ""))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("ERROR", "Error on POST /services/<id>/fav", e);
                callback.onError();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        callback.onSuccess(response.body().string());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    callback.onError();
                    Log.e("ERROR", "Error on POST /services/<id>/fav: " + response);
                }
            }
        });
    }

    public static void unfavService(String token, int serviceId, ServiceRateCallback callback) {
        HttpUrl.Builder httpBuilder = HttpUrl.parse(format(UNFAV_SERVICE_URL, serviceId)).newBuilder();
        Request request = new Request.Builder().url(httpBuilder.build())
                .addHeader("Authorization", "Bearer " + token)
                .post(RequestBody.create(null, ""))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("ERROR", "Error on POST /services/<id>/unfav", e);
                callback.onError();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        callback.onSuccess(response.body().string());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    callback.onError();
                    Log.e("ERROR", "Error on POST /services/<id>/unfav: " + response);
                }
            }
        });
    }

    public interface ServicesListCallback {
        void onServicesListReceived(String response) throws JSONException;

        void onError();
    }

    public interface ServiceRateCallback {
        void onSuccess(String response) throws JSONException;

        void onError();
    }
}
