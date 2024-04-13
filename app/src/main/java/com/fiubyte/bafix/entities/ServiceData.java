package com.fiubyte.bafix.entities;

import android.graphics.drawable.Drawable;

public class ServiceData {
    Drawable servicePicture;
    String title;
    double maxDistance;
    String providerName;

    public ServiceData(Drawable servicePicture, String title, double maxDistance, String providerName) {
        this.servicePicture = servicePicture;
        this.title = title;
        this.maxDistance = maxDistance;
        this.providerName = providerName;
    }

    public Drawable getServicePicture() {
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
