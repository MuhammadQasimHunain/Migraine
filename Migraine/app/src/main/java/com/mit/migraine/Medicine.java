package com.mit.migraine;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cs on 8/15/2017.
 */

public class Medicine implements Parcelable {
    String med_name;
    String med_strength;

    public Medicine() {

    }

    public Medicine(String name, String strength) {
        this.med_name = name;
        this.med_strength = strength;
    }

    protected Medicine(Parcel in) {
        med_name = in.readString();
        med_strength = in.readString();
    }

    public static final Creator<Medicine> CREATOR = new Creator<Medicine>() {
        @Override
        public Medicine createFromParcel(Parcel in) {
            return new Medicine(in);
        }

        @Override
        public Medicine[] newArray(int size) {
            return new Medicine[size];
        }
    };

    public String getMed_name() {
        return this.med_name;
    }

    public String getMed_strength() {
        return this.med_strength;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(med_name);
        dest.writeString(med_strength);
    }
}
