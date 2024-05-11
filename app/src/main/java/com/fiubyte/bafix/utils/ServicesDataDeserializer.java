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

            int id = jsonObject.getInt("id");
            String title = jsonObject.getString("title");
            String userPhotoURL = jsonObject.getString("user_profile_photo_url");
            String servicePhotoURL = jsonObject.getString("photo_url");
            String providerName = jsonObject.getString("user_name")
                    + " "
                    + jsonObject.getString("user_surname");
            DecimalFormat twoDForm = new DecimalFormat("#.#");
            double maxDistance = Double.valueOf(twoDForm.format(jsonObject.getDouble("distance")));
            boolean isAvailable = jsonObject.getBoolean("is_available");
            double latitude = jsonObject.getDouble("service_latitude");
            double longitude = jsonObject.getDouble("service_longitude");
            int providerId = jsonObject.getInt("user_id");
            String providerPhone = jsonObject.getString("user_phone_number");
            String description = jsonObject.getString("description");
            String availabilityDays = ServicesDataDeserializer.formatAvailableDays(jsonObject.getString("availability_days"));
            String availabilityTime = jsonObject.getString("availability_time_start") + " - " + jsonObject.getString("availability_time_end");
            boolean isServiceFaved = jsonObject.getBoolean("faved_by_me");

            services.add(new ServiceData(id, title, userPhotoURL, servicePhotoURL, maxDistance,
                                         providerName, providerId,
                                         isAvailable, new GeoPoint(latitude, longitude),
                                         providerPhone, description, availabilityDays,
                                         availabilityTime, isServiceFaved));
        }
        return services;
    }

    private static String formatAvailableDays(String availableDays) {
        String[] days = availableDays.split(",");

        for (int i = 0; i < days.length; i++) {
            String palabra = days[i];
            String abreviatura = palabra.substring(0, 2);
            days[i] = abreviatura;
        }

        String shortenDays = String.join(", ", days);
        return shortenDays;
    }
}
