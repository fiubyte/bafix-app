package com.fiubyte.bafix.entities;

import java.util.ArrayList;

public class ProviderData {
    String providerName;
    double providerMaxDistance;
    ArrayList<String> providerCategories;
    int providerProfileImage;

    public ProviderData(String providerName, double providerMaxDistance, ArrayList<String> providerCategories, int providerProfileImage) {
        this.providerName = providerName;
        this.providerMaxDistance = providerMaxDistance;
        this.providerCategories = providerCategories;
        this.providerProfileImage = providerProfileImage;
    }

    public String getProviderName() {
        return providerName;
    }

    public double getProviderMaxDistance() {
        return providerMaxDistance;
    }

    public ArrayList<String> getProviderCategories() {
        return providerCategories;
    }

    public int getProviderProfileImage() {
        return providerProfileImage;
    }
}
