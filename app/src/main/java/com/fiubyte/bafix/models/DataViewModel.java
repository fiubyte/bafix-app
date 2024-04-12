package com.fiubyte.bafix.models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fiubyte.bafix.entities.ServiceData;

import java.util.ArrayList;

public class DataViewModel extends ViewModel {

    private MutableLiveData<ArrayList<ServiceData>> services;

    public MutableLiveData<ArrayList<ServiceData>> getCurrentLoginToken() {
        if (services == null) {
            services = new MutableLiveData<>();
        }
        return services;
    }

    public void updateServices(ArrayList<ServiceData> services) {
        getCurrentLoginToken().setValue(services);
    }
}