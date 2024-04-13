package com.fiubyte.bafix.entities;

public class ServiceData {
    String servicePhotoURL;
    String title;
    double maxDistance;
    String providerName;

    public ServiceData(
            String title, String servicePhotoURL, double maxDistance,
            String providerName
                      ) {
        this.title = title;
        this.servicePhotoURL = servicePhotoURL;
        this.maxDistance = maxDistance;
        this.providerName = providerName;
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
}
