package com.fiubyte.bafix.entities;

public class ServiceData {
    String servicePhotoURL;
    String title;
    double maxDistance;
    String providerName;

    boolean available;

    public ServiceData(
            String title, String servicePhotoURL, double maxDistance,
            String providerName, boolean available
                      ) {
        this.title = title;
        this.servicePhotoURL = servicePhotoURL;
        this.maxDistance = maxDistance;
        this.providerName = providerName;
        this.available = available;
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

    public boolean isAvailable() {
        return available;
    }
}
