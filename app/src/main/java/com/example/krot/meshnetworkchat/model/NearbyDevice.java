package com.example.krot.meshnetworkchat.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.hypelabs.hype.Instance;

/**
 * Created by Krot on 3/25/18.
 */

public class NearbyDevice {

    @NonNull
    private final String deviceName;

    @NonNull
    private final Instance instance;

    public NearbyDevice(@NonNull String deviceName, @NonNull Instance instance) {
        this.deviceName = deviceName;
        this.instance = instance;
    }

    @NonNull
    public String getDeviceName() {
        return deviceName;
    }

    @NonNull
    public Instance getInstance() {
        return instance;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof  NearbyDevice) {
            NearbyDevice currentNearbyDevice = (NearbyDevice) obj;
            Log.i("WTF", "equals: " + (   (this.getDeviceName().equals(currentNearbyDevice.getDeviceName()))
                    && (this.getInstance() == currentNearbyDevice.getInstance())));
            return (   (this.getDeviceName().equals(currentNearbyDevice.getDeviceName()))
                    && (this.getInstance() == currentNearbyDevice.getInstance()));
        } else {
            Log.i("WTF", "equals: false");
            return false;
        }
    }
}
