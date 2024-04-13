package com.fiubyte.bafix.utils;

import android.graphics.drawable.Drawable;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.fiubyte.bafix.entities.ServiceData;
import com.fiubyte.bafix.models.DataViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class ServicesDataDeserializer {

    static DataViewModel dataViewModel;

    public static void deserialize(String servicesList, ViewModelStoreOwner viewModelStoreOwner) throws JSONException, IOException {
        ArrayList<ServiceData> services = new ArrayList<>();

        dataViewModel = new ViewModelProvider(viewModelStoreOwner).get(DataViewModel.class);

        JSONArray jsonArray = new JSONArray(servicesList);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            // Extraer los campos relevantes del JSON
            String title = jsonObject.getString("title");
            String providerName = jsonObject.getJSONObject("user").getString("name")
                    + " "
                    + jsonObject.getJSONObject("user").getString("surname");

            double maxDistance = jsonObject.getJSONObject("user").getDouble("max_radius");

            DrawableFromUrlLoader.loadDrawableFromUrl(jsonObject.getString("photo_url"),
                    drawable -> {
                        services.add(new ServiceData(drawable, title, maxDistance, providerName));
                        dataViewModel.updateServices(services);
                    });
        }
    }
}
