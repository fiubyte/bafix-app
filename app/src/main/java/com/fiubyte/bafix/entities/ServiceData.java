package com.fiubyte.bafix.entities;

import android.os.Parcel;
import android.os.Parcelable;

import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class ServiceData implements Serializable, Parcelable {
    int id;
    String providerPhotoURL;
    String servicePhotoURL;
    String title;
    double maxDistance;
    String providerName;
    int providerId;
    boolean available;
    GeoPoint geoPoint;
    String providerPhone;
    String description;
    String availabilityDays;
    String availabilityTime;
    Integer ownRating;
    Boolean ownRatingApproved;
    ArrayList<ServiceOpinionData> opinions;
    Double ratingAverage;
    boolean isServiceFaved;

    public ServiceData(int id,
            String title, String providerPhotoURL, String servicePhotoURL, double maxDistance,
            String providerName, int providerId, boolean available, GeoPoint geoPoint, String providerPhone,
            String description, String availabilityDays, String availabilityTime, Integer ownRating,
                       Boolean ownRatingApproved,
                       ArrayList<ServiceOpinionData> opinions, Double ratingAverage, boolean isServiceFaved
                      ) {
        this.id = id;
        this.title = title;
        this.providerPhotoURL = providerPhotoURL;
        this.servicePhotoURL = servicePhotoURL;
        this.maxDistance = maxDistance;
        this.providerName = providerName;
        this.providerId = providerId;
        this.available = available;
        this.geoPoint =  geoPoint;
        this.providerPhone = providerPhone;
        this.description = description;
        this.availabilityDays = availabilityDays;
        this.availabilityTime = availabilityTime;
        this.ownRating = ownRating;
        this.ownRatingApproved = ownRatingApproved;
        this.opinions = opinions;
        this.ratingAverage = ratingAverage;
        this.isServiceFaved = isServiceFaved;
    }

    protected ServiceData(Parcel in) {
        id = in.readInt();
        providerPhotoURL = in.readString();
        servicePhotoURL = in.readString();
        title = in.readString();
        maxDistance = in.readDouble();
        providerName = in.readString();
        providerId = in.readInt();
        available = in.readByte() != 0;
        geoPoint = in.readParcelable(GeoPoint.class.getClassLoader());
        providerPhone = in.readString();
        description = in.readString();
        availabilityDays = in.readString();
        availabilityTime = in.readString();
        if (in.readByte() == 0) {
            ownRating = null;
        } else {
            ownRating = in.readInt();
        }
        byte tmpOwnRatingApproved = in.readByte();
        ownRatingApproved = tmpOwnRatingApproved == 0 ? null : tmpOwnRatingApproved == 1;
        if (in.readByte() == 0) {
            ratingAverage = null;
        } else {
            ratingAverage = in.readDouble();
        }
        isServiceFaved = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(providerPhotoURL);
        dest.writeString(servicePhotoURL);
        dest.writeString(title);
        dest.writeDouble(maxDistance);
        dest.writeString(providerName);
        dest.writeInt(providerId);
        dest.writeByte((byte) (available ? 1 : 0));
        dest.writeParcelable(geoPoint, flags);
        dest.writeString(providerPhone);
        dest.writeString(description);
        dest.writeString(availabilityDays);
        dest.writeString(availabilityTime);
        if (ownRating == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(ownRating);
        }
        dest.writeByte((byte) (ownRatingApproved == null ? 0 : ownRatingApproved ? 1 : 2));
        if (ratingAverage == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(ratingAverage);
        }
        dest.writeByte((byte) (isServiceFaved ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ServiceData> CREATOR = new Creator<ServiceData>() {
        @Override
        public ServiceData createFromParcel(Parcel in) {
            return new ServiceData(in);
        }

        @Override
        public ServiceData[] newArray(int size) {
            return new ServiceData[size];
        }
    };

    public String getProviderPhotoURL() {
        return providerPhotoURL;
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

    public String getProviderPhone() {
        return providerPhone;
    }

    public String getDescription() {
        return description;
    }

    public String getAvailabilityDays() {
        return availabilityDays;
    }

    public String getAvailabilityTime() {
        return availabilityTime;
    }

    public Integer getOwnRating() {
        return ownRating;
    }

    public Boolean getOwnRatingApproved() {
        return ownRatingApproved;
    }

    public ArrayList<ServiceOpinionData> getOpinions() {
        return opinions;
    }

    public int getServiceId() { return id; }

    public Double getRatingAverage() { return ratingAverage; }

    public boolean isServiceFaved() { return isServiceFaved; }
    public void setIsServiceFaved(boolean isFaved) { isServiceFaved = isFaved; }

    public void writeToObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(id);
        out.writeObject(title);
        out.writeObject(providerPhotoURL);
        out.writeObject(servicePhotoURL);
        out.writeDouble(maxDistance);
        out.writeObject(providerName);
        out.writeInt(providerId);
        out.writeBoolean(available);
        out.writeObject(geoPoint);
        out.writeObject(providerPhone);
        out.writeObject(description);
        out.writeObject(availabilityDays);
        out.writeObject(availabilityTime);
        out.writeInt(ownRating);
        out.writeBoolean(ownRatingApproved);
        out.writeObject(opinions);
        out.writeDouble(ratingAverage);
        out.writeBoolean(isServiceFaved);
    }

    @SuppressWarnings("unchecked")
    public ServiceData readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        int id = in.readInt();
        String title = (String) in.readObject();
        String providerPhotoURL = (String) in.readObject();
        String servicePhotoURL = (String) in.readObject();
        double maxDistance = in.readDouble();
        String providerName = (String) in.readObject();
        int providerId = in.readInt();
        boolean available = in.readBoolean();
        GeoPoint geoPoint = (GeoPoint) in.readObject();
        String providerPhone = (String) in.readObject();
        String description = (String) in.readObject();
        String availabilityDays = (String) in.readObject();
        String availabilityTime = (String) in.readObject();
        int ownRating = in.readInt();
        boolean ownRatingApproved = in.readBoolean();
        ArrayList<ServiceOpinionData> opinions = (ArrayList<ServiceOpinionData>) in.readObject();
        Double ratingAverage = in.readDouble();
        boolean isServiceFaved = in.readBoolean();

        return new ServiceData(id, title, providerPhotoURL, servicePhotoURL, maxDistance, providerName,
                               providerId, available, geoPoint, providerPhone, description,
                               availabilityDays, availabilityTime, ownRating, ownRatingApproved, opinions,
                               ratingAverage, isServiceFaved);
    }
}
