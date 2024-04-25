package com.fiubyte.bafix.entities;

import org.osmdroid.util.GeoPoint;

public class ServiceData {
    String servicePhotoURL;
    String title;
    double maxDistance;
    String providerName;
    int providerId;
    boolean available;
    GeoPoint geoPoint;

    public ServiceData(
            String title, String servicePhotoURL, double maxDistance,
            String providerName, int providerId, boolean available, GeoPoint geoPoint
                      ) {
        this.title = title;
        this.servicePhotoURL = servicePhotoURL;
        this.maxDistance = maxDistance;
        this.providerName = providerName;
        this.providerId = providerId;
        this.available = available;
        this.geoPoint =  geoPoint;
    }

    public String getServicePhotoURL() {return servicePhotoURL;}

    public String getTitle() {
        return title;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public String getProviderName() {
        return providerName;
    }

    public int getProviderId() {
        return providerId;
    }

    public boolean isAvailable() {
        return available;
    }
    public GeoPoint getGeoPoint() {
        return geoPoint;
    }
}
