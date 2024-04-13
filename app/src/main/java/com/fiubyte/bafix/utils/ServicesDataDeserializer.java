package com.fiubyte.bafix.utils;

import com.fiubyte.bafix.entities.ServiceData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class ServicesDataDeserializer {
    private static ArrayList<ServiceData> services;

    public static ArrayList<ServiceData> deserialize(String servicesList) throws JSONException,
                                                                                 IOException {
        services = new ArrayList<>();

        JSONArray jsonArray = new JSONArray(servicesList);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String title = jsonObject.getString("title");
            String photoURL = jsonObject.getString("photo_url");
            String providerName = jsonObject.getJSONObject("user").getString("name")
                    + " "
                    + jsonObject.getJSONObject("user").getString("surname");
            double maxDistance = jsonObject.getJSONObject("user").getDouble("max_radius");

            services.add(new ServiceData(title, photoURL, maxDistance, providerName));
        }
        return services;
    }
}
