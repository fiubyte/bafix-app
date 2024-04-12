package com.fiubyte.bafix.entities;

import java.util.ArrayList;

public class ServiceData {
    int servicePicture;
    String title;
    double maxDistance;
    String providerName;

    public ServiceData(int servicePicture, String title, double maxDistance, String providerName) {
        this.servicePicture = servicePicture;
        this.title = title;
        this.maxDistance = maxDistance;
        this.providerName = providerName;
    }

    public int getServicePicture() {
        return servicePicture;
    }

    public String getTitle() {
        return title;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public String getProviderName() {
        return providerName;
    }
}
