package com.fiubyte.bafix.utils;

import android.util.Log;

import com.fiubyte.bafix.entities.ServiceData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
            services.add(new ServiceData(title, photoURL, maxDistance, providerName, isAvailable));
        }
        return services;
    }
}
