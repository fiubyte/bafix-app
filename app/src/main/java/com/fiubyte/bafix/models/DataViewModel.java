package com.fiubyte.bafix.models;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fiubyte.bafix.entities.ServiceData;

import java.util.ArrayList;
import java.util.Map;

public class DataViewModel extends ViewModel {

    private MutableLiveData<ArrayList<ServiceData>> services;
    private MutableLiveData<Map<String, Double>> location;
    private MutableLiveData<String> token;

    public MutableLiveData<ArrayList<ServiceData>> getCurrentServices() {
        if (services == null) {
            services = new MutableLiveData<>();
        }
        return services;
    }

    public MutableLiveData<Map<String, Double>> getCurrentLocation() {
        if (location == null) {
            location = new MutableLiveData<>();
        }
        return location;
    }

    public MutableLiveData<String> getToken() {
        if (token == null) {
            token = new MutableLiveData<>();
        }
        return token;
    }

    public void updateServices(ArrayList<ServiceData> services) {
        Log.d("SERVICES", services.toString());
        getCurrentServices().setValue(services);
    }
}