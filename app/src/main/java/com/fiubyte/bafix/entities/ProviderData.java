package com.fiubyte.bafix.entities;

import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class ProviderData implements Serializable {
    private int id;
    private String name;
    private GeoPoint geoPosition;
    private ArrayList<ServiceData> servicesOffered;
    private String providerPhotoURL;

    private String phone;

    private double distance;

    public ProviderData(int id, String name, GeoPoint location, ArrayList<ServiceData> servicesOffered, double distance, String providerPhotoURL, String phone) {
        this.id = id;
        this.name = name;
        this.geoPosition = location;
        this.servicesOffered = servicesOffered;
        this.distance = distance;
        this.providerPhotoURL = providerPhotoURL;
        this.phone = phone;
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

    public double getDistance() { return distance; }

    public String getProviderPhotoURL() {
        return providerPhotoURL;
    }

    public String getPhone() {
        return phone;
    }

    public void writeToObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeInt(id);
        out.writeObject(name);
        out.writeObject(geoPosition);
        out.writeObject(servicesOffered);
        out.writeObject(distance);
        out.writeObject(providerPhotoURL);
        out.writeObject(phone);
    }

    @SuppressWarnings("unchecked")
    public ProviderData readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        int id = in.readInt();
        String name = (String) in.readObject();
        GeoPoint geoPosition = (GeoPoint) in.readObject();
        ArrayList<ServiceData> servicesOffered = (ArrayList<ServiceData>) in.readObject();
        double distance = in.readDouble();
        String providerPhotoURL = (String) in.readObject();
        String phone = (String) in.readObject();
        return new ProviderData(id, name, geoPosition, servicesOffered, distance, providerPhotoURL, phone);
    }
}
