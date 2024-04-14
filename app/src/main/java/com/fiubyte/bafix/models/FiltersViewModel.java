package com.fiubyte.bafix.models;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fiubyte.bafix.entities.ServiceData;

import java.util.ArrayList;
import java.util.Map;

public class FiltersViewModel extends ViewModel {

    private MutableLiveData<Boolean> availabilityFilter;

    public MutableLiveData<Boolean> getAvailabilityFilter() {
        if(availabilityFilter == null) {
            availabilityFilter = new MutableLiveData<>(false);
        }
        return availabilityFilter;
    }
}