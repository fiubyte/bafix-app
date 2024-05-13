package com.fiubyte.bafix.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public enum ServiceTab implements Parcelable {
    INFORMATION,
    OPINIONS;

    // FIXME: this is not a real implementation of Parcelable :/
    public static final Creator<ServiceTab> CREATOR = new Creator<ServiceTab>() {
        @Override
        public ServiceTab createFromParcel(Parcel in) {
            return ServiceTab.INFORMATION;
        }

        @Override
        public ServiceTab[] newArray(int size) {
            return new ServiceTab[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(ServiceTab.INFORMATION.ordinal());
    }
}
