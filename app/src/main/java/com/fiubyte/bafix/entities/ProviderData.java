package com.fiubyte.bafix.entities;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class ProviderData {
    private int id;
    private String name;
    private GeoPoint geoPosition;
    private ArrayList<ServiceData> servicesOffered;

    public ProviderData(int id, String name, GeoPoint location, ArrayList<ServiceData> servicesOffered) {
        this.id = id;
        this.name = name;
        this.geoPosition = location;
        this.servicesOffered = servicesOffered;
    }

    public String getName() {
        return name;
    }

    public GeoPoint getGeoPosition() {
        return geoPosition;
    }

    public ArrayList<ServiceData> getServicesOffered() {
        return servicesOffered;
    }

    public void addService(ServiceData serviceData) {
        this.servicesOffered.add(serviceData);
    }

    public int getId() {
        return id;
    }
}
