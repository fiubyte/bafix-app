package com.fiubyte.bafix.models;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fiubyte.bafix.entities.ServiceData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FiltersViewModel extends ViewModel {

    private MutableLiveData<Map<String,String>> filters;

    public MutableLiveData<Map<String,String>> getFilters() {
        if(filters == null) {
            filters = new MutableLiveData<>();
            Map<String, String> initFilters = new HashMap<>();
            initFilters.put("filterByAvailability", "true");
            initFilters.put("distance", "15");
            initFilters.put("categories", "");
            filters.setValue(initFilters);
        }
        return filters;
    }
}