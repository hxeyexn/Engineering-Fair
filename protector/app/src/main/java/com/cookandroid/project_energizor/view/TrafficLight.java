package com.cookandroid.project_energizor.view;

import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

//사용자 위치(스마트 신호등) model
public class TrafficLight {

    public int DeviceNo;
    public float latitude;
    public float longitude;
    public String Time;

    public TrafficLight() {}

    public TrafficLight(int DeviceNo, float latitude, float longitude, String Time) {
        this.DeviceNo = DeviceNo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.Time = Time;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("DeviceNo", DeviceNo);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("Time", Time);

        return result;
    }
}



