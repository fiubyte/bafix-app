package com.fiubyte.bafix.utils;

import android.util.Log;

import com.fiubyte.bafix.entities.ServiceData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ServicesDataDeserializer {
    private static ArrayList<ServiceData> services;

    public static ArrayList<ServiceData> deserialize(String servicesList) throws JSONException,
                                                                                 IOException {
        services = new ArrayList<>();

        Log.d("DEBUGGING", servicesList);

        JSONArray jsonArray = new JSONArray(servicesList);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String title = jsonObject.getString("title");
            String photoURL = jsonObject.getString("photo_url");
            String providerName = jsonObject.getString("user_name")
                    + " "
                    + jsonObject.getString("user_surname");
            DecimalFormat twoDForm = new DecimalFormat("#.#");
            double maxDistance = Double.valueOf(twoDForm.format(jsonObject.getDouble("distance")));
            boolean isAvailable = jsonObject.getBoolean("is_available");
            double latitude = jsonObject.getDouble("service_latitude");
            double longitude = jsonObject.getDouble("service_longitude");
            int providerId = jsonObject.getInt("user_id");

            services.add(new ServiceData(title, photoURL, maxDistance, providerName, providerId,
                                         isAvailable, new GeoPoint(latitude, longitude)));
        }
        return services;
    }
}
