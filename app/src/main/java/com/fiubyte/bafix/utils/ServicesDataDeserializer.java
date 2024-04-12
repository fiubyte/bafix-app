package com.fiubyte.bafix.utils;

import com.fiubyte.bafix.R;
import com.fiubyte.bafix.entities.ServiceData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ServicesDataDeserializer {
    private static ArrayList<ServiceData> services;

    public static ArrayList<ServiceData> deserialize(String servicesList) throws JSONException {
        services = new ArrayList<>();

        JSONArray jsonArray = new JSONArray(servicesList);

        //Update i to 0 when firt item is deleted form BDD
        for (int i = 1; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            // Extraer los campos relevantes del JSON
            String title = jsonObject.getString("title");
            String providerName = jsonObject.getJSONObject("user").getString("name")
                                + " "
                                + jsonObject.getJSONObject("user").getString("surname");

            //Update when max_radius is not null;
            double maxDistance = 0.0;//jsonObject.getJSONObject("user").getDouble("max_radius");

            // Añadir los datos extraídos a la lista de ServiceData
            services.add(new ServiceData(R.drawable.julia, title, maxDistance, providerName));
        }

        return services;
    }
}
