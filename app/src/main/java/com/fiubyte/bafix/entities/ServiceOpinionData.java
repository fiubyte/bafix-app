package com.fiubyte.bafix.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class ServiceOpinionData implements Serializable, Parcelable {
    private String userName;
    private int userRating;
    private String userOpinion;

    private Boolean approved;

    public ServiceOpinionData(String userName, int userRating, String userOpinion, Boolean approved) {
        this.userName = userName;
        this.userRating = userRating;
        this.userOpinion = userOpinion;
        this.approved = approved;
    }

    protected ServiceOpinionData(Parcel in) {
        userName = in.readString();
        userRating = in.readInt();
        userOpinion = in.readString();
        byte tmpApproved = in.readByte();
        approved = tmpApproved == 0 ? null : tmpApproved == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeInt(userRating);
        dest.writeString(userOpinion);
        dest.writeByte((byte) (approved == null ? 0 : approved ? 1 : 2));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ServiceOpinionData> CREATOR = new Creator<ServiceOpinionData>() {
        @Override
        public ServiceOpinionData createFromParcel(Parcel in) {
            return new ServiceOpinionData(in);
        }

        @Override
        public ServiceOpinionData[] newArray(int size) {
            return new ServiceOpinionData[size];
        }
    };

    public String getUserName() {
        return userName;
    }

    public int getUserRating() {
        return userRating;
    }

    public String getUserOpinion() {
        return userOpinion;
    }

    public Boolean isApproved() { return approved; }

}
